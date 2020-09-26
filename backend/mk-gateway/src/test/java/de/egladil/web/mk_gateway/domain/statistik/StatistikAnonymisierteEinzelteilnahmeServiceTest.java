// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.auswertungen.StatistikTestUtils;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * StatistikAnonymisierteEinzelteilnahmeServiceTest
 */
public class StatistikAnonymisierteEinzelteilnahmeServiceTest {

	private AuthorizationService authService;

	private LoesungszettelRepository loesungszettelRepository;

	private MkKatalogeResourceAdapter katalogeResourceAdapter;

	private StatistikAnonymisierteEinzelteilnahmeService statistikService;

	private StatistikWettbewerbService statistikWettbewerbService;

	private List<Loesungszettel> wettbewerbLoesungszettel;

	@BeforeEach
	void setUp() throws Exception {

		authService = Mockito.mock(AuthorizationService.class);
		loesungszettelRepository = Mockito.mock(LoesungszettelRepository.class);
		katalogeResourceAdapter = Mockito.mock(MkKatalogeResourceAdapter.class);

		statistikWettbewerbService = StatistikWettbewerbService.createForTest(loesungszettelRepository);

		statistikService = StatistikAnonymisierteEinzelteilnahmeService.createForTest(authService, loesungszettelRepository,
			katalogeResourceAdapter, statistikWettbewerbService);

		wettbewerbLoesungszettel = StatistikTestUtils.loadTheLoesungszettel();

	}

	@Test
	void should_erstelleStatistikFuerEinzelteilnahme_call_AuthService() {

		// Arrange
		String teilnahmenummer = "ashichw";
		String userUuid = "ucgwug";
		Identifier userIdentifier = new Identifier(userUuid);
		Identifier teilnahmeID = new Identifier(teilnahmenummer);

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE.toString())
			.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(new WettbewerbID(2018));

		Mockito.when(authService.checkPermissionForTeilnahmenummer(userIdentifier, teilnahmeID))
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

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE.toString())
			.withTeilnahmenummer(schulkuerzel).withWettbewerbID(new WettbewerbID(2018));

		Mockito.when(authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUUID), new Identifier(schulkuerzel)))
			.thenReturn(Boolean.TRUE);

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
		assertEquals(219900, downloadData.data().length);

		StatistikTestUtils.print(downloadData, false);
	}

	@Test
	void should_sortByKlassenstufe_work() throws Exception {

		// Arrange
		List<Loesungszettel> alleLoesungszettel = StatistikTestUtils.loadTheLoesungszettel();
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

	@Test
	void should_findSchuleQuietlyReturnOptionalNotEmpty_when_mkKatalogeReturnsTheSchule() {

		// Arrange
		String schulkuerzel = "12345";
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

		// Act
		Optional<SchuleAPIModel> opt = statistikService.findSchuleQuietly(schulkuerzel);

		// Assert
		assertTrue(opt.isPresent());

	}

	@Test
	void should_findSchuleQuietlyReturnOptionalEmpty_when_mkKatalogeReturnsEmptyList() {

		// Arrange
		String schulkuerzel = "12345";
		List<Map<String, Object>> data = new ArrayList<>();

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), data)).build();

		Mockito.when(katalogeResourceAdapter.findSchulen(schulkuerzel)).thenReturn(response);

		// Act
		Optional<SchuleAPIModel> opt = statistikService.findSchuleQuietly(schulkuerzel);

		// Assert
		assertTrue(opt.isEmpty());

	}

	@Test
	void should_findSchuleQuietlyReturnOptionalEmpty_when_mkKatalogeReturnsThrowsException() {

		// Arrange
		String schulkuerzel = "12345";

		Mockito.when(katalogeResourceAdapter.findSchulen(schulkuerzel))
			.thenThrow(new MkGatewayRuntimeException("schlimm schlim schlimm"));

		// Act
		Optional<SchuleAPIModel> opt = statistikService.findSchuleQuietly(schulkuerzel);

		// Assert
		assertTrue(opt.isEmpty());

	}

}
