// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import de.egladil.web.mk_wettbewerb.domain.semantik.DomainService;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.ZugangsberechtigungUnterlagen;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbStatus;

/**
 * ZugangUnterlagenService
 */
@RequestScoped
@DomainService
public class ZugangUnterlagenService {

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	public static ZugangUnterlagenService createForTest(final TeilnahmenRepository teilnahmenRepository) {

		ZugangUnterlagenService result = new ZugangUnterlagenService();
		result.teilnahmenRepository = teilnahmenRepository;
		return result;

	}

	public boolean hatZugang(final Veranstalter veranstalter, final Wettbewerb wettbewerb) {

		if (wettbewerb.status() == WettbewerbStatus.PAUSE) {

			return false;
		}

		List<Teilnahme> teilnahmen = this.findTeilnahmen(veranstalter);

		Optional<Teilnahme> optAktuell = teilnahmen.stream().filter(t -> t.wettbewerbID().equals(wettbewerb.id())).findFirst();

		if (optAktuell.isEmpty()) {

			return false;
		}

		if (veranstalter.zugangsberechtigungUnterlagen() == ZugangsberechtigungUnterlagen.ENTZOGEN) {

			return false;
		}

		if (veranstalter.zugangsberechtigungUnterlagen() == ZugangsberechtigungUnterlagen.ERTEILT) {

			return true;
		}

		Teilnahme teilnahme = optAktuell.get();

		switch (wettbewerb.status()) {

		case ANMELDUNG:
			return false;

		case DOWNLOAD_LEHRER:
			return teilnahme.teilnahmeart() == Teilnahmeart.SCHULE;

		case DOWNLOAD_PRIVAT:
			return true;

		default:
			break;
		}

		return false;
	}

	private List<Teilnahme> findTeilnahmen(final Veranstalter veranstalter) {

		List<Teilnahme> teilnahmen = new ArrayList<>();

		veranstalter.teilnahmekuerzel().forEach(ident -> {

			Optional<Teilnahme> optTeilnahme = teilnahmenRepository.ofIdentifier(ident.identifier());

			if (optTeilnahme.isPresent()) {

				teilnahmen.add(optTeilnahme.get());
			}
		});

		return teilnahmen;
	}

}
