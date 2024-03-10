// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.infrastructure.persistence.impl.NewsletterauslieferungenRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * VersandauftragStatusUpdater
 */
@ApplicationScoped
public class VersandauftragStatusUpdater {

	private static final String DATE_TIME_FORMAT_PATTERN = "dd.MM.yyyy HH:mm:ss";

	@Inject
	NewsletterauslieferungenRepository newsletterAuslieferungenRepository;

	@Inject
	NewsletterVersandauftragService newsletterAuftraegeService;

	/**
	 * @param versandauftrag
	 */
	@Transactional
	public void markVersandauftragStarted(final Versandauftrag versandauftrag) {

		String begonnenAm = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
		versandauftrag.setStatus(StatusAuslieferung.IN_PROGRESS);
		versandauftrag.setVersandBegonnenAm(begonnenAm);

		newsletterAuftraegeService.versandauftragSpeichern(versandauftrag);
	}

	@Transactional
	void updateStatusVersandauftrag(final Versandauftrag versandauftrag, final int anzahlEmpfaenger) {

		List<NewsletterAuslieferung> allWithVersandauftrag = newsletterAuslieferungenRepository
			.findAllWithVersandauftrag(versandauftrag.identifier());

		Optional<NewsletterAuslieferung> optPending = allWithVersandauftrag.stream().filter(a -> !a.getStatus().isCompleted())
			.findFirst();

		if (optPending.isEmpty()) {

			Optional<NewsletterAuslieferung> optWithErrors = allWithVersandauftrag.stream()
				.filter(a -> a.getStatus() == StatusAuslieferung.ERRORS).findFirst();

			versandauftrag.setStatus(optWithErrors.isEmpty() ? StatusAuslieferung.COMPLETED : StatusAuslieferung.ERRORS);

			String beendetAm = new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN).format(new Date());
			versandauftrag.setVersandBeendetAm(beendetAm);
		}

		versandauftrag.setAnzahlAktuellVersendet(versandauftrag.anzahlAktuellVersendet() + anzahlEmpfaenger);
		newsletterAuftraegeService.versandauftragSpeichern(versandauftrag);
	}

	@Transactional
	public void markVersandauftragCompletedWithDataError(final Versandauftrag versandauftrag) {

		versandauftrag.setStatus(StatusAuslieferung.ERRORS);
		newsletterAuftraegeService.versandauftragSpeichern(versandauftrag);
	}
}
