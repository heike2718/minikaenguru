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

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.mk_gateway.domain.auth.s2s.MkGatewayAuthConfig;
import de.egladil.web.mk_gateway.domain.error.MkGatewayWebApplicationException;
import de.egladil.web.mk_gateway.domain.kataloge.LandPayloadComparator;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.kataloge.api.LandPayload;
import de.egladil.web.mk_gateway.domain.kataloge.dto.KatalogItem;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
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

		if (wettbewerb.status() != WettbewerbStatus.BEENDET) {

			LOGGER.warn("Wettbewerb {}: falscher Status {} - Aufruf über MkBiZa", jahr, wettbewerb.status());
			MessagePayload messagePayload = MessagePayload.error("NotFound");
			Response response = Response.status(404).entity(messagePayload).build();
			throw new MkGatewayWebApplicationException(response);
		}

		MkBiZaWettbewerbDetails result = new MkBiZaWettbewerbDetails();
		result.setJahr(jahr);

		List<Loesungszettel> alleLoesungszettel = loesungszettelRepository.loadAllForWettbewerb(wettbewerb.id());
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

			long anzahl = alleLoesungszettel.stream().filter(l -> l.teilnahmeIdentifier().teilnahmeart() == teilnahmeart).count();
			MkBiZaGruppierungsitem gruppierungsitem = new MkBiZaGruppierungsitem().withName(teilnahmeart.toString())
				.withAnzahl(anzahl);
			result.addKinderJeTeilnahmeart(gruppierungsitem);
		}

		final Map<String, List<Loesungszettel>> groupsByLaendern = this.groupByLaendern(
			alleLoesungszettel.stream().filter(l -> l.teilnahmeIdentifier().teilnahmeart() == Teilnahmeart.SCHULE).toList());

		List<LandPayload> laender = getLaender();

		for (LandPayload land : laender) {

			List<Loesungszettel> list = groupsByLaendern.get(land.kuerzel());

			if (list != null && !list.isEmpty()) {

				MkBiZaGruppierungsitem gruppierungsitem = new MkBiZaGruppierungsitem().withName(land.name())
					.withAnzahl(list.size());
				result.addSchulteilnahmenJeLand(gruppierungsitem);
			}
		}

		return result;

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
}
