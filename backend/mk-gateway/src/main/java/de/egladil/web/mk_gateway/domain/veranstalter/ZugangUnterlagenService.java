// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;

/**
 * ZugangUnterlagenService
 */
@ApplicationScoped
@DomainService
public class ZugangUnterlagenService {

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	WettbewerbService wettbewerbSerivice;

	public static ZugangUnterlagenService createForTest(final TeilnahmenRepository teilnahmenRepository, final VeranstalterRepository veranstalterRepository, final WettbewerbService wettbewerbSerivice) {

		ZugangUnterlagenService result = new ZugangUnterlagenService();
		result.teilnahmenRepository = teilnahmenRepository;
		result.veranstalterRepository = veranstalterRepository;
		result.wettbewerbSerivice = wettbewerbSerivice;
		return result;

	}

	/**
	 * Ermittelt, ob der Veranstalter mit der gegebenen ID Zugang zu den Unterlagen des aktuellen Wettbewerbs hat.
	 *
	 * @param  uuid
	 * @return      boolean
	 */
	public boolean hatZugang(final String uuid) {

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(new Identifier(uuid));

		if (optVeranstalter.isEmpty()) {

			return false;
		}

		Optional<Wettbewerb> optWettbewerb = wettbewerbSerivice.aktuellerWettbewerb();

		if (optWettbewerb.isEmpty()) {

			return false;
		}

		return hatZugang(optVeranstalter.get(), optWettbewerb.get());

	}

	boolean hatZugang(final Veranstalter veranstalter, final Wettbewerb wettbewerb) {

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

			Optional<Teilnahme> optTeilnahme = teilnahmenRepository
				.ofTeilnahmeIdentifier(new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
					.withWettbewerbID(wettbewerb.id()).withTeilnahmenummer(ident.identifier()));

			if (optTeilnahme.isPresent()) {

				teilnahmen.add(optTeilnahme.get());
			}
		});

		return teilnahmen;
	}

}
