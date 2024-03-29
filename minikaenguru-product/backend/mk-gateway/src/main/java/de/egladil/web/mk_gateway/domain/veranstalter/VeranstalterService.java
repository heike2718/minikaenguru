// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.admin.VeranstalterSuchkriterium;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterSuchanfrage;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * VeranstalterService
 */
@ApplicationScoped
public class VeranstalterService {

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	VeranstalterRepository veranstalterRepository;

	List<Veranstalter> getAllVeranstalterAktuellerWettbewerb() {

		List<Veranstalter> result = new ArrayList<>();
		Optional<Wettbewerb> optWettbewerb = this.wettbewerbService.aktuellerWettbewerb();

		if (optWettbewerb.isEmpty()) {

			return result;

		}

		final List<Teilnahme> aktuelleTeilnahmen = this.teilnahmenRepository.loadAllForWettbewerb(optWettbewerb.get().id());

		for (Teilnahme teilnahme : aktuelleTeilnahmen) {

			VeranstalterSuchanfrage veranstalterSuchanfrage = new VeranstalterSuchanfrage()
				.withSuchkriterium(VeranstalterSuchkriterium.TEILNAHMENUMMER)
				.withSuchstring(teilnahme.teilnahmenummer().identifier());

			List<Veranstalter> veranstalter = this.veranstalterRepository.findVeranstalter(veranstalterSuchanfrage);
			result.addAll(veranstalter);

		}

		return result;
	}

	/**
	 * Ermittelt die Mailadressen aller Newsletterempfänger, die eine Anmeldung zum aktuellen Wettbewerb haben.
	 *
	 * @return List
	 */
	public List<String> getEmailsNewsletterempfaengerAktuellerWettbewerb() {

		List<Veranstalter> angemeldeteVeranstalter = this.getAllVeranstalterAktuellerWettbewerb();
		final List<String> mailsAngemeldeteUndNewsletter = angemeldeteVeranstalter.stream()
			.filter(v -> v.isNewsletterEmpfaenger())
			.map(v -> v.email()).toList();

		return mailsAngemeldeteUndNewsletter;
	}

}
