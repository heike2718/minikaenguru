// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.apimodel.StringsAPIModel;
import de.egladil.web.mk_gateway.domain.kataloge.SchulkatalogService;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.api.AnmeldungenAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.api.AnmeldungsitemAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.api.AnmeldungsitemAPIModelComparator;
import de.egladil.web.mk_gateway.domain.statistik.api.MedianAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.api.MedianeAPIModel;
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
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.TeilnahmenHibernateRepository;

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
	TeilnahmenRepository teilnahmenRepository;

	private String schulenJson;

	public static StatistikWettbewerbService createForTest(final LoesungszettelRepository loesungszettelRepository, final WettbewerbService wettbewerbService, final SchulkatalogService schulkatalogService, final TeilnahmenRepository teilnahmeRepository) {

		StatistikWettbewerbService result = new StatistikWettbewerbService();
		result.loesungszettelRepository = loesungszettelRepository;
		result.wettbewerbService = wettbewerbService;
		result.schulkatalogService = schulkatalogService;
		result.teilnahmenRepository = teilnahmeRepository;
		return result;
	}

	public static StatistikWettbewerbService createForIntegrationTest(final EntityManager entityManagerWettbewerbe, final String schulenJson) throws Exception {

		StatistikWettbewerbService result = new StatistikWettbewerbService();
		result.loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManagerWettbewerbe);
		result.wettbewerbService = WettbewerbService.createForIntegrationTest(entityManagerWettbewerbe);
		result.teilnahmenRepository = TeilnahmenHibernateRepository.createForIntegrationTest(entityManagerWettbewerbe);
		result.schulenJson = schulenJson;
		return result;
	}

	/**
	 * @param  wettbewerbID
	 * @return              Map
	 */
	public MedianeAPIModel berechneGesamtmedianeWettbewerb(final WettbewerbID wettbewerbID) {

		List<Loesungszettel> loesungszettel = loesungszettelRepository.loadAllForWettbewerb(wettbewerbID);
		return this.berechneMediane(loesungszettel);
	}

	/**
	 * Berechnet für die gegebenen Loesungszettel die Mediane je Klassenstufe.
	 *
	 * @param  loesungszettel
	 * @return                MedianeAPIModel
	 */
	MedianeAPIModel berechneMediane(final List<Loesungszettel> loesungszettel) {

		final MedianRechner medianRechner = new MedianRechner();
		final MedianeAPIModel result = new MedianeAPIModel();

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			List<Loesungszettel> loesungszettelKlassenstufe = loesungszettel.stream().filter(z -> klassenstufe == z.klassenstufe())
				.collect(Collectors.toList());

			if (!loesungszettelKlassenstufe.isEmpty()) {

				String median = medianRechner.berechneMedian(loesungszettelKlassenstufe);
				MedianAPIModel medianModel = new MedianAPIModel(klassenstufe, median, loesungszettelKlassenstufe.size());
				result.addMedian(medianModel);
			}
		}

		return result;
	}

	public AnmeldungenAPIModel berechneAnmeldungsstatistikAktuellerWettbewerb() {

		Optional<Wettbewerb> optAktuellerWettbewerb = wettbewerbService.aktuellerWettbewerb();

		if (optAktuellerWettbewerb.isEmpty()) {

			return this.createEmptyResponsePayload();

		}

		Wettbewerb aktueller = optAktuellerWettbewerb.get();

		List<Teilnahme> teilnahmen = new ArrayList<>();
		List<Loesungszettel> loesungszettel = new ArrayList<>();

		switch (aktueller.status()) {

		case ERFASST:
		case BEENDET:
			return this.createEmptyResponsePayload();

		case ANMELDUNG:
			teilnahmen = teilnahmenRepository.loadAllForWettbewerb(aktueller.id());
			break;

		case DOWNLOAD_LEHRER:
		case DOWNLOAD_PRIVAT:
			teilnahmen = teilnahmenRepository.loadAllForWettbewerb(aktueller.id());
			loesungszettel = loesungszettelRepository.loadAllForWettbewerb(aktueller.id());
			break;

		default:
			break;
		}

		AnmeldungenAPIModel result = computeTeilnahmestatistik(aktueller, teilnahmen, loesungszettel, false);

		return result;
	}

	/**
	 * @param  wettbewerbsjahr
	 * @return                 AnmeldungenAPIModel
	 */
	public ResponsePayload getBeteiligungen(final Integer wettbewerbsjahr) {

		WettbewerbID wettbewerbId = new WettbewerbID(wettbewerbsjahr);
		Optional<Wettbewerb> optWettbewerb = wettbewerbService.findWettbewerbMitID(wettbewerbId);

		if (optWettbewerb.isEmpty()) {

			return ResponsePayload.messageOnly(MessagePayload.error("keine Daten vorhanden"));

		}

		Wettbewerb wettbewerb = optWettbewerb.get();

		List<Teilnahme> teilnahmen = new ArrayList<>();
		List<Loesungszettel> loesungszettel = new ArrayList<>();

		switch (wettbewerb.status()) {

		case ERFASST:

			AnmeldungenAPIModel data = createEmptyResponsePayload().withStatusWettbewerb(wettbewerb.status())
				.withWettbewerbsjahr(wettbewerb.id().toString());

			return new ResponsePayload(MessagePayload.warn("Der Wettbewerb ist noch nicht zur Anmeldung freigegeben."), data);

		default:
			teilnahmen = teilnahmenRepository.loadAllForWettbewerb(wettbewerbId);
			loesungszettel = loesungszettelRepository.loadAllForWettbewerb(wettbewerbId);
			break;
		}

		boolean withMediane = wettbewerb.isBeendet();

		AnmeldungenAPIModel result = computeTeilnahmestatistik(wettbewerb, teilnahmen, loesungszettel, withMediane);

		return new ResponsePayload(MessagePayload.ok(), result);
	}

	AnmeldungenAPIModel createEmptyResponsePayload() {

		return new AnmeldungenAPIModel()
			.withPrivatanmeldungen(new AnmeldungsitemAPIModel().withName("Privatanmeldungen"))
			.withSchulanmeldungen(new AnmeldungsitemAPIModel().withName("Schulanmeldungen"));

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

	AnmeldungenAPIModel computeTeilnahmestatistik(final Wettbewerb wettbewerb, final List<Teilnahme> teilnahmen, final List<Loesungszettel> loesungszettel, final boolean withMediane) {

		List<SchuleAPIModel> schulen = schulenJson != null ? getSchulenFromJson() : getSchulenFromSchulkatalog(teilnahmen);

		long anzahlPrivatteilnahmen = teilnahmen.stream().filter(t -> Teilnahmeart.PRIVAT == t.teilnahmeart()).count();
		List<Loesungszettel> loesungszettelPrivat = loesungszettel.stream()
			.filter(z -> Teilnahmeart.PRIVAT == z.teilnahmeIdentifier().teilnahmeart()).collect(Collectors.toList());

		AnmeldungsitemAPIModel privatanmeldungen = new AnmeldungsitemAPIModel()
			.withName("Privatanmeldungen")
			.withAnzahlAnmeldungen(Long.valueOf(anzahlPrivatteilnahmen).intValue())
			.withAnzahlLoesungszettel(loesungszettelPrivat.size());

		if (withMediane) {

			MedianeAPIModel medianePrivat = berechneMediane(loesungszettelPrivat);
			privatanmeldungen.setMediane(medianePrivat.getMedianeSortedByKlassenstufe());
		}

		long anzahlSchulanmeldungen = teilnahmen.stream().filter(t -> Teilnahmeart.SCHULE == t.teilnahmeart()).count();
		List<Loesungszettel> loesungszettelSchulen = loesungszettel.stream()
			.filter(z -> Teilnahmeart.SCHULE == z.teilnahmeIdentifier().teilnahmeart()).collect(Collectors.toList());

		AnmeldungsitemAPIModel summeSchulanmeldungen = new AnmeldungsitemAPIModel()
			.withName("Schulanmeldungen")
			.withAnzahlAnmeldungen(Long.valueOf(anzahlSchulanmeldungen).intValue())
			.withAnzahlLoesungszettel(loesungszettelSchulen.size());

		if (withMediane) {

			MedianeAPIModel medianeSchulen = berechneMediane(loesungszettelSchulen);
			summeSchulanmeldungen.setMediane(medianeSchulen.getMedianeSortedByKlassenstufe());
		}

		Map<String, List<SchuleAPIModel>> schulenNachBundeslaendern = new SchulanmeldungenLandAggregator().apply(schulen);

		Map<String, List<Loesungszettel>> loesungszettelNachBundeslaendern = new LoesungszettelLandAggregator()
			.apply(loesungszettel, schulen);

		List<AnmeldungsitemAPIModel> laenderanmeldungen = new ArrayList<>();

		for (String bundesland : schulenNachBundeslaendern.keySet()) {

			List<SchuleAPIModel> sch = schulenNachBundeslaendern.get(bundesland);

			if (schulen != null) {

				List<Loesungszettel> lz = loesungszettelNachBundeslaendern.get(bundesland);

				int anzahlAnmeldungen = sch.size();
				int anzahlLoesungszettel = lz == null ? 0 : lz.size();

				AnmeldungsitemAPIModel statistikLand = new AnmeldungsitemAPIModel().withName(bundesland)
					.withAnzahlAnmeldungen(anzahlAnmeldungen)
					.withAnzahlLoesungszettel(anzahlLoesungszettel);

				if (withMediane && anzahlLoesungszettel > 0) {

					MedianeAPIModel medianeLand = berechneMediane(lz);
					statistikLand.setMediane(medianeLand.getMedianeSortedByKlassenstufe());
				}

				laenderanmeldungen.add(statistikLand);
			}
		}

		Collections.sort(laenderanmeldungen, new AnmeldungsitemAPIModelComparator());

		AnmeldungenAPIModel result = new AnmeldungenAPIModel()
			.withWettbewerbsjahr(wettbewerb.id().toString())
			.withStatusWettbewerb(wettbewerb.status())
			.withSchulanmeldungen(summeSchulanmeldungen)
			.withPrivatanmeldungen(privatanmeldungen)
			.withLaendern(laenderanmeldungen);

		if (withMediane) {

			MedianeAPIModel medianeGesamt = berechneMediane(loesungszettel);
			result.setMediane(medianeGesamt.getMedianeSortedByKlassenstufe());

		}
		return result;
	}

	/**
	 * @return
	 */
	private List<SchuleAPIModel> getSchulenFromSchulkatalog(final List<Teilnahme> teilnahmen) {

		List<String> schulkuerzel = teilnahmen.stream().filter(t -> Teilnahmeart.SCHULE == t.teilnahmeart())
			.map(t -> t.teilnahmenummer().identifier()).collect(Collectors.toList());

		if (!schulkuerzel.isEmpty()) {

			StringsAPIModel strings = new StringsAPIModel().withStrings(schulkuerzel);
			List<SchuleAPIModel> schulen = schulkatalogService.loadSchulenQuietly(strings);

			return schulen;
		}

		return new ArrayList<>();
	}

	/**
	 * @return
	 */
	private List<SchuleAPIModel> getSchulenFromJson() {

		try {

			SchuleAPIModel[] schulen = new ObjectMapper().readValue(schulenJson, SchuleAPIModel[].class);
			return Arrays.asList(schulen);

		} catch (Exception e) {

			throw new RuntimeException("Test kann nicht stattfinden wegen " + e.getMessage(), e);

		}
	}
}
