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
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_mailer.exception.EmailException;
import de.egladil.web.commons_mailer.exception.InvalidMailAddressException;
import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.mail.AdminMailService;
import de.egladil.web.mk_gateway.domain.newsletters.Newsletter;
import de.egladil.web.mk_gateway.domain.newsletters.NewsletterRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.NewsletterauslieferungenRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * NewsletterversandService
 */
@ApplicationScoped
public class NewsletterversandService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterversandService.class);

	@Inject
	NewsletterauslieferungenRepository newsletterAuslieferungenRepository;

	@Inject
	NewsletterRepository newsletterRepository;

	@Inject
	AdminMailService mailService;

	@Inject
	NewsletterVersandauftragService newsletterAuftraegeService;

	/**
	 * Prüft, ob es einen laufenden Newsletterversand gibt und ob es für diesen eine wartende Auslieferung gibt. Falls ja, wird
	 * versendet und der Status dieser Ausliefreung über IN_PROGRESS auf COMPLETED gesetzt. Außerdem wird die Versandinfo
	 * aktualisiert.
	 */
	public void checkAndSend() {

		LOGGER.info("pruefe Warteschlange");

		NewsletterAuslieferung nextAuslieferung = getFirstPendingAuslieferung();

		if (nextAuslieferung == null) {

			LOGGER.info("keine Auslieferung in der Warteschlange. Tue nix.");
			return;
		}

		Pair<Versandauftrag, Newsletter> versandauftragAndNewsletter = newsletterAuftraegeService
			.getVersandauftragAndNewsletterWithVersandauftragID(nextAuslieferung.getVersandauftragId());

		Versandauftrag versandauftrag = versandauftragAndNewsletter.getLeft();
		Newsletter newsletter = versandauftragAndNewsletter.getRight();

		if (newsletter == null) {

			if (versandauftrag != null) {

				markVersandinfoCompleted(versandauftrag, StatusAuslieferung.ERRORS);

				throw new MkGatewayRuntimeException(
					"Datenmatsch: es gibt einen Versandauftrag und NewsletterAuslieferung mit newsletterID="
						+ versandauftrag.newsletterID()
						+ "', aber keinen Newsletter mehr mit dieser ID");
			} else {

				throw new MkGatewayRuntimeException(
					"Datenmatsch: es gibt NewsletterAuslieferungen ohne Versandauftrag und newletterID: NewsletterAuslieferung.ID="
						+ nextAuslieferung.getIdentifier());
			}

		}

		if (nextAuslieferung.getStatus() == StatusAuslieferung.IN_PROGRESS) {

			LOGGER.debug("Auslieferung {} läuft gerade. Tue nix", nextAuslieferung.getIdentifier());
			return;
		}

		if (StatusAuslieferung.WAITING == versandauftrag.getStatus()) {

			String begonnenAm = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
			versandauftrag.setStatus(StatusAuslieferung.IN_PROGRESS);
			versandauftrag.setVersandBegonnenAm(begonnenAm);

			newsletterAuftraegeService.versandauftragSpeichern(versandauftrag);
		}

		boolean fehler = false;
		List<String> empfaenger = Arrays.asList(nextAuslieferung.getEmpfaenger());

		try {

			markAuslieferungStarted(nextAuslieferung);

			LOGGER.info("starte mit Versand an Auslieferung {}: anzahl Empfänger={}", nextAuslieferung.getIdentifier(),
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

			nextAuslieferung.setStatus(fehler ? StatusAuslieferung.ERRORS : StatusAuslieferung.COMPLETED);
			this.updateStatusAuslieferung(nextAuslieferung);
			this.checkAndUpdateStatusVersandauftrag(versandauftrag, nextAuslieferung.getEmpfaenger().length);

		}
	}

	void markVersandinfoCompleted(final Versandauftrag versandinfo, final StatusAuslieferung statusVersandauftrag) {

		versandinfo.setStatus(statusVersandauftrag);
		versandinfo.setVersandBeendetAm(CommonTimeUtils.format(CommonTimeUtils.now()));
		newsletterAuftraegeService.versandauftragSpeichern(versandinfo);
	}

	@Transactional
	void markAuslieferungStarted(final NewsletterAuslieferung auslieferung) {

		auslieferung.setStatus(StatusAuslieferung.IN_PROGRESS);
		newsletterAuslieferungenRepository.updateAuslieferung(auslieferung);
	}

	@Transactional
	void updateStatusAuslieferung(final NewsletterAuslieferung auslieferung) {

		newsletterAuslieferungenRepository.updateAuslieferung(auslieferung);

		if (StatusAuslieferung.ERRORS == auslieferung.getStatus()) {

			LOGGER.info("Versand Auslieferung {} mit Fehlern beendet", auslieferung.getIdentifier());
		} else {

			LOGGER.warn("Versand Auslieferung {} fehlerfrei beendet", auslieferung.getIdentifier());
		}
	}

	@Transactional
	void checkAndUpdateStatusVersandauftrag(final Versandauftrag versandauftrag, final int anzahlEmpfaenger) {

		List<NewsletterAuslieferung> allWithVersandauftrag = newsletterAuslieferungenRepository
			.findAllWithVersandauftrag(versandauftrag.identifier());

		Optional<NewsletterAuslieferung> optPending = allWithVersandauftrag.stream().filter(a -> !a.getStatus().isCompleted())
			.findFirst();

		if (optPending.isEmpty()) {

			Optional<NewsletterAuslieferung> optWithErrors = allWithVersandauftrag.stream()
				.filter(a -> a.getStatus() == StatusAuslieferung.ERRORS).findFirst();

			versandauftrag.setStatus(optWithErrors.isEmpty() ? StatusAuslieferung.COMPLETED : StatusAuslieferung.ERRORS);

			String beendetAm = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
			versandauftrag.setVersandBeendetAm(beendetAm);
		}

		versandauftrag.setAnzahlAktuellVersendet(versandauftrag.anzahlAktuellVersendet() + anzahlEmpfaenger);
		newsletterAuftraegeService.versandauftragSpeichern(versandauftrag);
	}

	NewsletterAuslieferung getFirstPendingAuslieferung() {

		List<Versandauftrag> offeneVersandauftraege = newsletterAuftraegeService.findNichtBeendeteVersandauftraege();

		for (Versandauftrag auftrag : offeneVersandauftraege) {

			List<NewsletterAuslieferung> auslieferungen = newsletterAuslieferungenRepository
				.findAllPendingWithVersandauftrag(auftrag.identifier());

			if (auslieferungen.isEmpty()) {

				return null;
			}

			return auslieferungen.get(0);

		}

		return null;
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
