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
import de.egladil.web.mk_gateway.domain.error.StatistikKeineDatenException;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.pdf.PrivatteilnahmenuebersichtPDFGenerator;
import de.egladil.web.mk_gateway.domain.statistik.pdf.SchuluebersichtPDFGenerator;
import de.egladil.web.mk_gateway.domain.statistik.pdf.StatistikPDFGenerator;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.veranstalter.SchuleKatalogResponseMapper;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * StatistikService
 */
@ApplicationScoped
public class StatistikService {

	private static final Logger LOG = LoggerFactory.getLogger(StatistikService.class);

	@Inject
	AuthorizationService authorizationService;

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	MkKatalogeResourceAdapter katalogeResourceAdapter;

	static StatistikService createForTest(final AuthorizationService authService) {

		StatistikService result = new StatistikService();
		result.authorizationService = authService;
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

		Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> verteilungenNachKlassenstufe = this.berechneDaten(teilnahmeIdentifier);
		Map<Klassenstufe, String> gesamtmediane = berechneGesamtmedianeWettbewerb(wettbewerbID);

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

	public DownloadData erstelleStatistikPDFWettbewerb(final WettbewerbID wettbewerbID) {

		Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> gesamtpunktverteilungenNachKlassenstufe = new HashMap<>();

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			Optional<GesamtpunktverteilungKlassenstufe> opt = this.erstelleStatistikWettbewerbKlassenstufe(wettbewerbID,
				klassenstufe);

			if (opt.isPresent()) {

				gesamtpunktverteilungenNachKlassenstufe.put(klassenstufe, opt.get());
			}
		}

		byte[] pdf = new StatistikPDFGenerator().generiereGesamtpunktverteilungWettbewerb(wettbewerbID,
			gesamtpunktverteilungenNachKlassenstufe);

		return new DownloadData("minikaenguru_" + wettbewerbID.toString() + "_gesamptunktverteilung.pdf", pdf);
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

	Optional<GesamtpunktverteilungKlassenstufe> erstelleStatistik(final WettbewerbID wettbewerbID, final Klassenstufe klassenstufe, final List<Loesungszettel> alleLoesungszettel) {

		if (alleLoesungszettel.isEmpty()) {

			return Optional.empty();
		}

		GesamtpunktverteilungKlassenstufe verteilungKlassenstufe = new StatistikKlassenstufeService()
			.generiereGesamtpunktverteilung(wettbewerbID, klassenstufe, alleLoesungszettel);

		return Optional.of(verteilungKlassenstufe);

	}

	Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> berechneDaten(final TeilnahmeIdentifier teilnahmeIdentifier) {

		List<Loesungszettel> alleLoesungszettel = loesungszettelRepository.loadAll(teilnahmeIdentifier);

		WettbewerbID wettbewerbID = new WettbewerbID(teilnahmeIdentifier.jahr());

		if (alleLoesungszettel.isEmpty()) {

			throw new StatistikKeineDatenException();
		}

		Map<Klassenstufe, List<Loesungszettel>> loesungszettelNachKlassenstufen = this.sortByKlassenstufe(alleLoesungszettel);

		Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> verteilungenNachKlassenstufe = new HashMap<>();

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			List<Loesungszettel> zettel = loesungszettelNachKlassenstufen.get(klassenstufe);

			if (zettel != null) {

				Optional<GesamtpunktverteilungKlassenstufe> optVerteilung = this.erstelleStatistik(wettbewerbID, klassenstufe,
					zettel);

				if (optVerteilung.isPresent()) {

					GesamtpunktverteilungKlassenstufe verteilungKlassenstufe = new StatistikKlassenstufeService()
						.generiereGesamtpunktverteilung(wettbewerbID, klassenstufe, zettel);

					verteilungenNachKlassenstufe.put(klassenstufe, verteilungKlassenstufe);
				}
			}

		}

		return verteilungenNachKlassenstufe;
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

	private Optional<SchuleAPIModel> findSchuleQuietly(final String schulkuerzel) {

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
