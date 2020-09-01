// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.signup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.auth.signup.SignUpResourceOwner;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * SignUpResourceOwnerTest
 */
public class SignUpResourceOwnerTest {

	@Test
	void should_ConstructorThrowException_when_UuidNull() {

		// Arrange
		final String uuid = null;
		final String fullName = "Maxe Meier";
		final String nonce = "LEHRER-26TZ54HE";

		try {

			new SignUpResourceOwner(uuid, fullName, nonce);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("uuid darf nicht blank sein", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_UuidBlank() {

		// Arrange
		final String uuid = " ";
		final String fullName = "Maxe Meier";
		final String nonce = "LEHRER-26TZ54HE";

		try {

			new SignUpResourceOwner(uuid, fullName, nonce);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("uuid darf nicht blank sein", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_FullNameNull() {

		// Arrange
		final String uuid = "hhalsh";
		final String fullName = null;
		final String nonce = "LEHRER-26TZ54HE";

		try {

			new SignUpResourceOwner(uuid, fullName, nonce);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("fullName darf nicht blank sein", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_FullNameBlank() {

		// Arrange
		final String uuid = "dgggigcqig";
		final String fullName = " ";
		final String nonce = "LEHRER-26TZ54HE";

		try {

			new SignUpResourceOwner(uuid, fullName, nonce);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("fullName darf nicht blank sein", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_NonceNull() {

		// Arrange
		final String uuid = "hhalsh";
		final String fullName = "Maxe Meier";
		final String nonce = null;

		try {

			new SignUpResourceOwner(uuid, fullName, nonce);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("nonce darf nicht blank sein", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_NonceBlank() {

		// Arrange
		final String uuid = "dgggigcqig";
		final String fullName = "Maxe Meier";
		final String nonce = "  ";

		try {

			new SignUpResourceOwner(uuid, fullName, nonce);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("nonce darf nicht blank sein", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_NonceDoesNotContainLehrerOrPrivat() {

		// Arrange
		final String uuid = "dgggigcqig";
		final String fullName = "Maxe Meier";
		final String nonce = "aguqgo";

		try {

			new SignUpResourceOwner(uuid, fullName, nonce);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals(
				"Fehler in der Integration mit AuthProvider: das nonce muss entweder den String 'PRIVAT' oder den String 'LEHRER' enthalten",
				e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_NonceLehrerOhneSchulkuerzel() {

		// Arrange
		final String uuid = "dgggigcqig";
		final String fullName = "Maxe Meier";
		final String nonce = "LEHRER";

		try {

			new SignUpResourceOwner(uuid, fullName, nonce);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals(
				"Das nonce für ein Lehrerkonto muss die Form 'LEHRER-SCHULKUERZEL' oder 'LEHRER-SCHULKUERZEL-TRUE' haben, war aber LEHRER",
				e.getMessage());
		}
	}

	@Test
	void should_ConstructorHandleNewsletterToken_when_NonceLehrerLastTokenNotTRUE() {

		// Arrange
		final String uuid = "dgggigcqig";
		final String fullName = "Maxe Meier";
		final String nonce = "LEHRER-BLA-BLUBB";

		// Act
		SignUpResourceOwner owner = new SignUpResourceOwner(uuid, fullName, nonce);

		// Assert
		assertFalse(owner.isNewsletterEmpfaenger());
		assertEquals("Maxe Meier", owner.fullName());
		assertEquals("dgggigcqig", owner.uuid());
		assertEquals(Rolle.LEHRER, owner.rolle());
		assertEquals("BLA", owner.schulkuerzel());

	}

	@Test
	void should_ConstructorHandleNewsletterToken_when_NonceLehrerLastTokenTRUE() {

		// Arrange
		final String uuid = "dgggigcqig";
		final String fullName = "Maxe Meier";
		final String nonce = "LEHRER-BLA-TRUE";

		// Act
		SignUpResourceOwner owner = new SignUpResourceOwner(uuid, fullName, nonce);

		// Assert
		assertTrue(owner.isNewsletterEmpfaenger());
		assertEquals("Maxe Meier", owner.fullName());
		assertEquals("dgggigcqig", owner.uuid());
		assertEquals(Rolle.LEHRER, owner.rolle());
		assertEquals("BLA", owner.schulkuerzel());

	}

	@Test
	void should_ConstructorThrowException_when_NonceLehrerZuVieleToken() {

		// Arrange
		final String uuid = "dgggigcqig";
		final String fullName = "Maxe Meier";
		final String nonce = "LEHRER-BLA-TRUE-BLUBB";

		try {

			new SignUpResourceOwner(uuid, fullName, nonce);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals(
				"Das nonce für ein Lehrerkonto muss die Form 'LEHRER-SCHULKUERZEL' oder 'LEHRER-SCHULKUERZEL-TRUE' haben, war aber LEHRER-BLA-TRUE-BLUBB",
				e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_NonceLehrerSchulkuerzelBlank() {

		// Arrange
		final String uuid = "dgggigcqig";
		final String fullName = "Maxe Meier";
		final String nonce = "LEHRER-";

		try {

			new SignUpResourceOwner(uuid, fullName, nonce);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals(
				"Das nonce für ein Lehrerkonto muss die Form 'LEHRER-SCHULKUERZEL' oder 'LEHRER-SCHULKUERZEL-TRUE' haben, war aber LEHRER-",
				e.getMessage());
		}
	}

	@Test
	void should_ConstructorInitializeRollePrivat() {

		// Arrange
		final String uuid = "dgggigcqig";
		final String fullName = "Maxe Meier";
		final String nonce = "PRIVAT";

		// Act
		SignUpResourceOwner result = new SignUpResourceOwner(uuid, fullName, nonce);

		// Assert
		assertEquals(uuid, result.uuid());
		assertEquals(fullName, result.fullName());
		assertEquals(Rolle.PRIVAT, result.rolle());
		assertFalse(result.isNewsletterEmpfaenger());
		assertNull(result.schulkuerzel());
	}

	@Test
	void should_ConstructorInitializeRollePrivat_when_newsletter() {

		// Arrange
		final String uuid = "dgggigcqig";
		final String fullName = "Maxe Meier";
		final String nonce = "PRIVAT-TRUE";

		// Act
		SignUpResourceOwner result = new SignUpResourceOwner(uuid, fullName, nonce);

		// Assert
		assertEquals(uuid, result.uuid());
		assertEquals(fullName, result.fullName());
		assertEquals(Rolle.PRIVAT, result.rolle());
		assertTrue(result.isNewsletterEmpfaenger());
		assertNull(result.schulkuerzel());
	}

	@Test
	void should_ConstructorInitializeRolleLehrerAndSchulkuerzel() {

		// Arrange
		final String uuid = "dgggigcqig";
		final String fullName = "Maxe Meier";
		final String nonce = "LEHRER-26TZ54HE";

		// Act
		SignUpResourceOwner result = new SignUpResourceOwner(uuid, fullName, nonce);

		// Assert
		assertEquals(uuid, result.uuid());
		assertEquals(fullName, result.fullName());
		assertEquals(Rolle.LEHRER, result.rolle());
		assertEquals("26TZ54HE", result.schulkuerzel());

		assertEquals(result, result);
		assertFalse(result.equals(new Object()));
		assertFalse(result.equals(null));
	}

	@Test
	void should_EqualsHashCode_BaseOnUuid() {

		// Arrange
		final String uuid = "dgggigcqig";
		final String fullName = "Maxe Meier";
		final String nonceLehrer = "LEHRER-26TZ54HE";
		final String noncePrivat = "PRIVAT";

		// Act
		SignUpResourceOwner lehrer = new SignUpResourceOwner(uuid, fullName, nonceLehrer);
		SignUpResourceOwner privat = new SignUpResourceOwner(uuid, fullName, noncePrivat);

		// Assert
		assertEquals(Rolle.LEHRER, lehrer.rolle());
		assertEquals(Rolle.PRIVAT, privat.rolle());
		assertEquals(lehrer, privat);
		assertEquals(lehrer.hashCode(), privat.hashCode());
	}

}
