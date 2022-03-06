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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kataloge.SchulkatalogService;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.api.MedianeAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.pdf.PrivatteilnahmenuebersichtPDFGenerator;
import de.egladil.web.mk_gateway.domain.statistik.pdf.SchuluebersichtPDFGenerator;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * StatistikAnonymisierteEinzelteilnahmeService
 */
@RequestScoped
public class StatistikAnonymisierteEinzelteilnahmeService {

	@Inject
	AuthorizationService authorizationService;

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	StatistikWettbewerbService statistikWettbewerbService;

	@Inject
	SchulkatalogService schulkatalogService;

	static StatistikAnonymisierteEinzelteilnahmeService createForTest(final AuthorizationService authService, final LoesungszettelRepository loesungszettelRepository, final SchulkatalogService schulkatalogService, final StatistikWettbewerbService statistikWettbewerbService) {

		StatistikAnonymisierteEinzelteilnahmeService result = new StatistikAnonymisierteEinzelteilnahmeService();
		result.authorizationService = authService;
		result.loesungszettelRepository = loesungszettelRepository;
		result.schulkatalogService = schulkatalogService;
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

		authorizationService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(userUuid),
			new Identifier(teilnahmeIdentifier.teilnahmenummer()),
			"[erstelleStatistikPDFEinzelteilnahme - " + teilnahmeIdentifier.teilnahmenummer() + "]");

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

		MedianeAPIModel gesamtmediane = statistikWettbewerbService.berechneGesamtmedianeWettbewerb(wettbewerbID);

		switch (teilnahmeIdentifier.teilnahmeart()) {

		case SCHULE:

			Optional<SchuleAPIModel> optSchule = this.schulkatalogService.findSchuleQuietly(teilnahmeIdentifier.teilnahmenummer());

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

		if (loesungszettelKlassenstufe.isEmpty()) {

			return Optional.empty();
		}

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

			List<Loesungszettel> list = result.get(klassenstufe);

			List<Loesungszettel> zettel = list == null ? new ArrayList<>() : list;
			zettel.add(z);
			result.put(klassenstufe, zettel);

		});

		return result;
	}

}
