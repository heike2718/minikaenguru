// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * WettbewerbIDTest
 */
public class WettbewerbIDTest {

	@Test
	void should_ConstructorThrowException_when_JahrNull() {

		Integer jahr = null;

		try {

			new WettbewerbID(jahr);
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("jahr darf nicht null sein", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_JahrKleiner2005() {

		try {

			new WettbewerbID(Integer.valueOf(1999));
			fail("keine InvalidInputException");

		} catch (InvalidInputException e) {

			ResponsePayload response = e.getResponsePayload();
			assertEquals("jahr muss größer 2004 sein", response.getMessage().getMessage());
			assertEquals("ERROR", response.getMessage().getLevel());
		}

	}

	@Test
	void should_DefaultConstructor_initWith0() {

		assertEquals(0, new WettbewerbID().jahr().intValue());
	}

	@Test
	void should_Constructor_initJahr() {

		// Arrange
		Integer jahr = Integer.valueOf(2005 + new Random().nextInt(50));

		// Act
		WettbewerbID wettbewerbID = new WettbewerbID(jahr);

		/// Assert
		assertEquals(jahr, wettbewerbID.jahr());
		assertEquals(jahr.toString(), wettbewerbID.toString());
		assertEquals(wettbewerbID, wettbewerbID);
		assertFalse(wettbewerbID.equals(new Object()));
		assertFalse(wettbewerbID.equals(null));
	}

	@Test
	void should_EqualsHashCode_beImplementedCorrectly() {

		// Arrange
		WettbewerbID erste = new WettbewerbID(Integer.valueOf(2005));
		WettbewerbID zweite = new WettbewerbID(Integer.valueOf(2005));
		WettbewerbID dritte = new WettbewerbID(Integer.valueOf(2006));

		// Assert
		assertEquals(erste, zweite);
		assertEquals(erste.hashCode(), zweite.hashCode());
		assertFalse(erste.equals(dritte));
		assertFalse(erste.hashCode() == dritte.hashCode());

	}

}
