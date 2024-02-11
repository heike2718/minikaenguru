// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_mailer.exception.EmailException;
import de.egladil.web.commons_mailer.exception.InvalidMailAddressException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.mail.AdminMailService;
import de.egladil.web.mk_gateway.domain.newsletters.Newsletter;
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

	/**
	 * Prüft, ob es eine wartende Auslieferung gibt. Falls ja, wird
	 * versendet und der Status dieser Ausliefreung über IN_PROGRESS auf COMPLETED gesetzt. Außerdem wird der Versandauftrag
	 * aktualisiert.
	 */
	public void processNextAuslieferung() {

		LOGGER.info("pruefe Warteschlange");

		NewsletterAuslieferung currentAuslieferung = auslieferungPicker.getFirstPendingAuslieferung();

		if (currentAuslieferung == null) {

			LOGGER.info("keine Auslieferung in der Warteschlange. Tue nix.");
			return;
		}

		Pair<Versandauftrag, Newsletter> versandauftragAndNewsletter = newsletterAuftraegeService
			.getVersandauftragAndNewsletterWithVersandauftragID(currentAuslieferung.getVersandauftragId());

		Versandauftrag versandauftrag = versandauftragAndNewsletter.getLeft();
		Newsletter newsletter = versandauftragAndNewsletter.getRight();

		if (newsletter == null) {

			if (versandauftrag != null) {

				versandauftragStatusUpdater.markVersandauftragCompletedWithDataError(versandauftrag);

				throw new MkGatewayRuntimeException(
					"Datenmatsch: es gibt einen Versandauftrag und NewsletterAuslieferung mit newsletterID="
						+ versandauftrag.newsletterID()
						+ "', aber keinen Newsletter mehr mit dieser ID");
			} else {

				throw new MkGatewayRuntimeException(
					"Datenmatsch: es gibt NewsletterAuslieferungen ohne Versandauftrag und newletterID: NewsletterAuslieferung.ID="
						+ currentAuslieferung.getIdentifier());
			}

		}

		if (currentAuslieferung.getStatus() == StatusAuslieferung.IN_PROGRESS) {

			LOGGER.debug("Auslieferung {} läuft gerade. Tue nix", currentAuslieferung.getIdentifier());
			return;
		}

		if (versandauftrag.getStatus() == StatusAuslieferung.WAITING) {

			versandauftragStatusUpdater.markVersandauftragStarted(versandauftrag);
		}

		boolean fehler = false;
		List<String> empfaenger = Arrays.asList(currentAuslieferung.getEmpfaenger());

		try {

			auslieferungStatusUpdater.markAuslieferungStarted(currentAuslieferung);

			LOGGER.info("starte mit Versand an Auslieferung {}: anzahl Empfänger={}", currentAuslieferung.getIdentifier(),
				empfaenger.size());

			this.sendeMail(newsletter, empfaenger);

		} catch (InvalidMailAddressException e) {

			LOGGER.warn(e.getMessage());
			fehler = true;

		} catch (EmailException e) {

			LOGGER.error(e.getMessage());
			fehler = true;

		} catch (Exception e) {

			LOGGER.error("Unerwartete Exception beim Mailversand: {}", e.getMessage(), e);
			fehler = true;

		} finally {

			currentAuslieferung.setStatus(fehler ? StatusAuslieferung.ERRORS : StatusAuslieferung.COMPLETED);
			auslieferungStatusUpdater.markAuslieferungCompleted(currentAuslieferung);
			versandauftragStatusUpdater.updateStatusVersandauftrag(versandauftrag, currentAuslieferung.getEmpfaenger().length);

		}
	}

	/**
	 * @param versandauftrag
	 */
	void markVersandauftragStarted(final Versandauftrag versandauftrag) {

		String begonnenAm = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
		versandauftrag.setStatus(StatusAuslieferung.IN_PROGRESS);
		versandauftrag.setVersandBegonnenAm(begonnenAm);

		newsletterAuftraegeService.versandauftragSpeichern(versandauftrag);
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
