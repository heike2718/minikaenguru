// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * KlasseTest
 */
public class KlasseTest {

	@Test
	void should_equalsAndHashCode_baseOnIdentifier() {

		// Arrange
		TeilnahmeIdentifier teilnahmeID1 = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer("AAAAAAAA").withWettbewerbID(new WettbewerbID(2006));

		TeilnahmeIdentifier teilnahmeID2 = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer("AAAAAAAA").withWettbewerbID(new WettbewerbID(2006));

		Klasse klasse1 = new Klasse(new Identifier("eins")).withName("Rudi").withTeilnahmeIdentifier(teilnahmeID1);
		Klasse klasse2 = new Klasse(new Identifier("zwei")).withName("Rudi").withTeilnahmeIdentifier(teilnahmeID1);
		Klasse klasse3 = new Klasse(new Identifier("eins")).withName("Harald").withTeilnahmeIdentifier(teilnahmeID2);

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
		TeilnahmeIdentifier teilnahmeID = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer("AAAAAAAA").withWettbewerbID(new WettbewerbID(2006));

		Klasse klasse = new Klasse(new Identifier("eins")).withName("Rudi").withTeilnahmeIdentifier(teilnahmeID);

		Kind kind = new Kind(new Identifier("FZFZIFIF"));

		// Act
		boolean result = klasse.addKind(kind);

		// Assert
		assertTrue(result);
		assertEquals(1, klasse.kinder().size());
		assertEquals(klasse.getIdentifier(), kind.klasseID());
	}

	@Test
	void should_RemoveKindRemoveKlasseId_when_KindIsPartOfKlasse() {

		// Arrange
		TeilnahmeIdentifier teilnahmeID = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer("AAAAAAAA").withWettbewerbID(new WettbewerbID(2006));

		Klasse klasse = new Klasse(new Identifier("eins")).withName("Rudi").withTeilnahmeIdentifier(teilnahmeID);
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
		TeilnahmeIdentifier teilnahmeID = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer("AAAAAAAA").withWettbewerbID(new WettbewerbID(2006));

		Klasse klasse = new Klasse(new Identifier("eins")).withName("Rudi").withTeilnahmeIdentifier(teilnahmeID);
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
