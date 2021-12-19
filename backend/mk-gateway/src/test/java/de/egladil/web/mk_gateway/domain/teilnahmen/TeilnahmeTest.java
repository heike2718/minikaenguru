// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * TeilnahmeTest
 */
public class TeilnahmeTest {

	@Test
	void should_EqualsHashCode_baseOn_KuerzelWettbewerbId() {

		// Arrange
		WettbewerbID wettbewerb1 = new WettbewerbID(2020);
		WettbewerbID wettbewerb2 = new WettbewerbID(2019);

		Identifier schuleId1 = new Identifier("gasdguq");
		Identifier lehrerId1 = new Identifier("sgahpsahj");

		Identifier schuleId2 = new Identifier("ysdahoasd");

		Schulteilnahme teilnahme1 = new Schulteilnahme(wettbewerb1, schuleId1, "Christaschule", lehrerId1);
		Teilnahme teilnahme2 = new Schulteilnahme(wettbewerb1, schuleId2, "Anderssenschule", lehrerId1);
		Teilnahme teilnahme3 = new Schulteilnahme(wettbewerb2, schuleId1, "Christaschule", lehrerId1);

		Teilnahme teilnahme4 = new Privatteilnahme(wettbewerb1, schuleId1);

		// Act
		assertEquals(teilnahme1, teilnahme1);
		assertFalse(teilnahme1.equals(teilnahme2));
		assertFalse(teilnahme1.equals(teilnahme3));
		assertFalse(teilnahme1.equals(null));
		assertFalse(teilnahme1.equals(new Object()));
		assertFalse(teilnahme1.equals(teilnahme4));

		assertEquals(teilnahme1.hashCode(), teilnahme1.hashCode());
		assertFalse(teilnahme1.hashCode() == teilnahme3.hashCode());

		assertEquals(wettbewerb1, teilnahme1.wettbewerbID());
		assertEquals(schuleId1, teilnahme1.teilnahmenummer());
		assertEquals("Christaschule", teilnahme1.nameSchule());
		assertEquals(lehrerId1, teilnahme1.angemeldetDurchVeranstalterId());

		assertEquals(Teilnahmeart.SCHULE, teilnahme2.teilnahmeart());
		assertEquals(Teilnahmeart.PRIVAT, teilnahme4.teilnahmeart());

		assertEquals("Schulteilnahme [wettbewerbID=2020, teilnahmekuerzel=gasdguq, name=Christaschule, angemeldetDurch=sgahpsahj]",
			teilnahme1.toString());
		assertEquals("Privatteilnahme [wettbewerbID=2020, teilnahmekuerzel=gasdguq]", teilnahme4.toString());
	}

	@Test
	void should_ConstructorThrowException_when_WettbewerbIdNull() {

		try {

			new Schulteilnahme(null, new Identifier("bjksgwk"), "Had Fae", new Identifier("sgwq"));

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("wettbewerbID darf nicht null sein", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_TeilnahmeIdNull() {

		try {

			new Schulteilnahme(new WettbewerbID(2020), null, "Had Fae", new Identifier("sgwq"));

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("teilnahmenummer darf nicht null sein", e.getMessage());
		}

	}

	@Test
	void should_ConstructorNotThrowException_when_VeranstalterIdNull() {

		// Act
		Schulteilnahme schulteilnahme = new Schulteilnahme(new WettbewerbID(2020), new Identifier("bjksgwk"), "Had Fae", null);

		// Assert
		assertNull(schulteilnahme.angemeldetDurchVeranstalterId());
	}

	@Test
	void should_ConstructorThrowException_when_NameSchuledNull() {

		try {

			new Schulteilnahme(new WettbewerbID(2020), new Identifier("bjksgwk"), null, new Identifier("sgwq"));

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("nameSchule darf nicht blank sein", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_NameSchuledBlank() {

		try {

			new Schulteilnahme(new WettbewerbID(2020), new Identifier("bjksgwk"), "   ", new Identifier("sgwq"));

			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("nameSchule darf nicht blank sein", e.getMessage());
		}

	}

}
