// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.auswertungen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_wettbewerb.domain.error.MkWettbewerbRuntimeException;
import de.egladil.web.mk_wettbewerb.domain.personen.VeranstalterRepository;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.PersistenterImportierterLoesungszettel;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.PersistenterLoesungszettel;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.TemporaerePersistentePrivatteilnahme;

/**
 * ImportLoesungszettelService
 */
@ApplicationScoped
@Deprecated(forRemoval = true)
public class ImportLoesungszettelService {

	private static final Logger LOG = LoggerFactory.getLogger(ImportLoesungszettelService.class);

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	VeranstalterRepository veranstalterRepository;

	public void run() {

		List<PersistenterImportierterLoesungszettel> importiereLoesungszettel = loesungszettelRepository.loadAllImportiert();
		List<TemporaerePersistentePrivatteilnahme> importiertePrivatteilnahmen = teilnahmenRepository
			.loadAllTemporaryPrivatteilnahmen();

		LOG.info("Starte import von {} Loesungszettel(n)", importiereLoesungszettel.size());

		importiereLoesungszettel = this.mapTeilnahmekuerzel(importiereLoesungszettel, importiertePrivatteilnahmen);

		LOG.info("Privatteilnahme-IDs gemapped");

		List<PersistenterLoesungszettel> vorhandeneLoesungszettel = loesungszettelRepository.loadAll();

		try {

			this.importieren(importiereLoesungszettel, vorhandeneLoesungszettel);

		} catch (Exception e) {

			String msg = "Da ist beim Import der Loesungszettel leider etwas schiefgelaufen.";
			LOG.error(msg + " " + e.getMessage(), e);
			throw new MkWettbewerbRuntimeException(msg);

		}

	}

	@Transactional
	List<PersistenterImportierterLoesungszettel> mapTeilnahmekuerzel(final List<PersistenterImportierterLoesungszettel> importiereLoesungszettel, final List<TemporaerePersistentePrivatteilnahme> importiertePrivatteilnahmen) {

		List<PersistenterImportierterLoesungszettel> result = new ArrayList<>();

		for (PersistenterImportierterLoesungszettel zettel : importiereLoesungszettel) {

			Teilnahmeart teilnahmeart = zettel.getTeilnahmeart();

			if (Teilnahmeart.SCHULE == teilnahmeart) {

				result.add(zettel);
			}

			if (Teilnahmeart.PRIVAT == teilnahmeart) {

				Optional<TemporaerePersistentePrivatteilnahme> optImportierteTeilnahme = importiertePrivatteilnahmen.stream()
					.filter(t -> zettel.getJahr()
						.equals(t.getWettbewerbUuid()) && zettel.getTeilnahmekuerzel().equals(t.getTeilnahmenummerAlt()))
					.findFirst();

				if (optImportierteTeilnahme.isEmpty()) {

					LOG.warn("Loesungszettel kann nicht importiert werden, denn keine passende importierte Teilnahme gefunden: {}",
						zettel);
				} else {

					TemporaerePersistentePrivatteilnahme teilnahme = optImportierteTeilnahme.get();

					if (!StringUtils.isBlank(teilnahme.getTeilnahmenummerNeu())) {

						zettel.setTeilnahmenummerNeu(optImportierteTeilnahme.get().getTeilnahmenummerNeu());

						PersistenterImportierterLoesungszettel geaenderterLoesungszettel = loesungszettelRepository
							.updateLoesungszettel(zettel);
						result.add(geaenderterLoesungszettel);
					} else {

						LOG.warn(
							"Lösungszettel kann nicht importiert werden, denn gefundene TemporaerePersistentePrivatteilnahme hat kein teilnahmenummermapping: {}",
							zettel);
					}

				}
			}
		}

		return result;
	}

	@Transactional
	void importieren(final List<PersistenterImportierterLoesungszettel> importiereLoesungszettel, final List<PersistenterLoesungszettel> vorhandeneLoesungszettel) {

		for (PersistenterImportierterLoesungszettel importierterZettel : importiereLoesungszettel) {

			Optional<PersistenterLoesungszettel> optVorhandener = vorhandeneLoesungszettel.stream()
				.filter(z -> z.getUuid().equals(importierterZettel.getTeilnahmenummerNeu())
					&& z.getWettbewerbUuid().equals(importierterZettel.getJahr()))
				.findFirst();

			if (optVorhandener.isEmpty()) {

				String teilnahmenummer = importierterZettel.getTeilnahmeart() == Teilnahmeart.PRIVAT
					? importierterZettel.getTeilnahmenummerNeu()
					: importierterZettel.getTeilnahmekuerzel();

				PersistenterLoesungszettel loesungszettel = new PersistenterLoesungszettel()
					.withAntwortcode(importierterZettel.getAntwortcode())
					.withAuswertungsquelle(importierterZettel.getAuswertungsquelle())
					.withKaengurusprung(importierterZettel.getKaengurusprung())
					.withKlassenstufe(importierterZettel.getKlassenstufe())
					.withLandkuerzel(importierterZettel.getLandkuerzel())
					.withNummer(importierterZettel.getNummer())
					.withNutzereingabe(importierterZettel.getNutzereingabe())
					.withPunkte(importierterZettel.getPunkte())
					.withSprache(importierterZettel.getSprache())
					.withTeilnahmeart(importierterZettel.getTeilnahmeart())
					.withTeilnahmenummer(teilnahmenummer)
					.withTypo(importierterZettel.isTypo())
					.withWertungscode(importierterZettel.getWertungscode())
					.withWettbewerbUuid(importierterZettel.getJahr());

				loesungszettelRepository.addLosungszettel(loesungszettel);

				LOG.info("Lösungszettel importiert: {}", loesungszettel);

			} else {

				LOG.warn(
					"Lösungszettel ist bereits importiert: {}",
					importierterZettel);
			}

		}

	}

}
