// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.auswertungen.StatistikTestUtils;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * StatistikWettbewerbServiceTest
 */
public class StatistikWettbewerbServiceTest {

	private LoesungszettelRepository loesungszettelRepository;

	private StatistikWettbewerbService statistikService;

	private List<Loesungszettel> wettbewerbLoesungszettel;

	@BeforeEach
	void setUp() throws Exception {

		wettbewerbLoesungszettel = StatistikTestUtils.loadTheLoesungszettel(2018);
		loesungszettelRepository = Mockito.mock(LoesungszettelRepository.class);
		statistikService = StatistikWettbewerbService.createForTest(loesungszettelRepository);
	}

	@Nested
	@DisplayName("MedianeTests")
	class MedianeTests {

		@Test
		void should_berechneGesamtmedianeWettbewerbReturnEmptyMap_when_keineLoesungszettel() {

			// Arrange
			final WettbewerbID wettbewerbID = new WettbewerbID(2015);
			Mockito.when(loesungszettelRepository.loadAllForWettbewerb(wettbewerbID)).thenReturn(new ArrayList<>());

			// Act
			Map<Klassenstufe, String> mediane = statistikService.berechneGesamtmedianeWettbewerb(wettbewerbID);

			// Assert
			assertTrue(mediane.isEmpty());

		}

		@Test
		void should_berechneGesamtmedianeWettbewerbReturnCompleteMap_when_alleLoesungszettel() {

			// Arrange
			final WettbewerbID wettbewerbID = new WettbewerbID(2019);

			Mockito.when(loesungszettelRepository.loadAllForWettbewerb(wettbewerbID))
				.thenReturn(wettbewerbLoesungszettel);

			// Act
			Map<Klassenstufe, String> mediane = statistikService.berechneGesamtmedianeWettbewerb(wettbewerbID);

			// Assert
			assertEquals(3, mediane.size());

			assertEquals("24,0", mediane.get(Klassenstufe.IKID));
			assertEquals("14,75", mediane.get(Klassenstufe.EINS));
			assertEquals("32,625", mediane.get(Klassenstufe.ZWEI));

		}

		@Test
		void should_berechneGesamtmedianeWettbewerbReturnPartialMap_when_alleNoIKids() {

			// Arrange
			final WettbewerbID wettbewerbID = new WettbewerbID(2019);

			List<Loesungszettel> alleLoesungszettel = wettbewerbLoesungszettel.stream()
				.filter(l -> l.klassenstufe() != Klassenstufe.IKID).collect(Collectors.toList());

			Mockito.when(loesungszettelRepository.loadAllForWettbewerb(wettbewerbID))
				.thenReturn(alleLoesungszettel);

			// Act
			Map<Klassenstufe, String> mediane = statistikService.berechneGesamtmedianeWettbewerb(wettbewerbID);

			// Assert
			assertEquals(2, mediane.size());

			assertNull(mediane.get(Klassenstufe.IKID));
			assertEquals("14,75", mediane.get(Klassenstufe.EINS));
			assertEquals("32,625", mediane.get(Klassenstufe.ZWEI));

		}

	}

	@Nested
	@DisplayName("Tests fuer die Klassenstufenstatistik")
	class StatistikKlassenstufeTests {

		@Test
		void should_erstelleStatistikWettbewerbKlassenstufeReturnTheStatsitik_when_datenVorhanden() {

			// Arrange
			Klassenstufe klassenstufe = Klassenstufe.EINS;
			List<Loesungszettel> loesungszettel = wettbewerbLoesungszettel.stream()
				.filter(l -> l.klassenstufe() == Klassenstufe.EINS).collect(Collectors.toList());

			WettbewerbID wettbewerbID = new WettbewerbID(2018);

			Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, klassenstufe))
				.thenReturn(loesungszettel);

			// Act
			Optional<GesamtpunktverteilungKlassenstufe> opt = statistikService.erstelleStatistikWettbewerbKlassenstufe(wettbewerbID,
				klassenstufe);

			// Assert
			assertTrue(opt.isPresent());

