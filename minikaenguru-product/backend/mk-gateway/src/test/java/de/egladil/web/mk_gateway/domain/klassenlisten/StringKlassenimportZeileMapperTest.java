// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StringKlassenimportZeileMapperTest
 */
public class StringKlassenimportZeileMapperTest {

	private KlassenlisteUeberschrift klassenlisteUeberschrift;

	@BeforeEach
	void setUp() {

		klassenlisteUeberschrift = new KlassenlisteUeberschrift("Klasse;Nachname;Vorname;Klassenstufe");
	}

	@Test
	void should_applyReturnExcepctedKlassenlisteZeile_when_allesOk() {

		// Arrange
		String kommaseparierteZeile = "2a ; Grüter ;Johanna ; 2";

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
		String kommaseparierteZeile = "2a ; Grüter ;2";
		Pair<Integer, String> zeileMitIndex = Pair.of(Integer.valueOf(42), kommaseparierteZeile);

		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);

		// Act
		Optional<KlassenimportZeile> optResult = mapper.apply(zeileMitIndex);

		// Assert
		assertTrue(optResult.isPresent());

		KlassenimportZeile result = optResult.get();
		assertEquals(42, result.getIndex());
		assertEquals(
			"Zeile 42: Fehler! \"2a ; Grüter ;2\" wird nicht importiert: Vorname, Nachname, Klasse und Klassenstufe lassen sich nicht zuordnen. Es sind weniger als 4 Angaben.",
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
		String kommaseparierteZeile = "2a ; Grüter ; Marie; Luise ;2.0";
		Pair<Integer, String> zeileMitIndex = Pair.of(Integer.valueOf(42), kommaseparierteZeile);
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);

		// Act
		Optional<KlassenimportZeile> optResult = mapper.apply(zeileMitIndex);

		// Assert
		assertTrue(optResult.isPresent());

		KlassenimportZeile result = optResult.get();
		assertEquals(42, result.getIndex());
		assertEquals(
			"Zeile 42: Fehler! \"2a ; Grüter ; Marie; Luise ;2.0\" wird nicht importiert: Vorname, Nachname, Klasse und Klassenstufe lassen sich nicht zuordnen. Es sind mehr als 4 Angaben.",
			result.getFehlermeldung());
		assertFalse(result.ok());
		assertNull(result.getKlasse());
		assertNull(result.getKlassenstufe());
		assertNull(result.getNachname());
		assertNull(result.getVorname());
	}

	@Test
	void should_applyReturnOptionalWithFehlermeldung_when_zeileZuKurz() {

		// Arrange
		String kommaseparierteZeile = "Grüter ; 2a";
		Pair<Integer, String> zeileMitIndex = Pair.of(Integer.valueOf(42), kommaseparierteZeile);
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);

		// Act
		Optional<KlassenimportZeile> optResult = mapper.apply(zeileMitIndex);

		// Assert
		assertTrue(optResult.isPresent());

		KlassenimportZeile result = optResult.get();
		assertEquals(42, result.getIndex());
		assertEquals(
			"Zeile 42: Fehler! \"Grüter ; 2a\" wird nicht importiert: Vorname, Nachname, Klasse und Klassenstufe lassen sich nicht zuordnen. Es sind weniger als 4 Angaben.",
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

			assertEquals("semikolonseparierteZeileMitIndex", e.getMessage());
		}
	}

	@Test
	void should_applyConvertDoubleString() {

		// Arrange
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);
		String zeile = "Szymon;Wanowski;1b;1.0";
		Pair<Integer, String> pair = Pair.of(Integer.valueOf(42), zeile);

		// Act
		Optional<KlassenimportZeile> result = mapper.apply(pair);

		// Assert
		assertTrue(result.isPresent());
		KlassenimportZeile importierteZeile = result.get();
		assertNull(importierteZeile.getFehlermeldung());
		assertEquals("1", importierteZeile.getKlassenstufe());
	}

	@Test
	void should_applyWork() {

		// Arrange
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);
		String zeile = "Szymon;Wanowski;1b;1";
		Pair<Integer, String> pair = Pair.of(Integer.valueOf(42), zeile);

		// Act
		Optional<KlassenimportZeile> result = mapper.apply(pair);

		// Assert
		assertTrue(result.isPresent());
		KlassenimportZeile importierteZeile = result.get();
		assertNull(importierteZeile.getFehlermeldung());
		assertEquals("1", importierteZeile.getKlassenstufe());
	}

	@Test
	void should_applyIgnore_when_zeile3SemikolonsUndSonstNix() {

		// Arrange
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);
		String zeile = ";;;";
		Pair<Integer, String> pair = Pair.of(Integer.valueOf(42), zeile);

		// Act
		Optional<KlassenimportZeile> result = mapper.apply(pair);

		// Assert
		assertTrue(result.isEmpty());
	}

	@Test
	void should_applyIgnore_when_zeileBeliebigeAnzahlSemikolonsUndSonstNix() {

		// Arrange
		StringKlassenimportZeileMapper mapper = new StringKlassenimportZeileMapper(klassenlisteUeberschrift);
		int anzahlSemikolons = new Random().nextInt(10) + 3;

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < anzahlSemikolons; i++) {

			sb.append(";");
		}

		String zeile = sb.toString();
		Pair<Integer, String> pair = Pair.of(Integer.valueOf(42), zeile);

		// Act
		Optional<KlassenimportZeile> result = mapper.apply(pair);

		// Assert
		assertTrue(result.isEmpty());
	}
}
