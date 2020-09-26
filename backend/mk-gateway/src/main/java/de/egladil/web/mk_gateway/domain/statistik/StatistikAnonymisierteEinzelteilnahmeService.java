// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.pdf.PrivatteilnahmenuebersichtPDFGenerator;
import de.egladil.web.mk_gateway.domain.statistik.pdf.SchuluebersichtPDFGenerator;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.veranstalter.SchuleKatalogResponseMapper;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * StatistikAnonymisierteEinzelteilnahmeService
 */
@ApplicationScoped
public class StatistikAnonymisierteEinzelteilnahmeService {

	private static final Logger LOG = LoggerFactory.getLogger(StatistikAnonymisierteEinzelteilnahmeService.class);

	@Inject
	AuthorizationService authorizationService;

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	MkKatalogeResourceAdapter katalogeResourceAdapter;

	@Inject
	StatistikWettbewerbService statistikWettbewerbService;

	static StatistikAnonymisierteEinzelteilnahmeService createForTest(final AuthorizationService authService, final LoesungszettelRepository loesungszettelRepository, final MkKatalogeResourceAdapter katalogeResourceAdapter, final StatistikWettbewerbService statistikWettbewerbService) {

		StatistikAnonymisierteEinzelteilnahmeService result = new StatistikAnonymisierteEinzelteilnahmeService();
		result.authorizationService = authService;
		result.loesungszettelRepository = loesungszettelRepository;
		result.katalogeResourceAdapter = katalogeResourceAdapter;
		result.statistikWettbewerbService = statistikWettbewerbService;
		return result;
	}

	/**
	 * Ertsellt ein PDF-Dokument, das die Auswertungsdaten der gegebenen Einzelteilnahme enthält, sofern der USER diese sehen darf.
	 *
	 * @param  teilnahmeIdentifier
	 * @param  userUuid
	 * @return                     DownloadFata
	 */
	public DownloadData erstelleStatistikPDFEinzelteilnahme(final TeilnahmeIdentifier teilnahmeIdentifier, final String userUuid) {

		authorizationService.checkPermissionForTeilnahmenummer(new Identifier(userUuid),
			new Identifier(teilnahmeIdentifier.teilnahmenummer()));

		WettbewerbID wettbewerbID = new WettbewerbID(teilnahmeIdentifier.jahr());

		Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> verteilungenNachKlassenstufe = new HashMap<>();

		List<Loesungszettel> alleLoesungszettel = loesungszettelRepository.loadAll(teilnahmeIdentifier);

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			Optional<GesamtpunktverteilungKlassenstufe> opt = erstelleStatistikEinzelteilnahmeKlassenstufe(wettbewerbID,
				klassenstufe, alleLoesungszettel);

			if (opt.isPresent()) {

				verteilungenNachKlassenstufe.put(klassenstufe, opt.get());
			}
		}

		Map<Klassenstufe, String> gesamtmediane = statistikWettbewerbService.berechneGesamtmedianeWettbewerb(wettbewerbID);

		switch (teilnahmeIdentifier.teilnahmeart()) {

		case SCHULE:

			Optional<SchuleAPIModel> optSchule = this.findSchuleQuietly(teilnahmeIdentifier.teilnahmenummer());

			return new SchuluebersichtPDFGenerator().generierePdf(wettbewerbID, optSchule, verteilungenNachKlassenstufe,
				gesamtmediane);

		case PRIVAT:

			return new PrivatteilnahmenuebersichtPDFGenerator().generierePdf(wettbewerbID, verteilungenNachKlassenstufe,
				gesamtmediane);

		default:
			throw new BadRequestException("unerwartete Teilnahmeart " + teilnahmeIdentifier.teilnahmeart());
		}

	}

	Optional<GesamtpunktverteilungKlassenstufe> erstelleStatistikEinzelteilnahmeKlassenstufe(final WettbewerbID wettbewerbID, final Klassenstufe klassenstufe, final List<Loesungszettel> alleLoesungszettel) {

		if (alleLoesungszettel.isEmpty()) {

			return Optional.empty();
		}

		List<Loesungszettel> loesungszettelKlassenstufe = alleLoesungszettel.stream().filter(l -> l.klassenstufe() == klassenstufe)
			.collect(Collectors.toList());

		GesamtpunktverteilungKlassenstufe verteilungKlassenstufe = new StatistikKlassenstufeService()
			.generiereGesamtpunktverteilung(wettbewerbID, klassenstufe, loesungszettelKlassenstufe);

		return Optional.of(verteilungKlassenstufe);

	}

	/**
	 * @param  alleLoesungszettel
	 * @return
	 */
	Map<Klassenstufe, List<Loesungszettel>> sortByKlassenstufe(final List<Loesungszettel> alleLoesungszettel) {

		final Map<Klassenstufe, List<Loesungszettel>> result = new HashMap<>();

		alleLoesungszettel.forEach(z -> {

			Klassenstufe klassenstufe = z.klassenstufe();

			List<Loesungszettel> zettel = result.getOrDefault(klassenstufe, new ArrayList<>());
			zettel.add(z);
			result.put(klassenstufe, zettel);

		});

		return result;
	}

	Optional<SchuleAPIModel> findSchuleQuietly(final String schulkuerzel) {

		try {

			Response katalogeResponse = katalogeResourceAdapter.findSchulen(schulkuerzel);

			List<SchuleAPIModel> trefferliste = new SchuleKatalogResponseMapper().getSchulenFromKatalogeAPI(katalogeResponse);

			return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));

		} catch (MkGatewayRuntimeException e) {

			LOG.warn("Können Schule nicht ermitteln: {}", e.getMessage());
			return Optional.empty();
		}
	}

}
