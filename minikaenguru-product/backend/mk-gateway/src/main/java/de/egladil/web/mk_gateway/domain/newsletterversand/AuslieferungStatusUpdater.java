// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.infrastructure.persistence.impl.NewsletterauslieferungenRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * AuslieferungStatusUpdater
 */
@ApplicationScoped
public class AuslieferungStatusUpdater {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuslieferungStatusUpdater.class);

	@Inject
	NewsletterauslieferungenRepository newsletterAuslieferungenRepository;

	@Transactional
	public void markAuslieferungStarted(final NewsletterAuslieferung auslieferung) {

		auslieferung.setStatus(StatusAuslieferung.IN_PROGRESS);
		newsletterAuslieferungenRepository.updateAuslieferung(auslieferung);
	}

	@Transactional
	void markAuslieferungCompleted(final NewsletterAuslieferung auslieferung) {

		newsletterAuslieferungenRepository.updateAuslieferung(auslieferung);

		if (StatusAuslieferung.ERRORS == auslieferung.getStatus()) {

			LOGGER.info("Versand Auslieferung {} mit Fehlern beendet", auslieferung.getIdentifier());
		} else {

			LOGGER.warn("Versand Auslieferung {} fehlerfrei beendet", auslieferung.getIdentifier());
		}
	}
}
