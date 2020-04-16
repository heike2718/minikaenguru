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

import de.egladil.web.mk_gateway.domain.model.Rolle;

/**
 * PrivatmenschCreatedTest
 */
public class PrivatmenschCreatedTest {

	@Test
	void should_ConstructorThrowException_when_OccuredOnNull() {

		// Arrange
		final LocalDateTime occouredOn = null;
		final String uuid = "cskjags";
		final String fullName = "Maxe Malle";

		// Act
		try {

			new PrivatmenschCreated(occouredOn, uuid, fullName);
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

		// Act
		try {

			new PrivatmenschCreated(occouredOn, uuid, fullName);
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

		// Act
		try {

			new PrivatmenschCreated(occouredOn, uuid, fullName);
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

		// Act
		try {

			new PrivatmenschCreated(occouredOn, uuid, fullName);
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

		// Act
		try {

			new PrivatmenschCreated(occouredOn, uuid, fullName);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("fullName darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ConstructorCreate_when_everythingIsOk() throws Exception {

		// Arrange
		final LocalDateTime occouredOn = LocalDateTime.now();
		final String uuid = "asbjkhq";
		final String fullName = "Maxe Malle";

		TimeUnit.MILLISECONDS.sleep(200);

		// Act
		PrivatmenschCreated event = new PrivatmenschCreated(occouredOn, uuid, fullName);

		// Assert
		assertTrue(LocalDateTime.now().isAfter(event.occuredOn()));
		assertEquals(uuid, event.uuid());
		assertEquals(fullName, event.fullName());
		assertEquals("PrivatmenschCreated", event.typeName());
		assertEquals(Rolle.PRIVAT, event.rolle());

	}

}
