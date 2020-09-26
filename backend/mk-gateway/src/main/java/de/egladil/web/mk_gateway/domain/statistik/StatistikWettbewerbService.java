// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.pdf.StatistikPDFGenerator;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * StatistikWettbewerbService
 */
@ApplicationScoped
public class StatistikWettbewerbService {

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	public static StatistikWettbewerbService createForTest(final LoesungszettelRepository loesungszettelRepository) {

		StatistikWettbewerbService result = new StatistikWettbewerbService();
		result.loesungszettelRepository = loesungszettelRepository;
		return result;
	}

	/**
	 * @param  wettbewerbID
	 * @return              Map
	 */
	public Map<Klassenstufe, String> berechneGesamtmedianeWettbewerb(final WettbewerbID wettbewerbID) {

		final MedianRechner medianRechner = new MedianRechner();

		Map<Klassenstufe, String> result = new HashMap<>();

		List<Loesungszettel> loesungszettel = loesungszettelRepository.loadAllForWettbewerb(wettbewerbID);

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			List<Loesungszettel> loesungszettelKlassenstufe = loesungszettel.stream().filter(z -> klassenstufe == z.klassenstufe())
				.collect(Collectors.toList());

			if (!loesungszettelKlassenstufe.isEmpty()) {

				String median = medianRechner.berechneMedian(loesungszettelKlassenstufe);
				result.put(klassenstufe, median);

			}
		}

		return result;
	}

	/**
	 * Erstellt die Statistik für alle vorhandenen Lösungszettel
	 *
	 * @param  wettbewerbID
	 * @param  klassenstufe
	 * @return              Optional
	 */
	public Optional<GesamtpunktverteilungKlassenstufe> erstelleStatistikWettbewerbKlassenstufe(final WettbewerbID wettbewerbID, final Klassenstufe klassenstufe) {

		List<Loesungszettel> zettelKlassenstufe = loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID,
			klassenstufe);

		if (zettelKlassenstufe.isEmpty()) {

			return Optional.empty();
		}

		GesamtpunktverteilungKlassenstufe verteilung = new StatistikKlassenstufeService()
			.generiereGesamtpunktverteilung(wettbewerbID, klassenstufe, zettelKlassenstufe);

		return Optional.of(verteilung);

	}

	public DownloadData erstelleStatistikPDFWettbewerb(final WettbewerbID wettbewerbID) {

		Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> gesamtpunktverteilungenNachKlassenstufe = new HashMap<>();

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			Optional<GesamtpunktverteilungKlassenstufe> opt = erstelleStatistikWettbewerbKlassenstufe(
				wettbewerbID,
				klassenstufe);

			if (opt.isPresent()) {

				gesamtpunktverteilungenNachKlassenstufe.put(klassenstufe, opt.get());
			}
		}

		byte[] pdf = new StatistikPDFGenerator().generiereGesamtpunktverteilungWettbewerb(wettbewerbID,
			gesamtpunktverteilungenNachKlassenstufe);

		return new DownloadData("minikaenguru_" + wettbewerbID.toString() + "_gesamptunktverteilung.pdf", pdf);
	}

}
