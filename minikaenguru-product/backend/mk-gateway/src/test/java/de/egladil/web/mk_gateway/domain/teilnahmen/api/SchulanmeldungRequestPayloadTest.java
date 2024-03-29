// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.SchulanmeldungRequestPayload;

/**
 * SchulanmeldungRequestPayloadTest
 */
public class SchulanmeldungRequestPayloadTest {

	@Test
	void should_serialize() throws Exception {

		// Arrange
		String schulkuerzel = "EEGEECP6";

		// Act
		SchulanmeldungRequestPayload payload = SchulanmeldungRequestPayload.create(schulkuerzel, "Albert-Schweitzer-Schule");

		// Assert
		assertEquals(schulkuerzel, payload.schulkuerzel());
		assertEquals("Albert-Schweitzer-Schule", payload.schulname());

		System.out.println(new ObjectMapper().writeValueAsString(payload));

	}

}
