// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * KlasseDuplettenprueferTest
 */
public class KlasseDuplettenprueferTest {

	@Test
	void should_klasseNotBeDoubleOfItself() {

		// Arrange
		Klasse klasse1 = new Klasse(new Identifier("eins")).withName("Fuchsklasse").withSchuleID(new Identifier("AAAAAAAA"));
		Klasse klasse2 = new Klasse(new Identifier("eins")).withName("Fuchsklasse").withSchuleID(new Identifier("AAAAAAAA"));

		// Act
		Boolean result = new KlasseDuplettenpruefer().apply(klasse1, klasse2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	@Test
	void should_klassenInDifferentSchools_NotBeDoubles_when_NamesEqual() {

		// Arrange
		Klasse klasse1 = new Klasse(new Identifier("eins")).withName("Fuchsklasse").withSchuleID(new Identifier("AAAAAAAA"));
		Klasse klasse2 = new Klasse(new Identifier("zwei")).withName("Fuchsklasse").withSchuleID(new Identifier("BBBBBBBB"));

		// Act
		Boolean result = new KlasseDuplettenpruefer().apply(klasse1, klasse2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	@Test
	void should_klassenInSameSchool_BeDoubles_when_NamesEqual() {

		// Arrange
		Klasse klasse1 = new Klasse(new Identifier("neu")).withName("FuchsKlasse").withSchuleID(new Identifier("AAAAAAAA"));
		Klasse klasse2 = new Klasse(new Identifier("zwei")).withName("Fuchsklasse").withSchuleID(new Identifier("AAAAAAAA"));

		// Act
		Boolean result = new KlasseDuplettenpruefer().apply(klasse1, klasse2);

		// Assert
		assertEquals(Boolean.TRUE, result);
	}
}
