// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;

/**
 * KlasseTest
 */
public class KlasseTest {

	@Test
	void should_equalsAndHashCode_baseOnIdentifier() {

		// Arrange
		Identifier schuleID = new Identifier("AAAAAAAA");

		Klasse klasse1 = new Klasse(new Identifier("eins")).withName("Rudi")
			.withSchuleID(schuleID);
		Klasse klasse2 = new Klasse(new Identifier("zwei")).withName("Rudi")
			.withSchuleID(schuleID);
		Klasse klasse3 = new Klasse(new Identifier("eins")).withName("Harald")
			.withSchuleID(schuleID);

		// Assert
		assertEquals(klasse1, klasse1);
		assertFalse(klasse1.equals(null));
		assertFalse(klasse1.equals(new Object()));
		assertEquals(klasse1, klasse3);
		assertEquals(klasse1.hashCode(), klasse3.hashCode());
		assertFalse(klasse1.equals(klasse2));
		assertFalse(klasse1.hashCode() == klasse2.hashCode());
	}

	@Test
	void should_AddKindInitReferenceInKind() {

		// Arrange
		Identifier schuleID = new Identifier("AAAAAAAA");

		Klasse klasse = new Klasse(new Identifier("eins")).withName("Rudi").withSchuleID(schuleID);
		Kind kind = new Kind(new Identifier("FZFZIFIF"));

		// Act
		boolean result = klasse.addKind(kind);

		// Assert
		assertTrue(result);
		assertEquals(1, klasse.kinder().size());
		assertEquals(klasse.identifier(), kind.klasseID());
	}

	@Test
	void should_RemoveKindRemoveKlasseId_when_KindIsPartOfKlasse() {

		// Arrange
		Identifier schuleID = new Identifier("AAAAAAAA");

		Klasse klasse = new Klasse(new Identifier("eins")).withName("Rudi").withSchuleID(schuleID);
		Kind kind = new Kind(new Identifier("FZFZIFIF"));
		klasse.addKind(kind);

		// Act
		boolean removed = klasse.removeKind(kind);

		// Assert
		assertTrue(removed);
		assertEquals(0, klasse.kinder().size());
		assertNull(kind.klasseID());

	}

	@Test
	void should_RemoveKindPreserveKlasseId_when_KindIsNotPartOfKlasse() {

		// Arrange
		Identifier schuleID = new Identifier("AAAAAAAA");

		Klasse klasse = new Klasse(new Identifier("eins")).withName("Rudi").withSchuleID(schuleID);
		Kind kind1 = new Kind(new Identifier("FZFZIFIF"));
		klasse.addKind(kind1);

		Kind kind2 = new Kind(new Identifier("GUGIDTD")).withKlasseID(new Identifier("FDDIDLZI"));

		// Act
		boolean removed = klasse.removeKind(kind2);

		// Assert
		assertFalse(removed);
		assertEquals(1, klasse.kinder().size());
		assertEquals(new Identifier("FDDIDLZI"), kind2.klasseID());

	}

}
