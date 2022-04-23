// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;

/**
 * KlassenlisteUeberschriftTest
 */
public class KlassenlisteUeberschriftTest {

	@Nested
	class ConstructorTests {

		@Test
		void should_throwException_when_null() {

			String zeileKommasepariert = null;

			try {

				new KlassenlisteUeberschrift(zeileKommasepariert);

				fail("keine MkGatewayRuntimeException");
			} catch (MkGatewayRuntimeException e) {

				assertEquals(
					"zeileSemikolonsepariert null",
					e.getMessage());
			}

		}

		@Test
		void should_throwException_when_blank() {

			String zeileKommasepariert = "  ";

			try {

				new KlassenlisteUeberschrift(zeileKommasepariert);

				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					"Die Klassenliste kann nicht verarbeitet werden. Ihre Tabelle hat nicht die erwarteten Spalten. Es werden genau 4 Spalten mit den Überschriften Klasse,Klassenstufe,Nachname,Vorname in beliebiger Reihenfolge erwartet. Ihre Spaltenüberschriften sind leer.",
					e.getMessage());
			}
		}

		@Test
		void should_throwException_when_lessThan4Fields() {

			int zahl = new Random().nextInt(4);
			List<String> strings = new ArrayList<>();

			if (zahl == 0) {

				zahl++;
			}

			for (int i = 0; i < zahl; i++) {

				strings.add("Test-" + i);
			}

			String zeileKommasepariert = StringUtils.join(strings, ";");

			String expectedMessage = "Die Klassenliste kann nicht verarbeitet werden. Ihre Tabelle hat nicht die erwarteten Spalten. Es werden genau 4 Spalten mit den Überschriften Klasse,Klassenstufe,Nachname,Vorname in beliebiger Reihenfolge erwartet. Gefunden wurden "
				+ zahl + " Spaltenüberschrift(en): "
				+ zeileKommasepariert + ".";

			try {

				new KlassenlisteUeberschrift(zeileKommasepariert);

				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					expectedMessage,
					e.getMessage());
			}
		}

		@Test
		void should_throwException_when_moreThan4Fields() {

			int zahl = new Random().nextInt(10) + 5;

			List<String> strings = new ArrayList<>();

			for (int i = 0; i < zahl; i++) {

				strings.add("Test-" + i);
			}

			String zeileKommasepariert = StringUtils.join(strings, ";");

			String expectedMessage = "Die Klassenliste kann nicht verarbeitet werden. Ihre Tabelle hat nicht die erwarteten Spalten. Es werden genau 4 Spalten mit den Überschriften Klasse,Klassenstufe,Nachname,Vorname in beliebiger Reihenfolge erwartet. Gefunden wurden "
				+ zahl + " Spaltenüberschrift(en): "
				+ zeileKommasepariert + ".";

			try {

				new KlassenlisteUeberschrift(zeileKommasepariert);

				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					expectedMessage,
					e.getMessage());
			}
		}

		@Test
		void should_throwException_when_firstTokenBlank() {

			int zahl = new Random().nextInt(10) + 5;

			List<String> strings = new ArrayList<>();

			for (int i = 0; i < zahl; i++) {

				strings.add("Test-" + i);
			}

			String zeileKommasepariert = ";Vorname;Nachname;Klasse;Klassenstufe";

			try {

				new KlassenlisteUeberschrift(zeileKommasepariert);

				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					"Die Klassenliste kann nicht verarbeitet werden. Ihre Tabelle hat nicht die erwarteten Spalten. Es werden genau 4 Spalten mit den Überschriften Klasse,Klassenstufe,Nachname,Vorname in beliebiger Reihenfolge erwartet. Gefunden wurden 5 Spaltenüberschrift(en): ;Vorname;Nachname;Klasse;Klassenstufe.",
					e.getMessage());
			}
		}

		@Test
		void should_throwException_when_NachnameFehlt() {

			int zahl = new Random().nextInt(10) + 5;

			List<String> strings = new ArrayList<>();

			for (int i = 0; i < zahl; i++) {

				strings.add("Test-" + i);
			}

			String zeileKommasepariert = "Vorname;Walter;Klasse;Klassenstufe";

			try {

				new KlassenlisteUeberschrift(zeileKommasepariert);

				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					"Die Klassenliste kann nicht verarbeitet werden. Es werden genau 4 Spalten mit den Überschriften Klasse,Klassenstufe,Nachname,Vorname in beliebiger Reihenfolge erwartet. Gefunden wurden die Spaltenüberschriften Vorname;Walter;Klasse;Klassenstufe.",
					e.getMessage());
			}
		}

		@Test
		void should_throwException_when_UnerwarteteUeberschrift() {

			int zahl = new Random().nextInt(10) + 5;

			List<String> strings = new ArrayList<>();

			for (int i = 0; i < zahl; i++) {

				strings.add("Test-" + i);
			}

			String zeileKommasepariert = "Vorname;Nachname;Walter;Klassenstufe";

			try {

				new KlassenlisteUeberschrift(zeileKommasepariert);

				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					"Die Klassenliste kann nicht verarbeitet werden. Es werden genau 4 Spalten mit den Überschriften Klasse,Klassenstufe,Nachname,Vorname in beliebiger Reihenfolge erwartet. Gefunden wurden die Spaltenüberschriften Vorname;Nachname;Walter;Klassenstufe.",
					e.getMessage());
			}
		}

	}

	@Nested
	class IndexFeldartTests {

		@Test
		void should_getIndexReturnExpectedValues_when_inputValid() {

			// Arrange
			String zeileKommasepariert = "klassenStufe;Klasse;VorName;nachName";
			KlassenlisteUeberschrift ueberschrift = new KlassenlisteUeberschrift(zeileKommasepariert);

			// Act + Assert
			assertEquals(Integer.valueOf(0), ueberschrift.getIndexFeldart(KlassenlisteFeldart.KLASSENSTUFE).get());
			assertEquals(Integer.valueOf(1), ueberschrift.getIndexFeldart(KlassenlisteFeldart.KLASSE).get());
			assertEquals(Integer.valueOf(2), ueberschrift.getIndexFeldart(KlassenlisteFeldart.VORNAME).get());
			assertEquals(Integer.valueOf(3), ueberschrift.getIndexFeldart(KlassenlisteFeldart.NACHNAME).get());

		}

		@Test
		void should_getIndexReturnTheCorrectValues_when_andereZeichen() {

			// Arrange
			String zeileKommasepariert = "Vorname;�Nachname;�Klasse;�Klassenstufe";
			KlassenlisteUeberschrift ueberschrift = new KlassenlisteUeberschrift(zeileKommasepariert);

			// Act + Assert
			assertEquals(Integer.valueOf(2), ueberschrift.getIndexFeldart(KlassenlisteFeldart.KLASSE).get());
			assertEquals(Integer.valueOf(3), ueberschrift.getIndexFeldart(KlassenlisteFeldart.KLASSENSTUFE).get());
			assertEquals(Integer.valueOf(0), ueberschrift.getIndexFeldart(KlassenlisteFeldart.VORNAME).get());
			assertEquals(Integer.valueOf(1), ueberschrift.getIndexFeldart(KlassenlisteFeldart.NACHNAME).get());
		}

		@Test
		void should_getIndexReturnExpectedValues_when_NameStattNachnamePos1() {

			// Arrange
			String zeileKommasepariert = "name;Klasse;VorName;klassenstufe";
			KlassenlisteUeberschrift ueberschrift = new KlassenlisteUeberschrift(zeileKommasepariert);

			// Act + Assert
			assertEquals(Integer.valueOf(0), ueberschrift.getIndexFeldart(KlassenlisteFeldart.NACHNAME).get());
			assertEquals(Integer.valueOf(1), ueberschrift.getIndexFeldart(KlassenlisteFeldart.KLASSE).get());
			assertEquals(Integer.valueOf(2), ueberschrift.getIndexFeldart(KlassenlisteFeldart.VORNAME).get());
			assertEquals(Integer.valueOf(3), ueberschrift.getIndexFeldart(KlassenlisteFeldart.KLASSENSTUFE).get());

		}
	}

	@Nested
	class DetectIndexNachnameTests {

		@Test
		void should_detectIndexNachnameReturn0_whenPosition0() {

			String[] eingaben = new String[] { " NaMe ", "  Vorname", "Klasse ", "Klassenstufe" };

			// Act + Assert
			assertEquals(0, new KlassenlisteUeberschrift().detectIndexNachname(eingaben));

		}

		@Test
		void should_detectIndexNachnameReturn1_whenPosition1() {

			String[] eingaben = new String[] { "  Vorname", " NaMe ", "Klasse ", "Klassenstufe" };

			// Act + Assert
			assertEquals(1, new KlassenlisteUeberschrift().detectIndexNachname(eingaben));

		}

		@Test
		void should_detectIndexNachnameReturn2_whenPosition2() {

			String[] eingaben = new String[] { "  Vorname", "Klasse ", " NaMe ", "Klassenstufe" };

			// Act + Assert
			assertEquals(2, new KlassenlisteUeberschrift().detectIndexNachname(eingaben));

		}

		@Test
		void should_detectIndexNachnameReturn3_whenPosition3() {

			String[] eingaben = new String[] { "  Vorname", "Klasse ", "Klassenstufe", " NaMe " };

			// Act + Assert
			assertEquals(3, new KlassenlisteUeberschrift().detectIndexNachname(eingaben));

		}

		@Test
		void should_detectIndexNachnameReturnNegative_whenNotPresent() {

			String[] eingaben = new String[] { "  Vorname", "Klasse ", "Klassenstufe", " Heinz " };

			// Act + Assert
			assertEquals(-1, new KlassenlisteUeberschrift().detectIndexNachname(eingaben));

		}

		@Test
		void should_detectIndexNachnameReturn0_whenNachnamePosition0() {

			String[] eingaben = new String[] { " NachName ", "  Vorname", "Klasse ", "Klassenstufe" };

			// Act + Assert
			assertEquals(0, new KlassenlisteUeberschrift().detectIndexNachname(eingaben));

		}

		@Test
		void should_detectIndexNachnameReturn1_whenNachnamePosition1() {

			String[] eingaben = new String[] { "  Vorname", " Nachname ", "Klasse ", "Klassenstufe" };

			// Act + Assert
			assertEquals(1, new KlassenlisteUeberschrift().detectIndexNachname(eingaben));

		}

		@Test
		void should_detectIndexNachnameReturn2_whenNachnamePosition2() {

			String[] eingaben = new String[] { "  Vorname", "Klasse ", " Nachname ", "Klassenstufe" };

			// Act + Assert
			assertEquals(2, new KlassenlisteUeberschrift().detectIndexNachname(eingaben));

		}

		@Test
		void should_detectIndexNachnameReturn3_whenNachnamePosition3() {

			String[] eingaben = new String[] { "  Vorname", "Klasse ", "Klassenstufe", " NaMe " };

			// Act + Assert
			assertEquals(3, new KlassenlisteUeberschrift().detectIndexNachname(eingaben));

		}

		@Test
		void should_detectIndexNachnameReturnNegative_whenOneItemNull() {

			String[] eingaben = new String[] { "  Vorname", "Klasse ", null, " NaMe " };

			// Act + Assert
			assertEquals(-1, new KlassenlisteUeberschrift().detectIndexNachname(eingaben));

		}
	}
}
