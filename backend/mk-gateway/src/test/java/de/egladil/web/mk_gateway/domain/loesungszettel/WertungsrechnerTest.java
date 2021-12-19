// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.statistik.Aufgabenkategorie;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * WertungsrechnerTest
 */
public class WertungsrechnerTest {

	@Nested
	class InklusionskidsTests {

		private final Klassenstufe klassenstufe = Klassenstufe.IKID;

		@Test
		void should_wertungscodeForKategorien_returnTheCorrectGroups() {

			// Arrange
			String wertungscode = "abcdef";

			// Act
			Map<Aufgabenkategorie, String> gruppen = new Wertungsrechner().wertungscodeForKategorien(wertungscode, klassenstufe);

			// Assert
			assertEquals("ab", gruppen.get(Aufgabenkategorie.LEICHT));
			assertEquals("cd", gruppen.get(Aufgabenkategorie.MITTEL));
			assertEquals("ef", gruppen.get(Aufgabenkategorie.SCHWER));
		}

		@Test
		void should_getWertungscodeThrowIllegalArgumentException_when_laengeWertungscodeFalsch() {

			// Arrange
			String wertungscode = "rrffn";

			try {

				new Wertungsrechner().getWertung(wertungscode, klassenstufe);
				fail("keine IllegalArgumentException");
			} catch (IllegalArgumentException e) {

				assertEquals("laenge wertungscode rrffn (5) und klassenstufe IKID sind inkompatibel", e.getMessage());
			}

		}

		@Test
		void should_getWertungReturn0_when_allesFalsch() {

			// Arrange
			String wertungscode = "ffffff";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(0, 0);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn3600_when_allesRichtig() {

			// Arrange
			String wertungscode = "rrrrrr";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(3600, 6);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn600_when_keineAufgabeGeloest() {

			// Arrange
			String wertungscode = "nnnnnn";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(1200, 0);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn1800_when_leichteRichtigGeloest() {

			// Arrange
			String wertungscode = "rrnnnn";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(1800, 2);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn1600_when_ersteDreiRichtigGeloest() {

			// Arrange
			String wertungscode = "rrrnnn";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(2200, 3);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn1475_when_irgendwieGeloest() {

			// Arrange
			String wertungscode = "rrfnrf";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(1850, 2);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		void getTheCorrectPunkte() {

			List<String> wertungscodes = Arrays.asList(new String[] { "rrrrrr", "rnnrnn", "rrrrrn", "rrrffr", "rnrrrn", "rrnnrr",
				"rrrrrf", "rrrfrr", "rrrrnn", "rrrfrf", "fffrnn", "ffrrrf", "rrfffr", "rrrrff", "rnnrnf", "rrrrnr", "rrrrfr",
				"rnnnnn", "rfrfnn", "rrrrfn", "rrfrrr", "rrfrrf", "rfrrrr" });

			assertEquals(23, wertungscodes.size());

			Wertungsrechner wertungsrechner = new Wertungsrechner();

			for (String code : wertungscodes) {

				Wettbewerbswertung wertung = wertungsrechner.getWertung(code, klassenstufe);

				StringBuilder sb = new StringBuilder();
				sb.append("update LOESUNGSZETTEL set PUNKTE=");
				sb.append(wertung.punkte());
				sb.append(", KAENGURUSPRUNG=");
				sb.append(wertung.kaengurusprung());
				sb.append(" WHERE WERTUNGSCODE='");
				sb.append(code);
				sb.append("';");

				System.out.println(sb);
			}

		}

	}

	@Nested
	class Klasse1Tests {

		private final Klassenstufe klassenstufe = Klassenstufe.EINS;

		@Test
		void should_wertungscodeForKategorien_returnTheCorrectGroups() {

			// Arrange
			String wertungscode = "abcdefghijkl";

			// Act
			Map<Aufgabenkategorie, String> gruppen = new Wertungsrechner().wertungscodeForKategorien(wertungscode, klassenstufe);

			// Assert
			assertEquals("abcd", gruppen.get(Aufgabenkategorie.LEICHT));
			assertEquals("efgh", gruppen.get(Aufgabenkategorie.MITTEL));
			assertEquals("ijkl", gruppen.get(Aufgabenkategorie.SCHWER));
		}

		@Test
		void should_getWertungReturn0_when_allesFalsch() {

			// Arrange
			String wertungscode = "ffffffffffff";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(0, 0);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn6000_when_allesRichtig() {

			// Arrange
			String wertungscode = "rrrrrrrrrrrr";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(6000, 12);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn1200_when_keineAufgabeGeloest() {

			// Arrange
			String wertungscode = "nnnnnnnnnnnn";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(1200, 0);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn2400_when_leichteRichtigGeloest() {

			// Arrange
			String wertungscode = "rrrrnnnnnnnn";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(2400, 4);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn2100_when_ersteDreiRichtigGeloest() {

			// Arrange
			String wertungscode = "rrrnnnnnnnnn";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(2100, 3);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn3125_when_irgendwieGeloest() {

			// Arrange
			String wertungscode = "frrffrrfnfrr";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(3125, 2);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

	}

	@Nested
	class Klasse2Tests {

		private final Klassenstufe klassenstufe = Klassenstufe.ZWEI;

		@Test
		void should_wertungscodeForKategorien_returnTheCorrectGroups() {

			// Arrange
			String wertungscode = "abcdefghijklmno";

			// Act
			Map<Aufgabenkategorie, String> gruppen = new Wertungsrechner().wertungscodeForKategorien(wertungscode, klassenstufe);

			// Assert
			assertEquals("abcde", gruppen.get(Aufgabenkategorie.LEICHT));
			assertEquals("fghij", gruppen.get(Aufgabenkategorie.MITTEL));
			assertEquals("klmno", gruppen.get(Aufgabenkategorie.SCHWER));
		}

		@Test
		void should_getWertungReturn0_when_allesFalsch() {

			// Arrange
			String wertungscode = "fffffffffffffff";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(0, 0);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn6000_when_allesRichtig() {

			// Arrange
			String wertungscode = "rrrrrrrrrrrrrrr";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(7500, 15);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn1500_when_keineAufgabeGeloest() {

			// Arrange
			String wertungscode = "nnnnnnnnnnnnnnn";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(1500, 0);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn3000_when_leichteRichtigGeloest() {

			// Arrange
			String wertungscode = "rrrrrnnnnnnnnnn";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(3000, 5);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn2400_when_ersteDreiRichtigGeloest() {

			// Arrange
			String wertungscode = "rrrnnnnnnnnnnnn";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(2400, 3);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

		@Test
		void should_getWertungReturn3625_when_irgendwieGeloest() {

			// Arrange
			String wertungscode = "frfrffffrrfrrrf";
			Wertungsrechner wertungsrechner = new Wertungsrechner();
			Wettbewerbswertung expectedWewrtung = new Wettbewerbswertung(3625, 3);

			// Act
			Wettbewerbswertung actual = wertungsrechner.getWertung(wertungscode, klassenstufe);

			// Assert
			assertEquals(expectedWewrtung, actual);

		}

	}
}
