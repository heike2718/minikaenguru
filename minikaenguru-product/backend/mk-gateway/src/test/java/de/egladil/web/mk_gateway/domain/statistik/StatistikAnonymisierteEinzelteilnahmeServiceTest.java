// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auswertungen.StatistikTestUtils;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.kataloge.SchulkatalogService;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.api.MedianAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.api.MedianeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * StatistikAnonymisierteEinzelteilnahmeServiceTest
 */
@QuarkusTest
public class StatistikAnonymisierteEinzelteilnahmeServiceTest {

	List<Loesungszettel> wettbewerbLoesungszettel;

	@InjectMock
	AuthorizationService authService;

	@InjectMock
	LoesungszettelRepository loesungszettelRepository;

	@InjectMock
	StatistikWettbewerbService statistikWettbewerbService;

	@InjectMock
	SchulkatalogService schulkatalogService;

	@Inject
	private StatistikAnonymisierteEinzelteilnahmeService statistikService;

	@BeforeEach
	public void setUp() {

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

		Mockito.when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(userIdentifier, teilnahmeID,
			"[erstelleStatistikPDFEinzelteilnahme - " + teilnahmenummer + "]")).thenThrow(new AccessDeniedException());

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

		MedianeAPIModel gesamtmediane = new MedianeAPIModel();
		gesamtmediane.addMedian(new MedianAPIModel(Klassenstufe.IKID, "30,00", 42));
		gesamtmediane.addMedian(new MedianAPIModel(Klassenstufe.EINS, "24,75", 422));
		gesamtmediane.addMedian(new MedianAPIModel(Klassenstufe.ZWEI, "22.25", 4242));

		when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(veranstalterUUID),
				new Identifier(schulkuerzel), "[erstelleStatistikPDFEinzelteilnahme - " + schulkuerzel + "]"))
			.thenReturn(Rolle.LEHRER);

		SchuleAPIModel schule = new SchuleAPIModel().withAktuellAngemeldet(false).withKuerzel(schulkuerzel)
			.withName("David-Hilbert-Schule").withOrt("Göttingen").withLand("Niedersachsen").withKuerzelLand("DE-NI");

		when(schulkatalogService.findSchule(schulkuerzel)).thenReturn(Optional.of(schule));
		when(loesungszettelRepository.loadAll(teilnahmeIdentifier)).thenReturn(wettbewerbLoesungszettel);
		when(statistikWettbewerbService.berechneGesamtmedianeWettbewerb(any(WettbewerbID.class))).thenReturn(gesamtmediane);

		// Act
		DownloadData downloadData = statistikService.erstelleStatistikPDFEinzelteilnahme(teilnahmeIdentifier,
			veranstalterUUID.toString());

		// Assert
		assertEquals("minikaenguru_2018_schulstatistik.pdf", downloadData.filename());
		assertEquals(219405, downloadData.data().length);

		StatistikTestUtils.print(downloadData, true);
	}

	@Test
	void should_erstelleStatistikPDF_fuerPrivatteilnahme_generateThePDF_whenDatenVollstaendig() throws Exception {

		// Arrange
		String schulkuerzel = "12345";
		String veranstalterUUID = "aaaaa";

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.PRIVAT)
			.withTeilnahmenummer(schulkuerzel).withWettbewerbID(new WettbewerbID(2018));

		MedianeAPIModel gesamtmediane = new MedianeAPIModel();
		gesamtmediane.addMedian(new MedianAPIModel(Klassenstufe.IKID, "30,00", 42));
		gesamtmediane.addMedian(new MedianAPIModel(Klassenstufe.EINS, "24,75", 422));
		gesamtmediane.addMedian(new MedianAPIModel(Klassenstufe.ZWEI, "22.25", 4242));

		when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(veranstalterUUID),
			new Identifier(schulkuerzel), "[erstelleStatistikPDFEinzelteilnahme - " + schulkuerzel + "]")).thenReturn(Rolle.PRIVAT);

		when(loesungszettelRepository.loadAll(teilnahmeIdentifier)).thenReturn(wettbewerbLoesungszettel);

		when(statistikWettbewerbService.berechneGesamtmedianeWettbewerb(any(WettbewerbID.class))).thenReturn(gesamtmediane);

		// Act
		DownloadData downloadData = statistikService.erstelleStatistikPDFEinzelteilnahme(teilnahmeIdentifier,
			veranstalterUUID.toString());

		// Assert
		assertEquals("minikaenguru_2018_statistik.pdf", downloadData.filename());
		assertEquals(218993, downloadData.data().length);

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
