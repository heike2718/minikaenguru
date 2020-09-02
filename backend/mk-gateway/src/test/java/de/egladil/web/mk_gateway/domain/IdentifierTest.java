// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * IdentifierTest
 */
public class IdentifierTest {

	@Test
	void should_ConstructorThrowException_when_ParameterNull() {

		try {

			new Identifier(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("identifier darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_ParameterBlank() {

		try {

			new Identifier("              ");
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("identifier darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorInitialize() {

		// Arrange
		String param = "hjshda";

		// Act
		Identifier identifier = new Identifier(param);

		// Assert
		assertEquals(param, identifier.identifier());

		assertEquals(identifier, identifier);
		assertFalse(identifier.equals(new Object()));
		assertFalse(identifier.equals(null));

		assertEquals(param, identifier.toString());
	}

	@Test
	void should_EqualsHashCode_baseOnTheStringAttribute() {

		// Arrange
		Identifier identifier1 = new Identifier("agdguq");
		Identifier identifier2 = new Identifier("agdguq");
		Identifier identifier3 = new Identifier("jdfjdpj");

		// Assert
		assertEquals(identifier1, identifier2);
		assertEquals(identifier1.hashCode(), identifier2.hashCode());
		assertFalse(identifier1.equals(identifier3));
		assertFalse(identifier1.hashCode() == identifier3.hashCode());
	}

}
