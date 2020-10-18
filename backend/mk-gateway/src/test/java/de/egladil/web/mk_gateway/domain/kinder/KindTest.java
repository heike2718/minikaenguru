// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * KindTest
 */
public class KindTest {

	@Test
	void should_equalsAndHashCode_baseOnIdentifier() {

		// Arrange
		Kind kind1 = new Kind(new Identifier("eins")).withVorname("Rudi").withNachname("Rettich");
		Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Rudi").withNachname("Rettich");
		Kind kind3 = new Kind(new Identifier("eins")).withVorname("Harald").withNachname("Hurtig");

		// Assert
		assertEquals(kind1, kind1);
		assertFalse(kind1.equals(null));
		assertFalse(kind1.equals(new Object()));
		assertEquals(kind1, kind3);
		assertEquals(kind1.hashCode(), kind3.hashCode());
		assertFalse(kind1.equals(kind2));
		assertFalse(kind1.hashCode() == kind2.hashCode());
	}

}
