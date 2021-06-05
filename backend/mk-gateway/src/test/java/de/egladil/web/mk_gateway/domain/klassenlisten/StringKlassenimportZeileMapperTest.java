// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.error.UploadFormatException;

/**
 * StringKlassenimportZeileMapperTest
 */
public class StringKlassenimportZeileMapperTest {

	private KlassenlisteUeberschrift klassenlisteUeberschrift;

	@BeforeEach
	void setUp() {

		klassenlisteUeberschrift = new KlassenlisteUeberschrift("Klasse,Nachname,Vorname,Klassenstufe");
	}

	@Test
	void should_applyReturnExcepctedKlassenlisteZeile_when_allesOk() {

		// Arrange
		String kommaseparierteZeile = "2a , Grüter ,Johanna , 2";
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);

		// Act
		KlassenimportZeile result = mapper.apply(kommaseparierteZeile);

		// Assert
		assertEquals("Johanna", result.getVorname());
		assertEquals("Grüter", result.getNachname());
		assertEquals("2a", result.getKlasse());
		assertEquals("2", result.getKlassenstufe());

	}

	@Test
	void should_applyThrowUploadFormatException_when_zeileZuKurz() {

		// Arrange
		String kommaseparierteZeile = "2a , Grüter ,2";
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);

		// Act + Assert
		try {

			mapper.apply(kommaseparierteZeile);
			fail("keine UploadFormatException");

		} catch (UploadFormatException e) {

			assertEquals("Die Klassenliste kann nicht importiert werden: erwarte genau 4 Einträge in jeder Zeile.", e.getMessage());
		}
	}

	@Test
	void should_applyThrowUploadFormatException_when_zeileZuLang() {

		// Arrange
		String kommaseparierteZeile = "2a , Grüter , Marie, Luise ,2";
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);

		// Act + Assert
		try {

			mapper.apply(kommaseparierteZeile);
			fail("keine UploadFormatException");

		} catch (UploadFormatException e) {

			assertEquals("Die Klassenliste kann nicht importiert werden: erwarte genau 4 Einträge in jeder Zeile.", e.getMessage());
		}
	}

	@Test
	void should_applyThrowUploadFormatException_when_zeileBlank() {

		// Arrange
		String kommaseparierteZeile = " ";
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);

		// Act + Assert
		try {

			mapper.apply(kommaseparierteZeile);
			fail("keine UploadFormatException");

		} catch (UploadFormatException e) {

			assertEquals("Die Klassenliste kann nicht importiert werden: erwarte genau 4 Einträge in jeder Zeile.", e.getMessage());
		}
	}

	@Test
	void should_applyThrowNPE_when_zeileBlank() {

		// Arrange
		String kommaseparierteZeile = null;
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);

		// Act + Assert
		try {

			mapper.apply(kommaseparierteZeile);
			fail("keine NullPointerException");

		} catch (NullPointerException e) {

			assertEquals("kommaseparierteZeile", e.getMessage());
		}
	}
}
