// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.StringsAPIModel;
import de.egladil.web.mk_gateway.domain.auth.s2s.MkGatewayAuthConfig;
import de.egladil.web.mk_gateway.domain.error.MkGatewayWebApplicationException;
import de.egladil.web.mk_gateway.domain.kataloge.LandPayloadComparator;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.kataloge.api.LandPayload;
import de.egladil.web.mk_gateway.domain.kataloge.dto.KatalogItem;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.AufgabeErgebnisItem;
import de.egladil.web.mk_gateway.domain.statistik.AufgabeErgebnisRechner;
import de.egladil.web.mk_gateway.domain.statistik.Aufgabenkategorie;
import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungItem;
import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungKlassenstufeDaten;
import de.egladil.web.mk_gateway.domain.statistik.VerteilungRechner;
import de.egladil.web.mk_gateway.domain.statistik.functions.PunkteStringMapper;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbeDescendingComparator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

/**
 * MkBiZaStatistikService
 */
@ApplicationScoped
public class MkBiZaStatistikService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MkBiZaStatistikService.class);

	private final AufgabeErgebnisRechner aufgabeErgebnisRechner = new AufgabeErgebnisRechner();

	@Inject
	MkGatewayAuthConfig authConfig;

	@ConfigProperty(name = "admin.secret")
	String adminSecret;

	@Inject
	WettbewerbRepository wettbewerbRepository;

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	MkKatalogeResourceAdapter katalogeResourceAdapter;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	/**
	 * Gibt die Daten für die Übersicht über die Wettbewerbe zurüclḱ.
	 *
	 * @param  status
	 *                WettbewerbStatus
	 * @return        List
	 */
	public List<MkBiZaWettbewerb> loadWettbewerbeOverview() {

		List<Wettbewerb> allWettbewerbe = this.wettbewerbRepository.loadWettbewerbe();
		Collections.sort(allWettbewerbe, new WettbewerbeDescendingComparator());

		List<MkBiZaWettbewerb> result = allWettbewerbe.stream()
			.map(w -> new MkBiZaWettbewerb(w.id().jahr(), w.status(), w.medianIkids(), w.medianKlasseEins(), w.medianKlasseZwei()))
			.toList();

		for (MkBiZaWettbewerb wettbewerb : result) {

			List<Loesungszettel> loesungszettel = loesungszettelRepository
				.loadAllForWettbewerb(new WettbewerbID(wettbewerb.getJahr()));

			long anzahlIKIDs = loesungszettel.stream().filter(l -> l.klassenstufe() == Klassenstufe.IKID).count();
			long anzahlEINS = loesungszettel.stream().filter(l -> l.klassenstufe() == Klassenstufe.EINS).count();
			long anzahlZWEI = loesungszettel.stream().filter(l -> l.klassenstufe() == Klassenstufe.ZWEI).count();

			List<MkBiZaGruppierungsitem> kinderJeKlassenstufe = new ArrayList<>();
			kinderJeKlassenstufe.add(new MkBiZaGruppierungsitem().withAnzahl(anzahlIKIDs).withName("Inklusion"));
			kinderJeKlassenstufe.add(new MkBiZaGruppierungsitem().withAnzahl(anzahlEINS).withName("Klasse 1"));
			kinderJeKlassenstufe.add(new MkBiZaGruppierungsitem().withAnzahl(anzahlZWEI).withName("Klasse 2"));

			wettbewerb.setAnzahlKinder(loesungszettel.size());
			wettbewerb.setKinderJeKlassenstufe(kinderJeKlassenstufe);
		}

		return result;
	}

	/**
	 * Aggregiert die Statistikdaten für ein gegebenes Wettbewerbsjahr.
	 *
	 * @param  jahr
	 *                                          Integer das Wettbewerbsjahr
	 * @return                                  MkBiZaWettbewerbDetails
	 * @throws MkGatewayWebApplicationException
	 *                                          wird im MkGatewayExceptionMapper verarbeitet.
	 */
	public MkBiZaWettbewerbDetails getStatistikJahr(final Integer jahr) throws MkGatewayWebApplicationException {

		Optional<Wettbewerb> optWettbewerb = wettbewerbRepository.wettbewerbMitID(new WettbewerbID(jahr));

		if (optWettbewerb.isEmpty()) {

			LOGGER.warn("Unbekanntes Wettbewerbsjahr {} - Aufruf über MkBiZa", jahr);
			MessagePayload messagePayload = MessagePayload.error("NotFound");
			Response response = Response.status(404).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		Wettbewerb wettbewerb = optWettbewerb.get();

		MkBiZaWettbewerbDetails result = new MkBiZaWettbewerbDetails();
		result.setJahr(jahr);
		result.setBeendet(wettbewerb.isBeendet());

		List<Loesungszettel> alleLoesungszettel = loesungszettelRepository.loadAllForWettbewerb(wettbewerb.id());
		List<Teilnahme> anmeldungen = teilnahmenRepository.loadAllForWettbewerb(wettbewerb.id());

		result.setAnzahlKinderGesamt(alleLoesungszettel.size());

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			long anzahl = alleLoesungszettel.stream().filter(l -> klassenstufe == l.klassenstufe()).count();
			MkBiZaGruppierungsitem gruppierungsitem = new MkBiZaGruppierungsitem().withName(klassenstufe.getLabel())
				.withAnzahl(anzahl);
			result.addKinderJeKlassenstufe(gruppierungsitem, klassenstufe);
		}

		for (Sprache sprache : Sprache.values()) {

			long anzahl = alleLoesungszettel.stream().filter(l -> sprache == l.sprache()).count();
			MkBiZaGruppierungsitem gruppierungsitem = new MkBiZaGruppierungsitem().withName(sprache.getLabel())
				.withAnzahl(anzahl);
			result.addKinderJeSprache(gruppierungsitem);
		}

		MkBiZaGruppierungsitem gruppierungsitemPrivat = null;

		for (Teilnahmeart teilnahmeart : Teilnahmeart.values()) {

			{

				long anzahl = alleLoesungszettel.stream().filter(l -> l.teilnahmeIdentifier().teilnahmeart() == teilnahmeart)
					.count();
				MkBiZaGruppierungsitem gruppierungsitem = new MkBiZaGruppierungsitem().withName(teilnahmeart.toString())
					.withAnzahl(anzahl);
				result.addKinderJeTeilnahmeart(gruppierungsitem);

				if (Teilnahmeart.PRIVAT == teilnahmeart) {

					gruppierungsitemPrivat = gruppierungsitem;
				}
			}

			{

				long anzahl = anmeldungen.stream().filter(a -> a.teilnahmeIdentifier().teilnahmeart() == teilnahmeart).count();

				if (Teilnahmeart.SCHULE == teilnahmeart) {

					result.setAnzahlSchulanmeldungen(anzahl);
				}

				if (Teilnahmeart.PRIVAT == teilnahmeart) {

					result.setAnzahlPrivatanmeldungen(anzahl);
				}
			}

		}

		final Map<String, List<Loesungszettel>> loesungszettelgroupsByLaendern = this.groupByLaendern(
			alleLoesungszettel.stream().filter(l -> l.teilnahmeIdentifier().teilnahmeart() == Teilnahmeart.SCHULE).toList());

		List<LandPayload> laender = getLaender();

		Set<Identifier> distinctSchuleLoesungszettel = alleLoesungszettel.stream()
			.filter(l -> Teilnahmeart.SCHULE == l.teilnahmeIdentifier().teilnahmeart())
			.map(Loesungszettel::getTheTeilnahmenummer).collect(Collectors.toSet());

		List<SchuleAPIModel> schulen = this.getSchulen(distinctSchuleLoesungszettel);

		for (LandPayload land : laender) {

			List<Loesungszettel> loesungszettelgroups = loesungszettelgroupsByLaendern.get(land.kuerzel());

			if (loesungszettelgroups != null && !loesungszettelgroups.isEmpty()) {

				MkBiZaGruppierungsitem gruppierungsitem = new MkBiZaGruppierungsitem().withName(land.name())
					.withAnzahl(loesungszettelgroups.size());
				result.addKinderJeLand(gruppierungsitem);
			}

			long anzahlSchulenImLand = schulen.stream().filter(s -> s.kuerzelLand().equals(land.kuerzel())).count();

			if (anzahlSchulenImLand > 0) {

				result.addSchulenJeLand(new MkBiZaGruppierungsitem().withName(land.name()).withAnzahl(anzahlSchulenImLand));
			}
		}

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			Integer median = medianZuKlassenstufe(klassenstufe, wettbewerb);

			if (median != null) {

				result.addMedianeJeKlassenstufe(
					new MkBiZaGruppierungsitem().withAnzahl(Long.valueOf(median))
						.withName(klassenstufe.getLabel()));
			}
		}

		List<MkBiZaGruppierungsitem> teilnehmendeSchulen = result.getSchulenJeLand();

		long anzahlTeilnehmendeSchulen = teilnehmendeSchulen.stream().mapToLong(MkBiZaGruppierungsitem::getAnzahl).sum();

		result.addKinderJeLand(gruppierungsitemPrivat);
		result.setTeilnehmendeSchulenGesamt(anzahlTeilnehmendeSchulen);

		return result;
	}

	/**
	 * Gibt die Anzahl
	 *
	 * @param  wettbewerbsjahr
	 * @return
	 */
	long getAnzahlLoesungszettel(final Integer wettbewerbsjahr) {

		return loesungszettelRepository.anzahlForWettbewerb(new WettbewerbID(wettbewerbsjahr));
	}

	Map<String, List<Loesungszettel>> groupByLaendern(final List<Loesungszettel> alleLoesungszettel) {

		final Map<String, List<Loesungszettel>> groupsByTeilnahmenummern = new HashMap<>();

		for (Loesungszettel loesungszettel : alleLoesungszettel) {

			String landkuerzel = loesungszettel.landkuerzel();

			List<Loesungszettel> list = groupsByTeilnahmenummern.get(landkuerzel);

			if (list == null) {

				List<Loesungszettel> lzList = new ArrayList<>();
				lzList.add(loesungszettel);
				groupsByTeilnahmenummern.put(landkuerzel, lzList);
			} else {

				list.add(loesungszettel);
			}

		}

		return groupsByTeilnahmenummern;
	}

	List<LandPayload> getLaender() {

		try {

			Response response = katalogeResourceAdapter.loadLaenderV2(authConfig.client(), adminSecret);
			KatalogItem[] laender = response.readEntity(new GenericType<KatalogItem[]>() {
			});

			List<LandPayload> list = Arrays.stream(laender).map(l -> LandPayload.create(l.getKuerzel(), l.getName())).toList();

			List<LandPayload> result = new ArrayList<>(list);
			Collections.sort(result, new LandPayloadComparator());

			return list;

		} catch (Exception e) {

			if (e instanceof WebApplicationException) {

				LOGGER.error("WebApplicationException beim Aufruf von [loadLaender]: {}", e.getMessage(), e);
				// das hier ist ein klarer Fall von ServerError
				throw new MkGatewayWebApplicationException(Response.serverError().build());
			}

			if (e instanceof ProcessingException) {

				LOGGER.error("endpoint [loadLaender] ist nicht erreichbar: {}", e.getMessage(), e);

				return new ArrayList<>();

			}

			LOGGER.error("Unerwartete Exception - " + e.getMessage(), e);

			return new ArrayList<>();
		}
	}

	List<SchuleAPIModel> getSchulen(final Set<Identifier> teilnahmenummern) {

		List<String> teilnahmenummernList = teilnahmenummern.stream().map(Identifier::identifier).toList();

		try {

			Response response = katalogeResourceAdapter.loadSchulenV2(new StringsAPIModel().withStrings(teilnahmenummernList));
			SchuleAPIModel[] schulen = response.readEntity(new GenericType<SchuleAPIModel[]>() {
			});

			return Arrays.asList(schulen);

		} catch (Exception e) {

			if (e instanceof WebApplicationException) {

				LOGGER.error("WebApplicationException beim Aufruf von [findSchulen]: {}", e.getMessage(), e);
				// das hier ist ein klarer Fall von ServerError
				throw new MkGatewayWebApplicationException(Response.serverError().build());
			}

			if (e instanceof ProcessingException) {

				LOGGER.error("endpoint [findSchulen] ist nicht erreichbar: {}", e.getMessage(), e);

				return new ArrayList<>();

			}

			LOGGER.error("Unerwartete Exception - " + e.getMessage(), e);

			return new ArrayList<>();
		}
	}

	/**
	 * Berechnet die Statistik zur gegebenen Klassenstufe des Wettbewerbsjahres.
	 *
	 * @param  jahr
	 * @param  klassenstufe
	 * @return
	 */
	public MkBiZaStatistikKlassenstufe getStatistikJahrKlassenstufe(final Integer jahr, final Klassenstufe klassenstufe) {

		Optional<Wettbewerb> optWettbewerb = wettbewerbRepository.wettbewerbMitID(new WettbewerbID(jahr));

		if (optWettbewerb.isEmpty()) {

			LOGGER.warn("Unbekanntes Wettbewerbsjahr {} - Aufruf über MkBiZa", jahr);
			MessagePayload messagePayload = MessagePayload.error("NotFound");
			Response response = Response.status(404).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		Wettbewerb wettbewerb = optWettbewerb.get();

		if (Klassenstufe.IKID == klassenstufe && StringUtils.isBlank(wettbewerb.loesungsbuchstabenIkids())) {

			LOGGER.warn("Keine Aufgaben zur Klassenstufe {} im Jahr {} - Aufruf über MkBiZa", klassenstufe, jahr);
			MessagePayload messagePayload = MessagePayload.error("NotFound");
			Response response = Response.status(404).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		if (Klassenstufe.EINS == klassenstufe && StringUtils.isBlank(wettbewerb.loesungsbuchstabenKlasse1())) {

			LOGGER.warn("Keine Aufgaben zur Klassenstufe {} im Jahr {} - Aufruf über MkBiZa", klassenstufe, jahr);
			MessagePayload messagePayload = MessagePayload.error("NotFound");
			Response response = Response.status(404).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		MkBiZaStatistikKlassenstufe result = new MkBiZaStatistikKlassenstufe();
		result.setWettbewerbsjahr(wettbewerb.id().toString());
		result.setKlassenstufe(klassenstufe.toString());
		result.setBeendet(wettbewerb.isBeendet());

		List<Loesungszettel> zettelKlassenstufe = loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerb.id(),
			klassenstufe);

		if (wettbewerb.isBeendet()) {

			Map<String, Integer> aufgabennummernWithWertungscodeIndex = klassenstufe
				.getAufgabennummernWithWertungscodeIndex(jahr);

			for (String nummer : aufgabennummernWithWertungscodeIndex.keySet()) {

				MkBiZaStatistikAufgabe statistikAufgabe = berechneStatistikAufgabe(nummer, aufgabennummernWithWertungscodeIndex,
					zettelKlassenstufe, klassenstufe);
				result.addAufgabenstatistik(statistikAufgabe);
			}

			Integer median = medianZuKlassenstufe(klassenstufe, wettbewerb);

			if (median != null) {

				int maximalpunktzahlMal100 = klassenstufe.getMaximalpunktzahlMal100();

				// Bis 2017 gab es keine Extraaufgaben für Klasse 1.
				if (Klassenstufe.EINS == klassenstufe && wettbewerb.id().jahr().intValue() < 2017) {

					maximalpunktzahlMal100 = 75;
				}

				result.setMedianUndGesamtpunkte(
					new MkBiZaMedianDto(median.intValue(), maximalpunktzahlMal100 / 100));
			}

			GesamtpunktverteilungKlassenstufeDaten daten = new VerteilungRechner().berechne(wettbewerb.id(), klassenstufe,
				zettelKlassenstufe);

			List<GesamtpunktverteilungItem> gesamtpunktverteilungItems = daten.gesamtpunktverteilungItems();

			for (GesamtpunktverteilungItem item : gesamtpunktverteilungItems) {

				result.addKinderJePunktintervall(
					new MkBiZaGruppierungsitem().withAnzahl(item.getAnzahl()).withName(item.getPunktintervallText()));

			}

			result.setRohpunkte(daten.rohpunktItems());
		}

		for (Sprache sprache : Sprache.values()) {

			long anzahl = zettelKlassenstufe.stream().filter(l -> sprache == l.sprache()).count();
			MkBiZaGruppierungsitem gruppierungsitem = new MkBiZaGruppierungsitem().withName(sprache.getLabel())
				.withAnzahl(anzahl);
			result.addKinderJeSprache(gruppierungsitem);
		}

		MkBiZaGruppierungsitem gruppierungsitemPrivat = null;

		for (Teilnahmeart teilnahmeart : Teilnahmeart.values()) {

			long anzahl = zettelKlassenstufe.stream().filter(l -> l.teilnahmeIdentifier().teilnahmeart() == teilnahmeart)
				.count();
			MkBiZaGruppierungsitem gruppierungsitem = new MkBiZaGruppierungsitem().withName(teilnahmeart.toString())
				.withAnzahl(anzahl);
			result.addKinderJeTeilnahmeart(gruppierungsitem);

			if (Teilnahmeart.PRIVAT == teilnahmeart) {

				gruppierungsitemPrivat = gruppierungsitem;
			}
		}

		final Map<String, List<Loesungszettel>> loesungszettelgroupsByLaendern = this.groupByLaendern(
			zettelKlassenstufe.stream().filter(l -> l.teilnahmeIdentifier().teilnahmeart() == Teilnahmeart.SCHULE).toList());

		List<LandPayload> laender = getLaender();

		for (LandPayload land : laender) {

			List<Loesungszettel> loesungszettelgroups = loesungszettelgroupsByLaendern.get(land.kuerzel());

			if (loesungszettelgroups != null && !loesungszettelgroups.isEmpty()) {

				MkBiZaGruppierungsitem gruppierungsitem = new MkBiZaGruppierungsitem().withName(land.name())
					.withAnzahl(loesungszettelgroups.size());
				result.addKinderJeLand(gruppierungsitem);
			}
		}

		result.addKinderJeLand(gruppierungsitemPrivat);
		result.setAnzahlKinderGesamt(zettelKlassenstufe.size());

		return result;
	}

	MkBiZaStatistikAufgabe berechneStatistikAufgabe(final String nummer, final Map<String, Integer> aufgabennummernWithWertungscodeIndex, final List<Loesungszettel> zettelKlassenstufe, final Klassenstufe klassenstufe) {

		Integer index = aufgabennummernWithWertungscodeIndex.get(nummer);

		if (index == null) {

			LOGGER.warn("Falsche nummer {} - Aufruf über MkBiZa", nummer);
			MessagePayload messagePayload = MessagePayload.error("NotFound");
			Response response = Response.status(404).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		AufgabeErgebnisItem aufgabeErgenbnisItem = aufgabeErgebnisRechner.berechneAufgabeErgebnisItem(nummer, index,
			zettelKlassenstufe);

		Aufgabenkategorie aufgabenkategorie = Aufgabenkategorie.valueOfNummer(nummer);
		String strafpunkte = new PunkteStringMapper().apply(aufgabenkategorie.getPenalty(klassenstufe));

		MkBiZaStatistikAufgabe statistikAufgabe = new MkBiZaStatistikAufgabe();
		statistikAufgabe.addAnzahleJeWertungscode(
			new MkBiZaGruppierungsitem().withName("falsch gelöst").withAnzahl(aufgabeErgenbnisItem.anzahlFalschGeloest()));
		statistikAufgabe.addAnzahleJeWertungscode(
			new MkBiZaGruppierungsitem().withName("richtig gelöst").withAnzahl(aufgabeErgenbnisItem.anzahlRichtigGeloest()));
		statistikAufgabe.addAnzahleJeWertungscode(
			new MkBiZaGruppierungsitem().withName("nicht gelöst").withAnzahl(aufgabeErgenbnisItem.anzahlNichtGeloest()));

		List<MkBiZaGruppierungsitem> anzahlenJeLoesungsbuchstabe = this.berechneAnzahlenJeLoesungsbuchstabe(zettelKlassenstufe,
			index);
		statistikAufgabe.setAnzahlenJeLoesungsbuchstabe(anzahlenJeLoesungsbuchstabe);
		statistikAufgabe.setNummer(nummer);
		statistikAufgabe.setStrafpunkte(strafpunkte);

		return statistikAufgabe;
	}

	List<MkBiZaGruppierungsitem> berechneAnzahlenJeLoesungsbuchstabe(final List<Loesungszettel> loesungszettels, final Integer index) {

		List<MkBiZaGruppierungsitem> result = new ArrayList<>();
		int anzahlA = 0;
		int anzahlB = 0;
		int anzahlC = 0;
		int anzahlD = 0;
		int anzahlE = 0;
		int anzahlN = 0;

		for (Loesungszettel loesungszettel : loesungszettels) {

			String antwortcode = loesungszettel.rohdaten().antwortcode();

			if (antwortcode != null) {

				String code = antwortcode.substring(index, index + 1);

				switch (code) {

				case "A":
					anzahlA++;
					break;

				case "B":
					anzahlB++;
					break;

				case "C":
					anzahlC++;
					break;

				case "D":
					anzahlD++;
					break;

				case "E":
					anzahlE++;
					break;

				case "N":
					anzahlN++;
					break;

				default:
					LOGGER.warn("Lösungszettel {} hat ein unerwartetes Zeichen {} im antwortcode - Aufruf über MkBiZa",
						loesungszettel.identifier(), antwortcode);
					MessagePayload messagePayload = MessagePayload.error("NotFound");
					Response response = Response.status(404).entity(messagePayload).build();
					throw new MkGatewayWebApplicationException(response);
				}
			}
		}

		result.add(new MkBiZaGruppierungsitem().withName("A").withAnzahl(anzahlA));
		result.add(new MkBiZaGruppierungsitem().withName("B").withAnzahl(anzahlB));
		result.add(new MkBiZaGruppierungsitem().withName("C").withAnzahl(anzahlC));
		result.add(new MkBiZaGruppierungsitem().withName("D").withAnzahl(anzahlD));
		result.add(new MkBiZaGruppierungsitem().withName("E").withAnzahl(anzahlE));
		result.add(new MkBiZaGruppierungsitem().withName("N").withAnzahl(anzahlN));

		return result;
	}

	Integer medianZuKlassenstufe(final Klassenstufe klassenstufe, final Wettbewerb wettbewerb) {

		switch (klassenstufe) {

		case IKID:
			if (wettbewerb.medianIkids() != null && !wettbewerb.medianIkids().equals(Integer.valueOf(0))) {

				return wettbewerb.medianIkids();
			}
			break;

		case EINS:
			if (wettbewerb.medianKlasseEins() != null && !wettbewerb.medianKlasseEins().equals(Integer.valueOf(0))) {

				return wettbewerb.medianKlasseEins();
			}
			break;

		case ZWEI:
			if (wettbewerb.medianKlasseZwei() != null && !wettbewerb.medianKlasseZwei().equals(Integer.valueOf(0))) {

				return wettbewerb.medianKlasseZwei();
			}
			break;

		default:
			throw new IllegalArgumentException("unerwartete Klassenstufe " + klassenstufe);
		}

		return null;

	}
}
