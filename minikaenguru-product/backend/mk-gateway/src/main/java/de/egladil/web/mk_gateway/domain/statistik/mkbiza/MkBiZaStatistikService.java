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
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
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
	 * Aggregiert die Statistikdaten für den gegebenen Wettbewerb.
	 *
	 * @param  jahr
	 *                                          Integer das Wettbewerbsjahr
	 * @return                                  MkBiZaWettbewerbDetails
	 * @throws MkGatewayWebApplicationException
	 *                                          wird im MkGatewayExceptionMapper verarbeitet.
	 */
	public MkBiZaWettbewerbDetails getStatistik(final Integer jahr) throws MkGatewayWebApplicationException {

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

		for (Teilnahmeart teilnahmeart : Teilnahmeart.values()) {

			{

				long anzahl = alleLoesungszettel.stream().filter(l -> l.teilnahmeIdentifier().teilnahmeart() == teilnahmeart)
					.count();
				MkBiZaGruppierungsitem gruppierungsitem = new MkBiZaGruppierungsitem().withName(teilnahmeart.toString())
					.withAnzahl(anzahl);
				result.addKinderJeTeilnahmeart(gruppierungsitem);
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
				result.addSchulkinderJeLand(gruppierungsitem);

			}

			long anzahlSchulenImLand = schulen.stream().filter(s -> s.kuerzelLand().equals(land.kuerzel())).count();

			if (anzahlSchulenImLand > 0) {

				result.addSchulenJeLand(new MkBiZaGruppierungsitem().withName(land.name()).withAnzahl(anzahlSchulenImLand));
			}
		}

		if (wettbewerb.medianIkids() != null && !wettbewerb.medianIkids().equals(Integer.valueOf(0))) {

			result.addMedianeJeKlassenstufe(
				new MkBiZaGruppierungsitem().withAnzahl(Long.valueOf(wettbewerb.medianIkids())).withName("Inklusion"));
		}

		if (wettbewerb.medianKlasseEins() != null && !wettbewerb.medianKlasseEins().equals(Integer.valueOf(0))) {

			result.addMedianeJeKlassenstufe(
				new MkBiZaGruppierungsitem().withAnzahl(Long.valueOf(wettbewerb.medianKlasseEins())).withName("Klasse 1"));
		}

		if (wettbewerb.medianKlasseZwei() != null && !wettbewerb.medianKlasseZwei().equals(Integer.valueOf(0))) {

			result.addMedianeJeKlassenstufe(
				new MkBiZaGruppierungsitem().withAnzahl(Long.valueOf(wettbewerb.medianKlasseZwei())).withName("Klasse 2"));
		}

		List<MkBiZaGruppierungsitem> teilnehmendeSchulen = result.getSchulenJeLand();

		long anzahlTeilnehmendeSchulen = teilnehmendeSchulen.stream().mapToLong(MkBiZaGruppierungsitem::getAnzahl).sum();

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
	 * Zählt durch, wie die gegebene Aufgabe gelöst wurde.
	 *
	 * @param  wettbewerbsjahr
	 * @param  klassenstufe
	 * @param  nummer
	 * @return                 StatistikAufgabe
	 */
	public StatistikAufgabe getStatistikZuAufgabe(final Integer jahr, final Klassenstufe klassenstufe, final String nummer) {

		Optional<Wettbewerb> optWettbewerb = wettbewerbRepository.wettbewerbMitID(new WettbewerbID(jahr));

		if (optWettbewerb.isEmpty()) {

			LOGGER.warn("Unbekanntes Wettbewerbsjahr {} - Aufruf über MkBiZa", jahr);
			MessagePayload messagePayload = MessagePayload.error("NotFound");
			Response response = Response.status(404).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		Wettbewerb wettbewerb = optWettbewerb.get();

		if (wettbewerb.status() != WettbewerbStatus.BEENDET) {

			LOGGER.warn("Wettbewerb {}: falscher Status {} - Aufruf über MkBiZa", jahr, wettbewerb.status());
			MessagePayload messagePayload = MessagePayload.error("NotFound");
			Response response = Response.status(404).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		List<Loesungszettel> zettelKlassenstufe = loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerb.id(),
			klassenstufe);

		Map<String, Integer> aufgabennummernWithWertungscodeIndex = klassenstufe
			.getAufgabennummernWithWertungscodeIndex(jahr);

		Integer index = aufgabennummernWithWertungscodeIndex.get(nummer);

		if (index == null) {

			LOGGER.warn("Wettbewerb {}: falsche nummer {} - Aufruf über MkBiZa", jahr, nummer);
			MessagePayload messagePayload = MessagePayload.error("NotFound");
			Response response = Response.status(404).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		AufgabeErgebnisItem aufgabeErgenbnisItem = aufgabeErgebnisRechner.berechneAufgabeErgebnisItem(nummer, index,
			zettelKlassenstufe);

		return new StatistikAufgabe().withAnzahlFalsch(aufgabeErgenbnisItem.anzahlFalschGeloest())
			.withAnzahlNicht(aufgabeErgenbnisItem.anzahlNichtGeloest())
			.withAnzahlRichtig(aufgabeErgenbnisItem.anzahlRichtigGeloest());
	}
}
