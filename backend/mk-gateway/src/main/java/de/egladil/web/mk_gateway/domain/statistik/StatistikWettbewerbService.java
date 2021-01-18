// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.apimodel.StringsAPIModel;
import de.egladil.web.mk_gateway.domain.kataloge.SchulkatalogService;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.api.AnmeldungenAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.api.AnmeldungsitemAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.api.AnmeldungsitemAPIModelComparator;
import de.egladil.web.mk_gateway.domain.statistik.functions.LoesungszettelLandAggregator;
import de.egladil.web.mk_gateway.domain.statistik.functions.SchulanmeldungenLandAggregator;
import de.egladil.web.mk_gateway.domain.statistik.pdf.StatistikPDFGenerator;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * StatistikWettbewerbService
 */
@ApplicationScoped
public class StatistikWettbewerbService {

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	SchulkatalogService schulkatalogService;

	@Inject
	TeilnahmenRepository teilnahmeRepository;

	public static StatistikWettbewerbService createForTest(final LoesungszettelRepository loesungszettelRepository, final WettbewerbService wettbewerbService, final SchulkatalogService schulkatalogService, final TeilnahmenRepository teilnahmeRepository) {

		StatistikWettbewerbService result = new StatistikWettbewerbService();
		result.loesungszettelRepository = loesungszettelRepository;
		result.wettbewerbService = wettbewerbService;
		result.schulkatalogService = schulkatalogService;
		result.teilnahmeRepository = teilnahmeRepository;
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

	public AnmeldungenAPIModel berechneAnmeldungsstatistikAktuellerWettbewerb() {

		Optional<Wettbewerb> optAktuellerWettbewerb = wettbewerbService.aktuellerWettbewerb();

		if (optAktuellerWettbewerb.isEmpty()) {

			return AnmeldungenAPIModel.createEmptyObject();

		}

		Wettbewerb aktueller = optAktuellerWettbewerb.get();

		List<Teilnahme> teilnahmen = new ArrayList<>();
		List<Loesungszettel> loesungszettel = new ArrayList<>();

		switch (aktueller.status()) {

		case ERFASST:
		case BEENDET:
			return AnmeldungenAPIModel.createEmptyObject();

		case ANMELDUNG:
			teilnahmen = teilnahmeRepository.loadAllForWettbewerb(aktueller.id());
			break;

		case DOWNLOAD_LEHRER:
		case DOWNLOAD_PRIVAT:
			teilnahmen = teilnahmeRepository.loadAllForWettbewerb(aktueller.id());
			loesungszettel = loesungszettelRepository.loadAllForWettbewerb(aktueller.id());
			break;

		default:
			break;
		}

		List<String> schulkuerzel = teilnahmen.stream().filter(t -> Teilnahmeart.SCHULE == t.teilnahmeart())
			.map(t -> t.teilnahmenummer().identifier()).collect(Collectors.toList());

		// Aus Teilnahmen die schulkuerzel extrahieren und schulen holen
		List<SchuleAPIModel> schulen = null;

		long anzahlPrivatteilnahmen = teilnahmen.stream().filter(t -> Teilnahmeart.PRIVAT == t.teilnahmeart()).count();
		long anzahlPrivatloesungszettel = loesungszettel.stream()
			.filter(z -> Teilnahmeart.PRIVAT == z.teilnahmeIdentifier().teilnahmeart()).count();

		AnmeldungsitemAPIModel privatanmeldungen = AnmeldungsitemAPIModel.createEmptyPrivatanmeldungenAPIModel()
			.withAnzahlAnmeldungen(Long.valueOf(anzahlPrivatteilnahmen).intValue())
			.withAnzahlLoesungszettel(Long.valueOf(anzahlPrivatloesungszettel).intValue());

		// Schulen und Privatteilnahmen aggregieren
		if (!schulkuerzel.isEmpty()) {

			StringsAPIModel strings = new StringsAPIModel().withStrings(schulkuerzel);

			schulen = schulkatalogService.loadSchulenQuietly(strings);
		}

		Map<String, List<SchuleAPIModel>> schulenNachBundeslaendern = new SchulanmeldungenLandAggregator().apply(schulen);

		Map<String, List<Loesungszettel>> loesnungszettelNachBundeslaendern = new LoesungszettelLandAggregator()
			.apply(loesungszettel, schulen);

		List<AnmeldungsitemAPIModel> schulanmeldungen = new ArrayList<>();

		for (String bundesland : schulenNachBundeslaendern.keySet()) {

			List<SchuleAPIModel> sch = schulenNachBundeslaendern.get(bundesland);

			if (schulen != null) {

				List<Loesungszettel> lz = loesnungszettelNachBundeslaendern.get(bundesland);

				int anzahlAnmeldungen = sch.size();
				int anzahlLoesungszettel = lz == null ? 0 : lz.size();

				schulanmeldungen.add(new AnmeldungsitemAPIModel().withName(bundesland).withAnzahlAnmeldungen(anzahlAnmeldungen)
					.withAnzahlLoesungszettel(anzahlLoesungszettel));
			}
		}

		Collections.sort(schulanmeldungen, new AnmeldungsitemAPIModelComparator());

		AnmeldungenAPIModel result = new AnmeldungenAPIModel()
			.withPrivatanmeldungen(privatanmeldungen)
			.withSchulen(schulanmeldungen);

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
