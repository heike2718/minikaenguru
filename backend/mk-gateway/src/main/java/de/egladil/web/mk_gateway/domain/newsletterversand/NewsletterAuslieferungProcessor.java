// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_mailer.exception.EmailException;
import de.egladil.web.commons_mailer.exception.InvalidMailAddressException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.mail.AdminMailService;
import de.egladil.web.mk_gateway.domain.newsletters.Newsletter;
import de.egladil.web.mk_gateway.domain.newsletterversand.event.NewsletterversandFailed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * NewsletterAuslieferungProcessor
 */
@ApplicationScoped
public class NewsletterAuslieferungProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterAuslieferungProcessor.class);

	@Inject
	AuslieferungPicker auslieferungPicker;

	@Inject
	AuslieferungStatusUpdater auslieferungStatusUpdater;

	@Inject
	VersandauftragStatusUpdater versandauftragStatusUpdater;

	@Inject
	NewsletterVersandauftragService newsletterAuftraegeService;

	@Inject
	AdminMailService mailService;

	@Inject
	DomainEventHandler domainEventHandler;

	/**
	 * Prüft, ob es eine wartende Auslieferung gibt. Falls ja, wird
	 * versendet und der Status dieser Ausliefreung über IN_PROGRESS auf COMPLETED gesetzt. Außerdem wird der Versandauftrag
	 * aktualisiert.
	 */
	public void processNextAuslieferung() {

		LOGGER.info("pruefe Warteschlange");

		NewsletterAuslieferung pendingAuslieferung = auslieferungPicker.getNextPendingAuslieferung();

		if (pendingAuslieferung == null) {

			LOGGER.info("keine Auslieferung in der Warteschlange. Tue nix.");
			return;
		}

		Pair<Versandauftrag, Newsletter> versandauftragAndNewsletter = null;

		try {

			versandauftragAndNewsletter = newsletterAuftraegeService
				.getVersandauftragAndNewsletterWithVersandauftragID(pendingAuslieferung.getVersandauftragId());
		} catch (MkGatewayRuntimeException e) {

			LOGGER.error(e.getMessage());

			pendingAuslieferung.setStatus(StatusAuslieferung.ERRORS);
			auslieferungStatusUpdater.markAuslieferungCompleted(pendingAuslieferung);

			throw e;
		}

		if (pendingAuslieferung.getStatus() != StatusAuslieferung.WAITING) {

			LOGGER.debug("Auslieferung {} hat Status {}. Tue nix", pendingAuslieferung.getIdentifier(),
				pendingAuslieferung.getStatus());
			return;
		}

		boolean fehler = false;
		List<String> empfaenger = Arrays.asList(pendingAuslieferung.getEmpfaenger());

		Versandauftrag versandauftrag = versandauftragAndNewsletter.getLeft();

		if (versandauftrag.getStatus() == StatusAuslieferung.WAITING) {

			versandauftragStatusUpdater.markVersandauftragStarted(versandauftrag);
		}

		auslieferungStatusUpdater.markAuslieferungStarted(pendingAuslieferung);

		LOGGER.info("starte mit Versand an Auslieferung {}: anzahl Empfänger={}", pendingAuslieferung.getIdentifier(),
			empfaenger.size());

		try {

			this.sendeMail(versandauftragAndNewsletter.getRight(), empfaenger);

		} catch (InvalidMailAddressException e) {

			String msg = "Mail konnte nicht an alle Empfänger versendet werden";

			NewsletterversandFailed versandFailedEventPayload = new NewsletterversandFailed()
				.withUuid(pendingAuslieferung.getIdentifier().identifier())
				.withInvalidMailaddresses(e.getAllInvalidAdresses())
				.withMessage(msg)
				.withValidSentAddresses(e.getAllValidSentAddresses())
				.withValidUnsentAddresses(e.getAllValidUnsentAddresses());

			domainEventHandler.handleEvent(versandFailedEventPayload);

			fehler = true;

		} catch (EmailException e) {

			LOGGER.error(e.getMessage());
			fehler = true;

		} catch (Exception e) {

			LOGGER.error("Unerwartete Exception beim Mailversand: {}", e.getMessage(), e);
			fehler = true;

		} finally {

			pendingAuslieferung.setStatus(fehler ? StatusAuslieferung.ERRORS : StatusAuslieferung.COMPLETED);
			auslieferungStatusUpdater.markAuslieferungCompleted(pendingAuslieferung);
			versandauftragStatusUpdater.updateStatusVersandauftrag(versandauftrag, pendingAuslieferung.getEmpfaenger().length);
		}
	}

	void sendeMail(final Newsletter newsletter, final List<String> gruppe) {

		String text = getCompleteText(newsletter);

		DefaultEmailDaten maildaten = new DefaultEmailDaten();
		maildaten.setBetreff(newsletter.betreff());
		maildaten.setText(text);
		maildaten.addHiddenEmpfaenger(gruppe);

		this.mailService.sendMail(maildaten);

		LOGGER.info("Mail an {} Empfaenger versendet", gruppe.size());
	}

	private String getCompleteText(final Newsletter newsletter) {

		try (InputStream in = getClass().getResourceAsStream("/mails/mailsuffix.txt"); StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName(MkGatewayFileUtils.DEFAULT_ENCODING));

			return newsletter.text() + sw.toString();

		} catch (IOException e) {

			LOGGER.warn("Standardmailende konnte nicht geladen werden: " + e.getMessage(), e);
			return newsletter.text();
		}

	}

}
