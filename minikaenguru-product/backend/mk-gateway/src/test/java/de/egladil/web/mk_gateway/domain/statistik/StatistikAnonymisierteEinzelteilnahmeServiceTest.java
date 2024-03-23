// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auswertungen.StatistikTestUtils;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.kataloge.SchulkatalogService;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * StatistikAnonymisierteEinzelteilnahmeServiceTest
 */
public class StatistikAnonymisierteEinzelteilnahmeServiceTest extends AbstractDomainServiceTest {

	private AuthorizationService authService;

	private LoesungszettelRepository loesungszettelRepository;

	private MkKatalogeResourceAdapter katalogeResourceAdapter;

	private StatistikAnonymisierteEinzelteilnahmeService statistikService;

	private StatistikWettbewerbService statistikWettbewerbService;

	private List<Loesungszettel> wettbewerbLoesungszettel;

	private SchulkatalogService schulkatalogService;

	@BeforeEach
	public void setUp() {

		authService = Mockito.mock(AuthorizationService.class);
		loesungszettelRepository = Mockito.mock(LoesungszettelRepository.class);
		katalogeResourceAdapter = Mockito.mock(MkKatalogeResourceAdapter.class);

		statistikWettbewerbService = StatistikWettbewerbService.createForTest(loesungszettelRepository, getWettbewerbService(),
			schulkatalogService, getTeilnahmenRepository());

		schulkatalogService = Mockito.mock(SchulkatalogService.class);
		statistikService = StatistikAnonymisierteEinzelteilnahmeService.createForTest(authService, loesungszettelRepository,
			SchulkatalogService.createForTest(katalogeResourceAdapter), statistikWettbewerbService);

		try {

			wettbewerbLoesungszettel = StatistikTestUtils.loadTheLoesungszettel(2018);
		} catch (Exception e) {

			e.printStackTrace();
			fail("Exception beim test setup");
		}

	}

	@Test
	void should_erstelleStatistikPDFEinzelteilnahme_call_AuthService() {

		// Arrange
		String teilnahmenummer = "ashichw";
		String userUuid = "ucgwug";
		Identifier userIdentifier = new Identifier(userUuid);
		Identifier teilnahmeID = new Identifier(teilnahmenummer);

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(new WettbewerbID(2018));

		Mockito
			.when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(userIdentifier, teilnahmeID,
				"[erstelleStatistikPDFEinzelteilnahme - " + teilnahmenummer + "]"))
			.thenThrow(new AccessDeniedException());

		// Act
		try {

			statistikService.erstelleStatistikPDFEinzelteilnahme(teilnahmeIdentifier, userUuid);
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			// nix
		}

	}

	@Test
	void should_erstelleStatistikPDF_fuerSchulteilnahme_generateThePDF_whenDatenVollstaendig() throws Exception {

		// Arrange
		String schulkuerzel = "12345";
		String veranstalterUUID = "aaaaa";

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer(schulkuerzel).withWettbewerbID(new WettbewerbID(2018));

		Mockito
			.when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(veranstalterUUID), new Identifier(schulkuerzel),
				"[erstelleStatistikPDFEinzelteilnahme - " + schulkuerzel + "]"))
			.thenReturn(Rolle.LEHRER);

		List<Map<String, Object>> data = new ArrayList<>();

		{

			Map<String, Object> schuleWettbewerbMap = new HashMap<>();

			schuleWettbewerbMap.put("kuerzel", schulkuerzel);
			schuleWettbewerbMap.put("name", "David-Hilbert-Schule");
			schuleWettbewerbMap.put("ort", "Göttingen");
			schuleWettbewerbMap.put("land", "Niedersachsen");
			schuleWettbewerbMap.put("kuerzelLand", "DE-NI");
			schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.FALSE);

			data.add(schuleWettbewerbMap);
		}

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), data)).build();

		Mockito.when(katalogeResourceAdapter.findSchulen(schulkuerzel)).thenReturn(response);
		Mockito.when(loesungszettelRepository.loadAll(teilnahmeIdentifier)).thenReturn(wettbewerbLoesungszettel);

		// Act
		DownloadData downloadData = statistikService.erstelleStatistikPDFEinzelteilnahme(teilnahmeIdentifier,
			veranstalterUUID.toString());

		// Assert
		assertEquals("minikaenguru_2018_schulstatistik.pdf", downloadData.filename());
		assertEquals(219034, downloadData.data().length);

		StatistikTestUtils.print(downloadData, true);
	}

	@Test
	void should_erstelleStatistikPDF_fuerPrivatteilnahme_generateThePDF_whenDatenVollstaendig() throws Exception {

		// Arrange
		String schulkuerzel = "12345";
		String veranstalterUUID = "aaaaa";

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.PRIVAT)
			.withTeilnahmenummer(schulkuerzel).withWettbewerbID(new WettbewerbID(2018));

		Mockito
			.when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(veranstalterUUID), new Identifier(schulkuerzel),
				"[erstelleStatistikPDFEinzelteilnahme - " + schulkuerzel + "]"))
			.thenReturn(Rolle.PRIVAT);

		Mockito.when(loesungszettelRepository.loadAll(teilnahmeIdentifier)).thenReturn(wettbewerbLoesungszettel);

		// Act
		DownloadData downloadData = statistikService.erstelleStatistikPDFEinzelteilnahme(teilnahmeIdentifier,
			veranstalterUUID.toString());

		// Assert
		assertEquals("minikaenguru_2018_statistik.pdf", downloadData.filename());
		assertEquals(218625, downloadData.data().length);

		StatistikTestUtils.print(downloadData, true);
	}

	@Test
	void should_erstelleStatistikEinzelteilnahmeKlassenstufeReturnEmpty_when_KeineLoesungszettelMitKlassenstufe() {

		// Arrange
		Klassenstufe klassenstufe = Klassenstufe.EINS;
		List<Loesungszettel> alleLoesungszettel = wettbewerbLoesungszettel.stream().filter(l -> l.klassenstufe() != klassenstufe)
			.collect(Collectors.toList());
		WettbewerbID wettbewerbID = new WettbewerbID(2018);

		// Act
		Optional<GesamtpunktverteilungKlassenstufe> opt = statistikService
			.erstelleStatistikEinzelteilnahmeKlassenstufe(wettbewerbID, klassenstufe, alleLoesungszettel);

		// Assert
		assertTrue(opt.isEmpty());
	}

	@Test
	void should_sortByKlassenstufe_work() throws Exception {

		// Arrange
		List<Loesungszettel> alleLoesungszettel = StatistikTestUtils.loadTheLoesungszettel(2018);
		assertEquals(12, alleLoesungszettel.size());

		// Act
		Map<Klassenstufe, List<Loesungszettel>> klassenstufeLoesungszettelMap = new StatistikAnonymisierteEinzelteilnahmeService()
			.sortByKlassenstufe(alleLoesungszettel);

		// Assert
		assertEquals(3, klassenstufeLoesungszettelMap.size());

		List<Loesungszettel> zettelIKIDS = klassenstufeLoesungszettelMap.get(Klassenstufe.IKID);
		assertEquals(3, zettelIKIDS.size());

		List<Loesungszettel> zettelEINS = klassenstufeLoesungszettelMap.get(Klassenstufe.EINS);
		assertEquals(5, zettelEINS.size());

		List<Loesungszettel> zettelZWEI = klassenstufeLoesungszettelMap.get(Klassenstufe.ZWEI);
		assertEquals(4, zettelZWEI.size());

	}

}
