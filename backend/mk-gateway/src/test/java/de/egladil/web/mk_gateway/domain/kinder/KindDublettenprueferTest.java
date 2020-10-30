// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

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
	void should_KinderNotBePossibleDoubles_when_klassenstufeDiffersAndKlasseIDAndAllNamesNotNullAndEqual() {

		// Arrange
		Identifier klasseID = new Identifier("Hasen");

		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.ZWEI).withKlasseID(klasseID);
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS).withKlasseID(klasseID);

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	@Test
	void should_KinderBePossibleDoubles_when_klassenstufeAndKlasseIDAndAllNamesNotNullAndEqual() {

		// Arrange
		Identifier klasseID = new Identifier("Hasen");

		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.ZWEI).withKlasseID(klasseID);
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.ZWEI).withKlasseID(klasseID);

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.TRUE, result);
	}

	@Test
	void should_KinderBePossibleDoubles_when_klassenstufeAndAllNamesNotNullAndEqual() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS);
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS);

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.TRUE, result);
	}

	@Test
	void should_KinderNotBePossibleDoubles_when_klassenstufeDiffersAndllNamesNotNullAndEqual() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.ZWEI);
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS);

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	@Test
	void should_KinderBePossibleDoubles_when_klassenstufeAnsAllNamesButVornameNullAndVornameEqual() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withKlassenstufe(Klassenstufe.EINS);
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withKlassenstufe(Klassenstufe.EINS);

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.TRUE, result);
	}

	@Test
	void should_KinderNotBePossibleDoubles_when_oneVornameNull() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withNachname("HEimeLig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS);
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS);

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	@Test
	void should_KinderNotBePossibleDoubles_when_oneVornameBlank() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("  ").withNachname("HEimeLig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS);
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS);

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	///////////////////////////////////////////////////////

	@Test
	void should_KinderBePossibleDoubles_when_allNamesButNachnameNullAndKlassenstufeAndVornameEqual() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withNachname("HEimeLig").withKlassenstufe(Klassenstufe.EINS);
		Kind kind2 = new Kind(new Identifier("zwei")).withNachname("Heimelig").withKlassenstufe(Klassenstufe.EINS);

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.TRUE, result);
	}

	@Test
	void should_KinderNotBePossibleDoubles_when_oneNachnameNull() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS);
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS);

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	@Test
	void should_KinderNotBePossibleDoubles_when_oneNachnameBlank() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS);
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("    ").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS);

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	///////////////////////////////////////////////////////

	@Test
	void should_KinderBePossibleDoubles_when_klassenstufeAndAllNamesButZusatzNull() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withZusatz("HEimeLig").withKlassenstufe(Klassenstufe.EINS);
		Kind kind2 = new Kind(new Identifier("zwei")).withZusatz("Heimelig").withKlassenstufe(Klassenstufe.EINS);

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.TRUE, result);
	}

	@Test
	void should_KinderNotBePossibleDoubles_when_oneZusatzNull() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("Heimelig")
			.withKlassenstufe(Klassenstufe.EINS);
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS);

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}

	@Test
	void should_KinderNotBePossibleDoubles_when_oneZusatzBlank() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("Heimelig").withZusatz("  ")
			.withKlassenstufe(Klassenstufe.EINS);
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
			.withKlassenstufe(Klassenstufe.EINS);

		// Act
		Boolean result = new KindDublettenpruefer().apply(kind1, kind2);

		// Assert
		assertEquals(Boolean.FALSE, result);
	}
}
