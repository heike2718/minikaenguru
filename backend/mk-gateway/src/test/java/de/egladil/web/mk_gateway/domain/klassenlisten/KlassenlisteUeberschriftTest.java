// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					"Die erste Zeile muss folgenden Inhalt in beliebiger Reihenfolge haben: Klasse,Klassenstufe,Nachname,Vorname",
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
					"Die erste Zeile muss folgenden Inhalt in beliebiger Reihenfolge haben: Klasse,Klassenstufe,Nachname,Vorname",
					e.getMessage());
			}
		}

		@Test
		void should_throwException_when_lessThan4Fields() {

			int zahl = new Random().nextInt(4);
			List<String> strings = new ArrayList<>();

			for (int i = 0; i < zahl; i++) {

				strings.add("Test-" + i);
			}

			String zeileKommasepariert = StringUtils.join(strings, ",");

			try {

				new KlassenlisteUeberschrift(zeileKommasepariert);

				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					"Die erste Zeile muss folgenden Inhalt in beliebiger Reihenfolge haben: Klasse,Klassenstufe,Nachname,Vorname",
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

			String zeileKommasepariert = StringUtils.join(strings, ",");

			try {

				new KlassenlisteUeberschrift(zeileKommasepariert);

				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					"Die erste Zeile muss folgenden Inhalt in beliebiger Reihenfolge haben: Klasse,Klassenstufe,Nachname,Vorname",
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
		void should_getIndexReturnEmpty_when_feldartNotFound() {

			// Arrange
			String zeileKommasepariert = "klassenStufe;Horst;VorName;nachName";
			KlassenlisteUeberschrift ueberschrift = new KlassenlisteUeberschrift(zeileKommasepariert);

			// Act + Assert
			assertTrue(ueberschrift.getIndexFeldart(KlassenlisteFeldart.KLASSE).isEmpty());
			assertEquals(Integer.valueOf(0), ueberschrift.getIndexFeldart(KlassenlisteFeldart.KLASSENSTUFE).get());
			assertEquals(Integer.valueOf(2), ueberschrift.getIndexFeldart(KlassenlisteFeldart.VORNAME).get());
			assertEquals(Integer.valueOf(3), ueberschrift.getIndexFeldart(KlassenlisteFeldart.NACHNAME).get());

		}

		@Test
		void should_getIndexReturnEmpty_when_tokenBlank() {

			// Arrange
			String zeileKommasepariert = "klassenStufe;\" \";VorName;nachName";
			KlassenlisteUeberschrift ueberschrift = new KlassenlisteUeberschrift(zeileKommasepariert);

			// Act + Assert
			assertTrue(ueberschrift.getIndexFeldart(KlassenlisteFeldart.KLASSE).isEmpty());
			assertEquals(Integer.valueOf(0), ueberschrift.getIndexFeldart(KlassenlisteFeldart.KLASSENSTUFE).get());
			assertEquals(Integer.valueOf(2), ueberschrift.getIndexFeldart(KlassenlisteFeldart.VORNAME).get());
			assertEquals(Integer.valueOf(3), ueberschrift.getIndexFeldart(KlassenlisteFeldart.NACHNAME).get());
		}
	}
}
