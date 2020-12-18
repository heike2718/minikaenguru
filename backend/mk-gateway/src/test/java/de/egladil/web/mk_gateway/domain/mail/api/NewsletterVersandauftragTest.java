// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail.api;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;

/**
 * NewsletterVersandauftragTest
 */
public class NewsletterVersandauftragTest {

	@Test
	void serialize() throws JsonProcessingException {

		// Arrange
		NewsletterVersandauftrag auftrag = NewsletterVersandauftrag.create("412b06b1-39a5-4fdc-a4f3-2c9e00ea1af3",
			Empfaengertyp.TEST);

		// Act
		String serialized = new ObjectMapper().writeValueAsString(auftrag);

		System.out.println(serialized);

	}
}
