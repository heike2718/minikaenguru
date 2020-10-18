// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * KindDublettenprueferTest
 */
public class KindDublettenprueferTest {

	@Test
	void should_KindNotBeDoubleOfItself() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts");
		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind1);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	@Test
	void should_KinderBePossibleDoubles_when_allNamesNotNullAndEqual() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts");
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts");

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.TRUE, result);
	}

	@Test
	void should_KinderBePossibleDoubles_when_allNamesButVornameNullAndVornameEqual() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld");
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald");

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.TRUE, result);
	}

	@Test
	void should_KinderNotBePossibleDoubles_when_oneVornameNull() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withNachname("HEimeLig").withZusatz("Bank rechts");
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts");

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	@Test
	void should_KinderNotBePossibleDoubles_when_oneVornameBlank() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("  ").withNachname("HEimeLig").withZusatz("Bank rechts");
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts");

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	///////////////////////////////////////////////////////

	@Test
	void should_KinderBePossibleDoubles_when_allNamesButNachnameNullAndVornameEqual() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withNachname("HEimeLig");
		Kind kind2 = new Kind(new Identifier("zwei")).withNachname("Heimelig");

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.TRUE, result);
	}

	@Test
	void should_KinderNotBePossibleDoubles_when_oneNachnameNull() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withZusatz("Bank rechts");
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts");

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	@Test
	void should_KinderNotBePossibleDoubles_when_oneNachnameBlank() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts");
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("    ").withZusatz("Bank rechts");

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	///////////////////////////////////////////////////////

	@Test
	void should_KinderBePossibleDoubles_when_allNamesButZusatzNull() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withZusatz("HEimeLig");
		Kind kind2 = new Kind(new Identifier("zwei")).withZusatz("Heimelig");

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.TRUE, result);
	}

	@Test
	void should_KinderNotBePossibleDoubles_when_oneZusatzNull() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("Heimelig");
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts");

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	@Test
	void should_KinderNotBePossibleDoubles_when_oneZusatzBlank() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("Heimelig").withZusatz("  ");
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts");

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}
}
