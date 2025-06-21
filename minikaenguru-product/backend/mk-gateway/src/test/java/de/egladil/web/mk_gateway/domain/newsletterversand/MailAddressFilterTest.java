//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * MailAddressFilterTest
 */
public class MailAddressFilterTest {

	@Test
	void should_getFiltered_work_when_bannedNotEmpty() {

		// arrange
		List<String> empfaenger = Arrays
			.asList(new String[] { "eins@gmail.com", "zwei-ohne-domain", "drei@aol.com@knorx", "vier@web.de" });
		List<String> bannedEmails = Arrays.asList(new String[] { "zwei-ohne-domain", "drei@aol.com@knorx" });

		// act
		List<String> result = new MailAddressFilter().getSetDifference(empfaenger, bannedEmails);

		// assert
		assertAll(() -> assertEquals(2, result.size()), () -> assertTrue(result.contains("eins@gmail.com")),
			() -> assertTrue(result.contains("vier@web.de")));

	}

	@Test
	void should_getFiltered_work_when_empfaengerEmpty() {

		// arrange
		List<String> empfaenger = new ArrayList<>();
		List<String> bannedEmails = Arrays.asList(new String[] { "zwei-ohne-domain", "drei@aol.com@knorx" });

		// act
		List<String> result = new MailAddressFilter().getSetDifference(empfaenger, bannedEmails);

		// assert
		assertEquals(0, result.size());

	}

	@Test
	void should_getFiltered_work_when_bannedEmpty() {

		// arrange
		List<String> empfaenger = Arrays.asList(new String[] { "eins@gmail.com", "vier@web.de" });
		List<String> bannedEmails = new ArrayList<>();

		// act
		List<String> result = new MailAddressFilter().getSetDifference(empfaenger, bannedEmails);

		// assert
		assertAll(() -> assertEquals(2, result.size()), () -> assertTrue(result.contains("eins@gmail.com")),
			() -> assertTrue(result.contains("vier@web.de")));

	}

	@Test
	void should_getFiltered_work_when_disjunkt() {

		// arrange
		List<String> empfaenger = Arrays.asList(new String[] { "eins@gmail.com", "vier@web.de" });
		List<String> bannedEmails = Arrays.asList(new String[] { "zwei-ohne-domain", "drei@aol.com@knorx" });

		// act
		List<String> result = new MailAddressFilter().getSetDifference(empfaenger, bannedEmails);

		// assert
		assertAll(() -> assertEquals(2, result.size()), () -> assertTrue(result.contains("eins@gmail.com")),
			() -> assertTrue(result.contains("vier@web.de")));

	}

}
