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
import javax.persistence.EntityManager;

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

	public static StatistikWettbewerbService createForTest(final LoesungszettelRepository loesungszettelRepository, final WettbewerbService wettbewerbService, final SchulkatalogService schulkatalogService, final TeilnahmenRepository teilnahmeRepository) {

		StatistikWettbewerbService result = new StatistikWettbewerbService();
		result.loesungszettelRepository = loesungszettelRepository;
		result.wettbewerbService = wettbewerbService;
		result.schulkatalogService = schulkatalogService;
		result.teilnahmenRepository = teilnahmeRepository;
		return result;
	}

	public static StatistikWettbewerbService createForIntegrationTest(final EntityManager entityManager) {

		StatistikWettbewerbService result = new StatistikWettbewerbService();
		result.loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManager);
		result.wettbewerbService = WettbewerbService.createForIntegrationTest(entityManager);
		result.teilnahmenRepository = TeilnahmenHibernateRepository.createForIntegrationTest(entityManager);
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

		AnmeldungenAPIModel result = computeTeilnahmestatistik(aktueller, teilnahmen, loesungszettel);

		return result;
	}

	/**
	 * @param  wettbewerbsjahr
	 * @return                 AnmeldungenAPIModel
	 */
	public ResponsePayload berechneTeilnahmestatistikWettbewerbsjahr(final Integer wettbewerbsjahr) {

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

		AnmeldungenAPIModel result = computeTeilnahmestatistik(wettbewerb, teilnahmen, loesungszettel);

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

	AnmeldungenAPIModel computeTeilnahmestatistik(final Wettbewerb wettbewerb, final List<Teilnahme> teilnahmen, final List<Loesungszettel> loesungszettel) {

		List<String> schulkuerzel = teilnahmen.stream().filter(t -> Teilnahmeart.SCHULE == t.teilnahmeart())
			.map(t -> t.teilnahmenummer().identifier()).collect(Collectors.toList());

		// Aus Teilnahmen die schulkuerzel extrahieren und schulen holen
		List<SchuleAPIModel> schulen = null;

		long anzahlPrivatteilnahmen = teilnahmen.stream().filter(t -> Teilnahmeart.PRIVAT == t.teilnahmeart()).count();
		long anzahlPrivatloesungszettel = loesungszettel.stream()
			.filter(z -> Teilnahmeart.PRIVAT == z.teilnahmeIdentifier().teilnahmeart()).count();

		long anzahlSchulanmeldungen = teilnahmen.stream().filter(t -> Teilnahmeart.SCHULE == t.teilnahmeart()).count();

		long anzahlSchulloesungszettel = loesungszettel.stream()
			.filter(z -> Teilnahmeart.SCHULE == z.teilnahmeIdentifier().teilnahmeart()).count();

		AnmeldungsitemAPIModel privatanmeldungen = new AnmeldungsitemAPIModel()
			.withName("Privatanmeldungen")
			.withAnzahlAnmeldungen(Long.valueOf(anzahlPrivatteilnahmen).intValue())
			.withAnzahlLoesungszettel(Long.valueOf(anzahlPrivatloesungszettel).intValue());

		AnmeldungsitemAPIModel summeSchulanmeldungen = new AnmeldungsitemAPIModel()
			.withName("Schulanmeldungen")
			.withAnzahlAnmeldungen(Long.valueOf(anzahlSchulanmeldungen).intValue())
			.withAnzahlLoesungszettel(Long.valueOf(anzahlSchulloesungszettel).intValue());

		// Schulen und Privatteilnahmen aggregieren
		if (!schulkuerzel.isEmpty()) {

			StringsAPIModel strings = new StringsAPIModel().withStrings(schulkuerzel);

			schulen = schulkatalogService.loadSchulenQuietly(strings);
		}

		Map<String, List<SchuleAPIModel>> schulenNachBundeslaendern = new SchulanmeldungenLandAggregator().apply(schulen);

		Map<String, List<Loesungszettel>> loesnungszettelNachBundeslaendern = new LoesungszettelLandAggregator()
			.apply(loesungszettel, schulen);

		List<AnmeldungsitemAPIModel> laenderanmeldungen = new ArrayList<>();

		for (String bundesland : schulenNachBundeslaendern.keySet()) {

			List<SchuleAPIModel> sch = schulenNachBundeslaendern.get(bundesland);

			if (schulen != null) {

				List<Loesungszettel> lz = loesnungszettelNachBundeslaendern.get(bundesland);

				int anzahlAnmeldungen = sch.size();
				int anzahlLoesungszettel = lz == null ? 0 : lz.size();

				laenderanmeldungen.add(new AnmeldungsitemAPIModel().withName(bundesland).withAnzahlAnmeldungen(anzahlAnmeldungen)
					.withAnzahlLoesungszettel(anzahlLoesungszettel));
			}
		}

		Collections.sort(laenderanmeldungen, new AnmeldungsitemAPIModelComparator());

		AnmeldungenAPIModel result = new AnmeldungenAPIModel()
			.withWettbewerbsjahr(wettbewerb.id().toString())
			.withStatusWettbewerb(wettbewerb.status())
			.withSchulanmeldungen(summeSchulanmeldungen)
			.withPrivatanmeldungen(privatanmeldungen)
			.withLaendern(laenderanmeldungen);
		return result;
	}

}
