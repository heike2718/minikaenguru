// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;

/**
 * AuswertungCSVToAuswertungimportZeilenMapperTest
 */
public class AuswertungCSVToAuswertungimportZeilenMapperTest {

	private final AuswertungimportZeileSensor sensor = new AuswertungimportZeileSensor();

	private final AuswertungCSVToAuswertungimportZeilenMapper mapper = new AuswertungCSVToAuswertungimportZeilenMapper();

	@Nested
	class MappingTests {

		@Test
		void should_mapperIgnoreLinesUpToUeberschrift_when_csvFromExcel() {

			// Arrange
			List<String> lines = new ArrayList<>();
			lines.add("");
			lines.add(
				"Unnamed: 0;Unnamed: 1;\"in die farbigen Zellen eingeben:r für richtig; f für falsch; n für nicht bearbeitet (bitte das n nicht weglassen)\";Unnamed: 3;Unnamed: 4;Unnamed: 5;Unnamed: 6;Unnamed: 7;Unnamed: 8;Unnamed: 9;Unnamed: 10;Unnamed: 11;Unnamed: 12;Unnamed: 13;Unnamed: 14;Unnamed: 15;Unnamed: 16;Unnamed: 17;Unnamed: 18;Unnamed: 19;Unnamed: 20;Unnamed: 21;Unnamed: 22;Unnamed: 23;Unnamed: 24;Unnamed: 25;Unnamed: 26;Unnamed: 27;Unnamed: 28;Unnamed: 29;Unnamed: 30;Unnamed: 31");
			lines.add(";;;;;;;;;;;;;;;;");
			lines.add(";In der letzten Spalte stehen anschließend die Punkte für das jeweilige Kind;;;;;;;;;;;;;;;");
			lines.add(
				";Gleich nach dem Ausfüllen und Speichern über die Minikänguru-Anwendung hochladen: https://mathe-jung-alt.de/mkv;;;;;;;;;;;;;;;");
			lines.add("Name;A-1;A-2;A-3;A-4;A-5;B-1;B-2;B-3;B-4;B-5;C-1;C-2;C-3;C-5;C-5;Punkte");
			lines.add("K;r;3;r;3.0;r;3.0;r;3.0;r;3.0;r;4.0;r;4.0;f;-1.0;r;4.0;r;4.0;r;5.0;r;5.0;r;5.0;r;5.0;f;-1.25;63.75");
			lines.add(";0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;15");

			// Act
			List<AuswertungimportZeile> zeilen = mapper.apply(lines);

			// Assert
			assertEquals(2, zeilen.size());

			{

				AuswertungimportZeile auswertungimportZeile = zeilen.get(0);
				assertTrue(sensor.isUeberschrift(auswertungimportZeile.getRohdaten()));
				assertEquals(0, auswertungimportZeile.getIndex());
			}

			{

				AuswertungimportZeile auswertungimportZeile = zeilen.get(1);
				assertFalse(sensor.isLeereZeile(auswertungimportZeile.getRohdaten()));
				assertEquals(1, auswertungimportZeile.getIndex());

			}

		}

		@Test
		void should_mapperwork_when_csvFromOds() {

			// Arrange
			List<String> lines = new ArrayList<>();
			lines.add("Name;A-1;A-2;A-3;A-4;B-1;B-2;B-3;B-4;C-1;C-2;C-3;C-4;Punkte");
			lines.add("Tarja;f;f;r;r;f;r;r;r;r;r;r;r;");

			// Act
			List<AuswertungimportZeile> zeilen = new AuswertungCSVToAuswertungimportZeilenMapper().apply(lines);

			// Assert
			assertEquals(2, zeilen.size());

			{

				AuswertungimportZeile auswertungimportZeile = zeilen.get(0);
				assertTrue(sensor.isUeberschrift(auswertungimportZeile.getRohdaten()));
				assertEquals(0, auswertungimportZeile.getIndex());
			}

			{

				AuswertungimportZeile auswertungimportZeile = zeilen.get(1);
				assertFalse(sensor.isLeereZeile(auswertungimportZeile.getRohdaten()));
				assertEquals(1, auswertungimportZeile.getIndex());

			}
		}

		@Test
		void should_mapperWork_when_ListEmpty() {

			// Act
			List<AuswertungimportZeile> zeilen = mapper.apply(new ArrayList<>());

			// Assert
			assertTrue(zeilen.isEmpty());

		}

		@Test
		void should_mapperWork_when_ArbitraryList() {

			List<String> lines = new ArrayList<>();
			lines.add("Tarja,f;f;r;r;f;r;r;r;r;r;r;r;");

			// Act
			List<AuswertungimportZeile> zeilen = mapper.apply(new ArrayList<>());

			// Assert
			assertTrue(zeilen.isEmpty());

		}

		@Test
		void should_mappingWork_when_convertedFile() {

			// Arrange
			String path = "/home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload/auswertung.csv";

			List<String> zeilen = MkGatewayFileUtils.readLines(path);

			// Act
			List<AuswertungimportZeile> result = mapper.apply(zeilen);

			// Assert
			assertEquals(25, result.size());

		}
	}

	@Nested
	class PositionTests {

		@Test
		void should_findIndexUeberschriftReturnMinus1_when_emptyList() {

			// Act
			int result = mapper.findIndexUeberschrift(new ArrayList<>());

			// Assert
			assertEquals(-1, result);
		}

		@Test
		void should_findIndexUeberschriftReturnMinus1_when_keineUeberschrift() {

			// Arrange
			String line = "adhuwhehqhpwdhpqi";

			// Act
			int result = mapper.findIndexUeberschrift(Collections.singletonList(line));

			// Assert
			assertEquals(-1, result);
		}
	}
}