			GesamtpunktverteilungKlassenstufe verteilung = opt.get();
			assertEquals(5, verteilung.anzahlTeilnehmer());
			assertEquals("14,75", verteilung.getMedian());
			assertEquals("Die Punktverteilung wurde auf der Grundlage der zurückgesendeten Ergebnisse von 5 Kindern ermittelt.",
				verteilung.basis());
			assertEquals(
				"Die Kinder erhielten ein Startguthaben von 12,00 Punkten. Für falsch gelöste Aufgaben wurden abgezogen: 0,75 Punkte bei 3-Punkte-Aufgaben, 1 Punkt bei 4-Punkte-Aufgaben, 1,25 Punkte bei 5-Punkte-Aufgaben.",
				verteilung.bewertung());
			assertEquals("Lösungen je Aufgabe", verteilung.titel());
			assertEquals("Lösungen je Aufgabe", verteilung.sectionEinzelergebnisse());
			assertEquals(13, verteilung.gesamtpunktverteilungItems().size());
			assertEquals(12, verteilung.aufgabeErgebnisItems().size());
			assertEquals(4, verteilung.rohpunktItems().size());
			assertEquals(Klassenstufe.EINS, verteilung.klassenstufe());

		}

		@Test
		void should_erstelleStatistikWettbewerbKlassenstufeReturnEmptyOpt_when_keineDdatenVorhanden() {

			// Arrange
			Klassenstufe klassenstufe = Klassenstufe.EINS;

			WettbewerbID wettbewerbID = new WettbewerbID(2018);

			Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, klassenstufe))
				.thenReturn(new ArrayList<>());

			// Act
			Optional<GesamtpunktverteilungKlassenstufe> opt = statistikService.erstelleStatistikWettbewerbKlassenstufe(wettbewerbID,
				klassenstufe);

			// Assert
			assertTrue(opt.isEmpty());

		}

	}

	@Nested
	@DisplayName("Tests fuer die PDF")
	class StatistikPDFTests {

		@Test
		void should_erstelleStatistikWettbewerbKlassenstufeReturnTheStatsitik_when_datenVollstaendigVorhanden() throws Exception {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2018);

			{

				Klassenstufe klassenstufe = Klassenstufe.IKID;
				List<Loesungszettel> loesungszettel = wettbewerbLoesungszettel.stream()
					.filter(l -> l.klassenstufe() == Klassenstufe.IKID).collect(Collectors.toList());

				Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, klassenstufe))
					.thenReturn(loesungszettel);
			}

			{

				Klassenstufe klassenstufe = Klassenstufe.EINS;
				List<Loesungszettel> loesungszettel = wettbewerbLoesungszettel.stream()
					.filter(l -> l.klassenstufe() == Klassenstufe.EINS).collect(Collectors.toList());

				Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, klassenstufe))
					.thenReturn(loesungszettel);
			}

			{

				Klassenstufe klassenstufe = Klassenstufe.ZWEI;
				List<Loesungszettel> loesungszettel = wettbewerbLoesungszettel.stream()
					.filter(l -> l.klassenstufe() == Klassenstufe.ZWEI).collect(Collectors.toList());

				Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, klassenstufe))
					.thenReturn(loesungszettel);
			}

			// Act
			DownloadData downloadData = statistikService.erstelleStatistikPDFWettbewerb(wettbewerbID);

			// Assert
			assertEquals("minikaenguru_2018_gesamptunktverteilung.pdf", downloadData.filename());
			assertEquals(227440, downloadData.data().length);

			StatistikTestUtils.print(downloadData, true);

		}

		@Test
		void should_erstelleStatistikWettbewerbKlassenstufeReturnTheStatsitik_when_datenLueckenhaftVorhanden() throws Exception {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2018);

			{

				Klassenstufe klassenstufe = Klassenstufe.IKID;
				Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, klassenstufe))
					.thenReturn(new ArrayList<>());
			}

			{

				Klassenstufe klassenstufe = Klassenstufe.EINS;
				List<Loesungszettel> loesungszettel = wettbewerbLoesungszettel.stream()
					.filter(l -> l.klassenstufe() == Klassenstufe.EINS).collect(Collectors.toList());

				Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, klassenstufe))
					.thenReturn(loesungszettel);
			}

			{

				Klassenstufe klassenstufe = Klassenstufe.ZWEI;
				List<Loesungszettel> loesungszettel = wettbewerbLoesungszettel.stream()
					.filter(l -> l.klassenstufe() == Klassenstufe.ZWEI).collect(Collectors.toList());

				Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, klassenstufe))
					.thenReturn(loesungszettel);
			}

			// Act
			DownloadData downloadData = statistikService.erstelleStatistikPDFWettbewerb(wettbewerbID);

			// Assert
			assertEquals("minikaenguru_2018_gesamptunktverteilung.pdf", downloadData.filename());
			assertEquals(163669, downloadData.data().length);

			StatistikTestUtils.print(downloadData, true);

		}

		@Test
		void should_erstelleStatistikWettbewerbKlassenstufeReturnTheStatsitik_when_keineDatenVorhanden() throws Exception {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2018);

			{

				Klassenstufe klassenstufe = Klassenstufe.IKID;
				Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, klassenstufe))
					.thenReturn(new ArrayList<>());
			}

			{

				Klassenstufe klassenstufe = Klassenstufe.EINS;

				Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, klassenstufe))
					.thenReturn(new ArrayList<>());
			}

			{

				Klassenstufe klassenstufe = Klassenstufe.ZWEI;

				Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, klassenstufe))
					.thenReturn(new ArrayList<>());
			}

			// Act
			DownloadData downloadData = statistikService.erstelleStatistikPDFWettbewerb(wettbewerbID);

			// Assert
			assertEquals("minikaenguru_2018_gesamptunktverteilung.pdf", downloadData.filename());
			assertEquals(33305, downloadData.data().length);

			StatistikTestUtils.print(downloadData, true);

		}

	}

}
