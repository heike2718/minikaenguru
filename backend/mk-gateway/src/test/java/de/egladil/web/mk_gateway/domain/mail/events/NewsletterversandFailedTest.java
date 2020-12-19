// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail.events;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * NewsletterversandFailedTest
 */
public class NewsletterversandFailedTest {

	@Test
	void should_serializeWork() {

		// Arrange
		List<String> invalidMailaddresses = Arrays.asList(new String[] { "hdwinkel@egladil.de", "info@egladil.de" });
		NewsletterversandFailed eventPayload = new NewsletterversandFailed().withInvalidMailaddresses(invalidMailaddresses)
			.withMessage("Fehler beim Mailversand");

		// Act
		String serialization = eventPayload.serializeQuietly();

		// System.out.println(serialization);

		assertEquals(
			"NewsletterversandFailed: {\"message\":\"Fehler beim Mailversand\",\"invalidEmails\":[\"hdwinkel@egladil.de\",\"info@egladil.de\"],\"validSentEmails\":null,\"validUnsentEmails\":null}",
			serialization);
	}

}
