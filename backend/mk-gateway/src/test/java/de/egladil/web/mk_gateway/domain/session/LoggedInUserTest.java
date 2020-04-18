// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.model.Rolle;

/**
 * LoggedInUserTest
 */
public class LoggedInUserTest {

	@Test
	void should_CreateThrow_when_uuidNull() {

		// Arrange
		String uuid = null;
		Rolle rolle = Rolle.LEHRER;
		String fullName = "jkaqdwhq";
		String idReference = "jksdgq";

		// Act
		try {

			LoggedInUser.create(uuid, rolle, fullName, idReference);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			// Assert
			assertEquals("uuid darf nicht blank sein", e.getMessage());
		}

	}

	@Test
	void should_CreateThrow_when_uuidBlank() {

		// Arrange
		String uuid = "  ";
		Rolle rolle = Rolle.LEHRER;
		String fullName = "jkaqdwhq";
		String idReference = "jksdgq";

		// Act
		try {

			LoggedInUser.create(uuid, rolle, fullName, idReference);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			// Assert
			assertEquals("uuid darf nicht blank sein", e.getMessage());
		}

	}

	@Test
	void should_CreateThrow_when_fullNameNull() {

		// Arrange
		String uuid = "jksdgq";
		Rolle rolle = Rolle.LEHRER;
		String fullName = null;
		String idReference = "sdjgwgoö";

		// Act
		try {

			LoggedInUser.create(uuid, rolle, fullName, idReference);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			// Assert
			assertEquals("fullName darf nicht blank sein", e.getMessage());
		}

	}

	@Test
	void should_CreateThrow_when_fullNameBlank() {

		// Arrange
		String uuid = "jksdgq";
		Rolle rolle = Rolle.LEHRER;
		String fullName = "";
		String idReference = "asdqwdq";

		// Act
		try {

			LoggedInUser.create(uuid, rolle, fullName, idReference);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			// Assert
			assertEquals("fullName darf nicht blank sein", e.getMessage());
		}

	}

	@Test
	void should_CreateThrow_when_rolleNull() {

		// Arrange
		String uuid = "jksdgq";
		Rolle rolle = null;
		String fullName = "skldhlw";
		String idReference = "sdjgwgoö";

		// Act
		try {

			LoggedInUser.create(uuid, rolle, fullName, idReference);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			// Assert
			assertEquals("rolle darf nicht null sein", e.getMessage());
		}

	}

	@Test
	void should_CreateSetAttributes_when_ParametersValid() {

		// Arrange
		String uuid = "jksdgq";
		Rolle rolle = Rolle.LEHRER;
		String fullName = "diüipdas";
		String idReference = "asdqwdq";

		// Act
		LoggedInUser user = LoggedInUser.create(uuid, rolle, fullName, idReference);

		// Assert
		assertEquals(uuid, user.uuid());
		assertEquals(rolle, user.rolle());
		assertEquals(fullName, user.fullName());
		assertEquals(idReference, user.idReference());
		assertEquals(uuid, user.getName());

		assertEquals("LoggedInUser [uuid=jksdgq, rolle=LEHRER, fullName=diüipdas]", user.toString());
	}

	@Test
	void should_EqualsHashCodeEqual_when_UuidsEqual() {

		// Arrange
		String uuid = "klsdhvlsh";
		LoggedInUser user1 = LoggedInUser.create(uuid, Rolle.LEHRER, "Klaus", "hsjlfh");
		LoggedInUser user2 = LoggedInUser.create(uuid, Rolle.ADMIN, "Ruth", "aswrejdöfjg");

		// Act + Assert
		assertEquals(user1, user2);
		assertEquals(user1.hashCode(), user2.hashCode());

	}

	@Test
	void should_notEqual_when_DifferentConditions() {

		// Arrange
		String uuid = "klsdhvlsh";
		LoggedInUser user1 = LoggedInUser.create(uuid, Rolle.LEHRER, "Klaus", "hsjlfh");

		// assert
		assertEquals(user1, user1);
		assertFalse(user1.equals(null));
		assertFalse(user1.equals(new Object()));
	}

}
