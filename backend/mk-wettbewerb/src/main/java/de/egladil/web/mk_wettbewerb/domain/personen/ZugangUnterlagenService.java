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
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.ZugangUnterlagen;
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

		if (wettbewerb.status() == WettbewerbStatus.BEENDET || wettbewerb.status() == WettbewerbStatus.ERFASST) {

			return false;
		}

		List<Teilnahme> teilnahmen = this.findTeilnahmen(veranstalter, wettbewerb);

		if (teilnahmen.isEmpty()) {

			return false;
		}

		if (veranstalter.zugangUnterlagen() == ZugangUnterlagen.ENTZOGEN) {

			return false;
		}

		if (veranstalter.zugangUnterlagen() == ZugangUnterlagen.ERTEILT) {

			return true;
		}

		switch (wettbewerb.status()) {

		case ANMELDUNG:
			return false;

		case DOWNLOAD_LEHRER:
			return veranstalter.teilnahmeart() == Teilnahmeart.SCHULE;

		case DOWNLOAD_PRIVAT:
			return true;

		default:
			break;
		}

		return false;
	}

	private List<Teilnahme> findTeilnahmen(final Veranstalter veranstalter, final Wettbewerb wettbewerb) {

		List<Teilnahme> teilnahmen = new ArrayList<>();

		veranstalter.teilnahmeIdentifier().forEach(ident -> {

			Optional<Teilnahme> optTeilnahme = teilnahmenRepository.ofTeilnahmenummerArtWettbewerb(ident.identifier(),
				veranstalter.teilnahmeart(), wettbewerb.id());

			if (optTeilnahme.isPresent()) {

				teilnahmen.add(optTeilnahme.get());
			}
		});

		return teilnahmen;
	}

}