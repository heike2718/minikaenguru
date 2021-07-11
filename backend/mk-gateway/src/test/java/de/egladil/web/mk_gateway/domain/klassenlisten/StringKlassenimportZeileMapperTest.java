// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
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
	void should_constructorThrowUploadFormatException_when_ueberschriftNichtWieGefordert() {

		KlassenlisteUeberschrift ueberschrift = new KlassenlisteUeberschrift("blau,grün,Vorname,Klassenstufe");

		try {

			new StringKlassenimportZeileMapper(ueberschrift);
			fail("keine UploadFormatException");

		} catch (UploadFormatException e) {

			assertEquals(
				"Die hochgeladene Datei kann nicht verarbeitet werden. Die erste Zeile enthält nicht die Felder \"Nachname\", \"Vorname\", \"Klasse\", \"Klassenstufe\".",
				e.getMessage());
		}

	}

	@Test
	void should_applyReturnExcepctedKlassenlisteZeile_when_allesOk() {

		// Arrange
		String kommaseparierteZeile = "2a , Grüter ,Johanna , 2";

		Pair<Integer, String> zeileMitIndex = Pair.of(Integer.valueOf(42), kommaseparierteZeile);

		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);

		// Act
		Optional<KlassenimportZeile> optResult = mapper.apply(zeileMitIndex);

		// Assert
		KlassenimportZeile result = optResult.get();
		assertEquals("Johanna", result.getVorname());
		assertEquals("Grüter", result.getNachname());
		assertEquals("2a", result.getKlasse());
		assertEquals("2", result.getKlassenstufe());
		assertEquals(42, result.getIndex());
		assertNull(result.getFehlermeldung());
		assertTrue(result.ok());

	}

	@Test
	void should_applyReturnZeileMitFehlermeldung_when_zeileZuKurz() {

		// Arrange
		String kommaseparierteZeile = "2a , Grüter ,2";
		Pair<Integer, String> zeileMitIndex = Pair.of(Integer.valueOf(42), kommaseparierteZeile);

		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);

		// Act
		Optional<KlassenimportZeile> optResult = mapper.apply(zeileMitIndex);

		// Assert
		assertTrue(optResult.isPresent());

		KlassenimportZeile result = optResult.get();
		assertEquals(42, result.getIndex());
		assertEquals(
			"Fehler! Zeile \"2a , Grüter ,2\" wird nicht importiert: Vorname, Nachname, Klasse und Klassenstufe lassen sich nicht zuordnen.",
			result.getFehlermeldung());
		assertFalse(result.ok());
		assertNull(result.getKlasse());
		assertNull(result.getKlassenstufe());
		assertNull(result.getNachname());
		assertNull(result.getVorname());

	}

	@Test
	void should_applyReturnOptionalWithFehlermeldung_when_zeileZuLang() {

		// Arrange
		String kommaseparierteZeile = "2a , Grüter , Marie, Luise ,2";
		Pair<Integer, String> zeileMitIndex = Pair.of(Integer.valueOf(42), kommaseparierteZeile);
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);

		// Act
		Optional<KlassenimportZeile> optResult = mapper.apply(zeileMitIndex);

		// Assert
		assertTrue(optResult.isPresent());

		KlassenimportZeile result = optResult.get();
		assertEquals(42, result.getIndex());
		assertEquals(
			"Fehler! Zeile \"2a , Grüter , Marie, Luise ,2\" wird nicht importiert: Vorname, Nachname, Klasse und Klassenstufe lassen sich nicht zuordnen.",
			result.getFehlermeldung());
		assertFalse(result.ok());
		assertNull(result.getKlasse());
		assertNull(result.getKlassenstufe());
		assertNull(result.getNachname());
		assertNull(result.getVorname());
	}

	@Test
	void should_applyReturnEmptyOptional_when_zeileBlank() {

		// Arrange
		String kommaseparierteZeile = " ";
		Pair<Integer, String> zeileMitIndex = Pair.of(Integer.valueOf(42), kommaseparierteZeile);
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);

		// Act
		Optional<KlassenimportZeile> optResult = mapper.apply(zeileMitIndex);
		assertTrue(optResult.isEmpty());
	}

	@Test
	void should_applyThrowNPE_when_zeileMitIndexNull() {

		// Arrange
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);

		// Act + Assert
		try {

			mapper.apply(null);
			fail("keine NullPointerException");

		} catch (NullPointerException e) {

			assertEquals("kommaseparierteZeile", e.getMessage());
		}
	}
}
