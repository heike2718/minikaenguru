// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.auswertungen.StatistikTestUtils;
import de.egladil.web.mk_gateway.domain.error.StatistikKeineDatenException;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.api.ProzentrangAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;

/**
 * ProzentrangEinzelpunktzahlServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class ProzentrangEinzelpunktzahlServiceTest {

	@Mock
	LoesungszettelRepository loesungszettelRepository;

	@Mock
	WettbewerbRepository wettbewerbRepository;

	@InjectMocks
	ProzentrangEinzelpunktzahlService service;

	@Nested
	class CheckParameterTests {

		@Test
		void should_throwStatistikKeineDatenException_when_IKidsAndJahrVor2019() {

			// Arrange
			Klassenstufe klassenstufe = Klassenstufe.IKID;
			int punkte = 2575;

			// Act
			for (int i = 2005; i < 2019; i++)
				try {

					Integer wettbewerbsjahr = Integer.valueOf(i);

					service.checkParameters(wettbewerbsjahr, klassenstufe, punkte);
					fail("jahr = " + i + ": keine StatistikKeineDatenException");

				} catch (StatistikKeineDatenException e) {

				}
		}

		@Test
		void should_throwStatistikKeineDatenException_when_KlassenstufeEinsAndJahrVor2014() {

			// Arrange
			Klassenstufe klassenstufe = Klassenstufe.EINS;
			int punkte = 2575;

			// Act
			for (int i = 2005; i < 2014; i++)
				try {

					Integer wettbewerbsjahr = Integer.valueOf(i);

					service.checkParameters(wettbewerbsjahr, klassenstufe, punkte);
					fail("jahr = " + i + ": keine StatistikKeineDatenException");

				} catch (StatistikKeineDatenException e) {

				}
		}

		@Test
		void should_throwStatistikKeineDatenException_when_KlassenstufeZweiAndJahrVor2010() {

			// Arrange
			Klassenstufe klassenstufe = Klassenstufe.ZWEI;
			int punkte = 2575;

			// Act
			for (int i = 2005; i < 2010; i++)
				try {

					Integer wettbewerbsjahr = Integer.valueOf(i);

					service.checkParameters(wettbewerbsjahr, klassenstufe, punkte);
					fail("jahr = " + i + ": keine StatistikKeineDatenException");

				} catch (StatistikKeineDatenException e) {

				}
		}

		@Test
		void should_throwIllegalArgumentException_when_WettbewerbsjahrNull() {

			// Arrange
			Integer wettbewerbsjahr = null;
			Klassenstufe klassenstufe = Klassenstufe.EINS;
			int punkte = 2575;

			// Act
			try {

				service.checkParameters(wettbewerbsjahr, klassenstufe, punkte);
				fail("keine IllegalArgumentException");

			} catch (IllegalArgumentException e) {

				assertEquals("wettbewerbsjahr null", e.getMessage());
			}

		}

		@Test
		void should_throwIllegalArgumentException_when_KlassenstufeNull() {

			// Arrange
			Integer wettbewerbsjahr = Integer.valueOf(2018);
			Klassenstufe klassenstufe = null;
			int punkte = 2575;

			// Act
			try {

				service.checkParameters(wettbewerbsjahr, klassenstufe, punkte);
				fail("keine IllegalArgumentException");

			} catch (IllegalArgumentException e) {

				assertEquals("klassenstufe null", e.getMessage());
			}

		}

		@Test
		void should_throwInvalidInputException_when_PunkteKleiner0() {

			// Arrange
			Integer wettbewerbsjahr = Integer.valueOf(2018);
			Klassenstufe klassenstufe = Klassenstufe.IKID;
			int punkte = -1;

			// Act
			try {

				service.checkParameters(wettbewerbsjahr, klassenstufe, punkte);
				fail("keine InvalidInputException");

			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();
				assertEquals("Weniger als 0 Punkte können nicht erzielt werden.", responsePayload.getMessage().getMessage());
				assertEquals("ERROR", responsePayload.getMessage().getLevel());
			}

		}

		@Test
		void should_throwInvalidInputException_when_klassenstufeIKIDAndPunkteGroesser3600() {

			// Arrange
			Integer wettbewerbsjahr = Integer.valueOf(2019);
			Klassenstufe klassenstufe = Klassenstufe.IKID;
			int punkte = 3601;

			// Act
			try {

				service.checkParameters(wettbewerbsjahr, klassenstufe, punkte);
				fail("keine InvalidInputException");

			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();
				assertEquals("Für Klassenstufe IKID muss die Eingabe zwischen 0 und 36 liegen.",
					responsePayload.getMessage().getMessage());
				assertEquals("ERROR", responsePayload.getMessage().getLevel());
			}

		}

		@Test
		void should_throwInvalidInputException_when_klassenstufeEINSAndPunkteGroesser6000() {

			// Arrange
			Integer wettbewerbsjahr = Integer.valueOf(2019);
			Klassenstufe klassenstufe = Klassenstufe.EINS;
			int punkte = 6001;

			// Act
			try {

				service.checkParameters(wettbewerbsjahr, klassenstufe, punkte);
				fail("keine InvalidInputException");

			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();
				assertEquals("Für Klassenstufe EINS muss die Eingabe zwischen 0 und 60 liegen.",
					responsePayload.getMessage().getMessage());
				assertEquals("ERROR", responsePayload.getMessage().getLevel());
			}

		}

		@Test
		void should_throwInvalidInputException_when_klassenstufeZWEIAndPunkteGroesser6000() {

			// Arrange
			Integer wettbewerbsjahr = Integer.valueOf(2019);
			Klassenstufe klassenstufe = Klassenstufe.ZWEI;
			int punkte = 7501;

			// Act
			try {

				service.checkParameters(wettbewerbsjahr, klassenstufe, punkte);
				fail("keine InvalidInputException");

			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();
				assertEquals("Für Klassenstufe ZWEI muss die Eingabe zwischen 0 und 75 liegen.",
					responsePayload.getMessage().getMessage());
				assertEquals("ERROR", responsePayload.getMessage().getLevel());
			}

		}
	}

	@Nested
	class ExceptionTests {

		@Test
		void should_berechneProzentrangThrowStatistikKeineDatenException_when_WettbewerbNochNichtBeendet() {

			// Arrange
			Integer wettbewerbsjahr = Integer.valueOf(2021);
			Klassenstufe klassenstufe = Klassenstufe.EINS;
			int punkte = 2575;

			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(wettbewerbsjahr)).withStatus(WettbewerbStatus.DOWNLOAD_PRIVAT);

			Mockito.when(wettbewerbRepository.wettbewerbMitID(new WettbewerbID(wettbewerbsjahr)))
				.thenReturn(Optional.of(wettbewerb));

			// Act
			try {

				service.berechneProzentrang(wettbewerbsjahr, klassenstufe, punkte);
				fail("keine StatistikKeineDatenException");

			} catch (StatistikKeineDatenException e) {

			}
		}

		@Test
		void should_berechneProzentrangThrowInvalidInputException_when_JahrNachAktuellemJahr() {

			// Arrange
			Integer wettbewerbsjahr = Integer.valueOf(2032);
			Klassenstufe klassenstufe = Klassenstufe.EINS;
			int punkte = 2575;

			Mockito.when(wettbewerbRepository.wettbewerbMitID(new WettbewerbID(wettbewerbsjahr)))
				.thenReturn(Optional.empty());

			// Act
			try {

				service.berechneProzentrang(wettbewerbsjahr, klassenstufe, punkte);
				fail("keine InvalidInputException");

			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();
				assertEquals("Es liegen nur Daten ab 2010 bis 2022 vor", responsePayload.getMessage().getMessage());
				assertEquals("WARN", responsePayload.getMessage().getLevel());
			}
		}

		@Test
		void should_berechneProzentrangThrowStatistikKeineDatenException_when_keineLoesungszettelZuKlassensufe() {

			// Arrange
			Integer wettbewerbsjahr = Integer.valueOf(2021);
			Klassenstufe klassenstufe = Klassenstufe.ZWEI;
			int punkte = 2575;

			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(wettbewerbsjahr)).withStatus(WettbewerbStatus.BEENDET);

			Mockito.when(wettbewerbRepository.wettbewerbMitID(new WettbewerbID(wettbewerbsjahr)))
				.thenReturn(Optional.of(wettbewerb));

			Mockito
				.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(new WettbewerbID(wettbewerbsjahr),
					Klassenstufe.ZWEI))
				.thenReturn(new ArrayList<>());

			// Act
			try {

				service.berechneProzentrang(wettbewerbsjahr, klassenstufe, punkte);
				fail("keine StatistikKeineDatenException");

			} catch (StatistikKeineDatenException e) {

			}
		}
	}

	@Nested
	class MitErgebnisTests {

		@Test
		void should_berechneProzentrangReturnExakt_when_exakterTreffer() throws Exception {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2019);
			Klassenstufe klassenstufe = Klassenstufe.ZWEI;
			int punkte = 5250;
			List<Loesungszettel> alleLoesungszettel = getLoesungszettelKlasse2();

			Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, Klassenstufe.ZWEI))
				.thenReturn(alleLoesungszettel);

			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID).withStatus(WettbewerbStatus.BEENDET);

			Mockito.when(wettbewerbRepository.wettbewerbMitID(wettbewerbID))
				.thenReturn(Optional.of(wettbewerb));

			// Act
			ProzentrangAPIModel result = service.berechneProzentrang(wettbewerbID.jahr(), klassenstufe, punkte);

			// Assert
			assertEquals(
				"2019 haben 77,78 Prozent aller Kinder das gleiche oder ein schlechteres Ergebnis erzielt.",
				result.getText());
			assertEquals(9, result.getAnzahlLoesungszettel());
			assertEquals("Klasse 2", result.getKlassenstufe());
			assertEquals(wettbewerbID.toString(), result.getWettbewerbsjahr());
			assertEquals("77,78", result.getProzentrang());
		}

		@Test
		void should_berechneProzentrangReturnExakt_when_exakterTrefferMaximum() throws Exception {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2019);
			Klassenstufe klassenstufe = Klassenstufe.ZWEI;
			int punkte = 6375;
			List<Loesungszettel> alleLoesungszettel = getLoesungszettelKlasse2();

			Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, Klassenstufe.ZWEI))
				.thenReturn(alleLoesungszettel);

			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID).withStatus(WettbewerbStatus.BEENDET);

			Mockito.when(wettbewerbRepository.wettbewerbMitID(wettbewerbID))
				.thenReturn(Optional.of(wettbewerb));

			// Act
			ProzentrangAPIModel result = service.berechneProzentrang(wettbewerbID.jahr(), klassenstufe, punkte);

			// Assert
			assertEquals(
				"Das ist die höchste erreichbare Punktzahl. 2019 haben 2 Kinder das gleiche Ergebnis erzielt.",
				result.getText());
			assertEquals(9, result.getAnzahlLoesungszettel());
			assertEquals("Klasse 2", result.getKlassenstufe());
			assertEquals(wettbewerbID.toString(), result.getWettbewerbsjahr());
			assertEquals("100", result.getProzentrang());
		}

		@Test
		void should_berechneProzentrangReturnExakt_when_exakterTrefferMinimum() throws Exception {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2019);
			Klassenstufe klassenstufe = Klassenstufe.ZWEI;
			int punkte = 1250;
			List<Loesungszettel> alleLoesungszettel = getLoesungszettelKlasse2();

			Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, Klassenstufe.ZWEI))
				.thenReturn(alleLoesungszettel);

			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID).withStatus(WettbewerbStatus.BEENDET);

			Mockito.when(wettbewerbRepository.wettbewerbMitID(wettbewerbID))
				.thenReturn(Optional.of(wettbewerb));

			// Act
			ProzentrangAPIModel result = service.berechneProzentrang(wettbewerbID.jahr(), klassenstufe, punkte);

			// Assert
			assertEquals(
				"2019 haben 22,22 Prozent aller Kinder das gleiche oder ein schlechteres Ergebnis erzielt.",
				result.getText());
			assertEquals(9, result.getAnzahlLoesungszettel());
			assertEquals("Klasse 2", result.getKlassenstufe());
			assertEquals(wettbewerbID.toString(), result.getWettbewerbsjahr());
			assertEquals("22,22", result.getProzentrang());
		}

		@Test
		void should_berechneProzentrangReturnExakt_when_zwischenZweiItems() throws Exception {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2019);
			Klassenstufe klassenstufe = Klassenstufe.ZWEI;
			int punkte = 2000;
			List<Loesungszettel> alleLoesungszettel = getLoesungszettelKlasse2();

			Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, Klassenstufe.ZWEI))
				.thenReturn(alleLoesungszettel);

			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID).withStatus(WettbewerbStatus.BEENDET);

			Mockito.when(wettbewerbRepository.wettbewerbMitID(wettbewerbID))
				.thenReturn(Optional.of(wettbewerb));

			// Act
			ProzentrangAPIModel result = service.berechneProzentrang(wettbewerbID.jahr(), klassenstufe, punkte);

			// Assert
			assertEquals(
				"2019 haben 55,56 Prozent aller Kinder ein schlechteres Ergebnis erzielt.",
				result.getText());
			assertEquals(9, result.getAnzahlLoesungszettel());
			assertEquals("Klasse 2", result.getKlassenstufe());
			assertEquals(wettbewerbID.toString(), result.getWettbewerbsjahr());
			assertEquals("55,56", result.getProzentrang());
		}

		@Test
		void should_berechneProzentrangReturnExakt_when_besserAlsAlle() throws Exception {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2019);
			Klassenstufe klassenstufe = Klassenstufe.ZWEI;
			int punkte = 6875;
			List<Loesungszettel> alleLoesungszettel = getLoesungszettelKlasse2();

			Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, Klassenstufe.ZWEI))
				.thenReturn(alleLoesungszettel);

			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID).withStatus(WettbewerbStatus.BEENDET);

			Mockito.when(wettbewerbRepository.wettbewerbMitID(wettbewerbID))
				.thenReturn(Optional.of(wettbewerb));

			// Act
			ProzentrangAPIModel result = service.berechneProzentrang(wettbewerbID.jahr(), klassenstufe, punkte);

			// Assert
			assertEquals(
				"Das ist die höchste erreichbare Punktzahl. 2019 hat kein Kind eine so hohe Punktzahl erzielt.",
				result.getText());
			assertEquals(9, result.getAnzahlLoesungszettel());
			assertEquals("Klasse 2", result.getKlassenstufe());
			assertEquals(wettbewerbID.toString(), result.getWettbewerbsjahr());
			assertEquals("100", result.getProzentrang());
		}

		@Test
		void should_berechneProzentrangReturnExakt_when_schelchterAlsAlle() throws Exception {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2019);
			Klassenstufe klassenstufe = Klassenstufe.ZWEI;
			int punkte = 875;
			List<Loesungszettel> alleLoesungszettel = getLoesungszettelKlasse2();

			Mockito.when(loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID, Klassenstufe.ZWEI))
				.thenReturn(alleLoesungszettel);

			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID).withStatus(WettbewerbStatus.BEENDET);

			Mockito.when(wettbewerbRepository.wettbewerbMitID(wettbewerbID))
				.thenReturn(Optional.of(wettbewerb));

			// Act
			ProzentrangAPIModel result = service.berechneProzentrang(wettbewerbID.jahr(), klassenstufe, punkte);

			// Assert
			assertEquals(
				"2019 haben alle Kinder ein besseres Ergebnis erzielt.",
				result.getText());
			assertEquals(9, result.getAnzahlLoesungszettel());
			assertEquals("Klasse 2", result.getKlassenstufe());
			assertEquals(wettbewerbID.toString(), result.getWettbewerbsjahr());
			assertEquals("0", result.getProzentrang());
		}

	}

	List<Loesungszettel> getLoesungszettelKlasse2() throws Exception {

		List<Loesungszettel> alleLoesungszettel = StatistikTestUtils.loadTheLoesungszettel();

		List<Loesungszettel> result = alleLoesungszettel.stream().filter(lz -> Klassenstufe.ZWEI == lz.klassenstufe())
			.collect(Collectors.toList());
		// Loesungszettel besterZettel = new Loesungszettel().withIdentifier(new Identifier("gusadgqgo"))
		// .withKlassenstufe(Klassenstufe.ZWEI).withPunkte(6800);
		// result.add(besterZettel);

		return result;
	}

}
