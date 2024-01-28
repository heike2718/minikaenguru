// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_mailer.exception.EmailException;
import de.egladil.web.commons_mailer.exception.InvalidMailAddressException;
import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
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
	NewsletterAuftraegeService newsletterAuftraegeService;

	/**
	 * Prüft, ob es einen laufenden Newsletterversand gibt und ob es für diesen eine wartende Auslieferung gibt. Falls ja, wird
	 * versendet und der Status dieser Ausliefreung über IN_PROGRESS auf COMPLETED gesetzt. Außerdem wird die Versandinfo
	 * aktualisiert.
	 */
	public void checkAndSend() {

		LOGGER.info("pruefe Warteschlange");

		// werden aufsteigend nach dem Datum sortiert, an dem sie erstellt wurden, d.h, die älteste ist die erste.
		List<Versandauftrag> offeneVersandauftraege = newsletterAuftraegeService.findNichtBeendeteVersandauftraege();

		if (offeneVersandauftraege.isEmpty()) {

			LOGGER.debug("keine Newsletterauftrag in der Warteschlange.");
			return;
		}

		Versandauftrag versandauftrag = offeneVersandauftraege.get(0);

		Identifier newsletterID = versandauftrag.newsletterID();

		Optional<Newsletter> optNewsletter = newsletterRepository.ofId(newsletterID);

		if (optNewsletter.isEmpty()) {

			markVersandinfoCompleted(versandauftrag, StatusAuslieferung.ERRORS);

			throw new MkGatewayRuntimeException(
				"Datenmatsch: es gibt eine Versandauftrag und NewsletterAuslieferung mit newsletterID=" + newsletterID
					+ "', aber keinen Newsletter mehr mit dieser ID");
		}

		if (StatusAuslieferung.WAITING == versandauftrag.getStatus()) {

			versandauftrag.setStatus(StatusAuslieferung.IN_PROGRESS);
			newsletterAuftraegeService.versandauftragSpeichern(versandauftrag);
		}

		List<NewsletterAuslieferung> auslieferungen = newsletterAuslieferungenRepository
			.findAllWithVersandauftrag(versandauftrag.identifier());

		if (StatusAuslieferung.IN_PROGRESS == versandauftrag.getStatus()) {

			long anzahlOffeneAuslieferungen = auslieferungen.stream()
				.filter(a -> StatusAuslieferung.IN_PROGRESS == a.getStatus() || StatusAuslieferung.WAITING == a.getStatus())
				.count();

			if (anzahlOffeneAuslieferungen == 0) {

				Optional<NewsletterAuslieferung> optWithErrors = auslieferungen.stream()
					.filter(a -> StatusAuslieferung.ERRORS == a.getStatus()).findFirst();

				StatusAuslieferung statusVersandauftrag = optWithErrors.isEmpty() ? StatusAuslieferung.COMPLETED
					: StatusAuslieferung.ERRORS;

				markVersandinfoCompleted(versandauftrag, statusVersandauftrag);
				LOGGER.info("Versandinfo {} als completed markiert: status={}", versandauftrag.identifier(), statusVersandauftrag);
				return;

			}

		}

		Newsletter newsletter = optNewsletter.get();
		NewsletterAuslieferung auslieferung = auslieferungen.get(0);
		List<String> empfaenger = Arrays.asList(auslieferung.getEmpfaenger());

		boolean fehler = false;

		if (StatusAuslieferung.IN_PROGRESS == auslieferung.getStatus()) {

			LOGGER.debug("Auslieferung {} läuft gerade. Tue nix", auslieferung.getIdentifier());
			return;
		}

		try {

			markAuslieferungStarted(auslieferung);

			LOGGER.info("starte mit Versand an Auslieferung {}: anzahl Empfänger={}", auslieferung.getIdentifier(),
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

			auslieferung.setStatus(fehler ? StatusAuslieferung.ERRORS : StatusAuslieferung.COMPLETED);
			this.updateStatus(versandauftrag, auslieferung, empfaenger.size());
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
	void updateStatus(final Versandauftrag versandinformation, final NewsletterAuslieferung auslieferung, final int anzahlEmpfaenger) {

		versandinformation.setAnzahlAktuellVersendet(versandinformation.anzahlAktuellVersendet() + anzahlEmpfaenger);
		newsletterAuftraegeService.versandauftragSpeichern(versandinformation);

		newsletterAuslieferungenRepository.updateAuslieferung(auslieferung);

		if (StatusAuslieferung.ERRORS == auslieferung.getStatus()) {

			LOGGER.info("Versand Auslieferung {} mit Fehlern beendet", auslieferung.getIdentifier());
		} else {

			LOGGER.warn("Versand Auslieferung {} fehlerfrei beendet", auslieferung.getIdentifier());
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
