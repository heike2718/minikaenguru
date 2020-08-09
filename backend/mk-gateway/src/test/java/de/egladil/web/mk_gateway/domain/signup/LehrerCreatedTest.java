// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.signup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * LehrerCreatedTest
 */
public class LehrerCreatedTest {

	@Test
	void should_ConstructorThrowException_when_OccuredOnNull() {

		// Arrange
		final LocalDateTime occouredOn = null;
		final String uuid = "cskjags";
		final String fullName = "Maxe Malle";
		final String schulkuerzel = "H7GDGHJ";

		// Act
		try {

			new LehrerCreated(occouredOn, uuid, fullName, schulkuerzel, true);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("occouredOn darf nicht null sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_UuidNull() {

		// Arrange
		final LocalDateTime occouredOn = LocalDateTime.now();
		final String uuid = null;
		final String fullName = "Maxe Malle";
		final String schulkuerzel = "H7GDGHJ";

		// Act
		try {

			new LehrerCreated(occouredOn, uuid, fullName, schulkuerzel, false);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("uuid darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_UuidBlank() {

		// Arrange
		final LocalDateTime occouredOn = LocalDateTime.now();
		final String uuid = " ";
		final String fullName = "Maxe Malle";
		final String schulkuerzel = "H7GDGHJ";

		// Act
		try {

			new LehrerCreated(occouredOn, uuid, fullName, schulkuerzel, false);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("uuid darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_FullNameNull() {

		// Arrange
		final LocalDateTime occouredOn = LocalDateTime.now();
		final String uuid = "asbjkhq";
		final String fullName = null;
		final String schulkuerzel = "H7GDGHJ";

		// Act
		try {

			new LehrerCreated(occouredOn, uuid, fullName, schulkuerzel, true);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("fullName darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_FullNameBlank() {

		// Arrange
		final LocalDateTime occouredOn = LocalDateTime.now();
		final String uuid = "asbjkhq";
		final String fullName = "";
		final String schulkuerzel = "H7GDGHJ";

		// Act
		try {

			new LehrerCreated(occouredOn, uuid, fullName, schulkuerzel, false);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("fullName darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_SchulkuerzelNull() {

		// Arrange
		final LocalDateTime occouredOn = LocalDateTime.now();
		final String uuid = "asbjkhq";
		final String fullName = "Maxe Malle";
		final String schulkuerzel = null;

		// Act
		try {

			new LehrerCreated(occouredOn, uuid, fullName, schulkuerzel, true);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("schulkuerzel darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorThrowException_when_SchulkuerzelBlank() {

		// Arrange
		final LocalDateTime occouredOn = LocalDateTime.now();
		final String uuid = "asbjkhq";
		final String fullName = "Maxe Malle";
		final String schulkuerzel = "  ";

		// Act
		try {

			new LehrerCreated(occouredOn, uuid, fullName, schulkuerzel, false);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("schulkuerzel darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorCreate_when_everythingIsOk() throws Exception {

		// Arrange
		final LocalDateTime occouredOn = LocalDateTime.now();
		final String uuid = "asbjkhq";
		final String fullName = "Maxe Malle";
		final String schulkuerzel = "H7GDGHJ";

		TimeUnit.MILLISECONDS.sleep(200);

		// Act
		LehrerCreated event = new LehrerCreated(occouredOn, uuid, fullName, schulkuerzel, true);

		// Assert
		assertTrue(LocalDateTime.now().isAfter(event.occuredOn()));
		assertEquals(uuid, event.uuid());
		assertEquals(fullName, event.fullName());
		assertEquals("LehrerCreated", event.typeName());
		assertEquals(schulkuerzel, event.schulkuerzel());
		assertEquals(Rolle.LEHRER, event.rolle());
		assertTrue(event.isNewsletterEmpfaenger());

	}
}
