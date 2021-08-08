// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 * UploadWertungseingabeTest
 */
public class UploadWertungseingabeTest {

	@Test
	void should_valueOfStringThrowException_when_null() {

		try {

			UploadWertungseingabe.valueOfString(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("'null' ist keine zulaessige Eingabe", e.getMessage());
		}
	}

	@Test
	void should_valueOfStringThrowException_when_blank() {

		try {

			UploadWertungseingabe.valueOfString("  ");
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("'  ' ist keine zulaessige Eingabe", e.getMessage());
		}
	}

	@Test
	void should_valueOfStringThrowException_when_longerThan1() {

		try {

			UploadWertungseingabe.valueOfString(" ff ");
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("No enum constant de.egladil.web.mk_gateway.domain.loesungszettel.upload.UploadWertungseingabe.ff",
				e.getMessage());
		}
	}

	@Test
	void should_valueOfStringThrowException_when_notFRNfrn() {

		String unerlaubteZeichen = "0123456789abcdeghijklmopqstuvwxyzäöü!\"§$%&/()=?}][{+*~#';,:._-']"
			+ "abcdeghijklmopqstuvwxyzäöü".toUpperCase();

		char[] chars = unerlaubteZeichen.toCharArray();

		for (char c : chars) {

			String str = String.valueOf(c);

			try {

				UploadWertungseingabe.valueOfString(str);
				fail("keine IllegalArgumentException bei " + str);
			} catch (IllegalArgumentException e) {

				assertEquals(
					"No enum constant de.egladil.web.mk_gateway.domain.loesungszettel.upload.UploadWertungseingabe."
						+ str,
					e.getMessage());
			}
		}
	}

}
