// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import java.util.List;

import de.egladil.web.mk_gateway.infrastructure.persistence.impl.NewsletterauslieferungenRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * AuslieferungPicker
 */
@ApplicationScoped
public class AuslieferungPicker {

	@Inject
	NewsletterVersandauftragService newsletterAuftraegeService;

	@Inject
	NewsletterauslieferungenRepository newsletterAuslieferungenRepository;

	public NewsletterAuslieferung getFirstPendingAuslieferung() {

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

}
