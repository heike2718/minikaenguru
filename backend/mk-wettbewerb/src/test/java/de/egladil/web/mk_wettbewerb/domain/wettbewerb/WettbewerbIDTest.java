// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.wettbewerb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * WettbewerbIDTest
 */
public class WettbewerbIDTest {

	@Test
	void should_ConstructorThrowException_when_JahrNull() {

		try {

			new WettbewerbID(null);
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

		} catch (IllegalArgumentException e) {

			assertEquals("jahr muss größer 2004 sein", e.getMessage());
		}

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
