// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_commons.constants.MkConstants;
import de.egladil.web.mk_commons.exception.MkRuntimeException;

/**
 * KuerzelGeneratorTest
 */
public class KuerzelGeneratorTest {

	@Test
	void generateKuerzel_klappt() {

		// Arrange
		final int laenge = MkConstants.LAENGE_SCHULKUERZEL;
		final char[] charset = MkConstants.SCHULKURZEL_CHARS;

		final int[] ids = new int[] { 6771, 6772, 6773, 6774, 6775, 6776, 6777, 6778, 6779, 6780, 6781, 6782, 6783, 6784, 6785,
			6786, 6787, 6788, 6789, 6790, 6791, 6792, 6793, 6794, 6795, 6796 };

		// Act
		for (final int id : ids) {

			final String kuerzel = KuerzelGenerator.generateKuerzel(laenge, charset);
			System.out.println("insert into kat_schulen (kuerzel, name, ort) values ('" + kuerzel + "', 'Unbekannt', " + id + ");");

			// Assert
			assertNotNull(kuerzel);
			assertEquals(laenge, kuerzel.length());
		}
	}

	@Test
	void generateKuerzel_exception_wenn_charPool_null() {

		final Throwable ex = assertThrows(MkRuntimeException.class, () -> {

			KuerzelGenerator.generateKuerzel(10, null);
		});
		assertEquals("charPool darf nicht null sein", ex.getMessage());

	}

	@Test
	void generateKuerzel_exception_wenn_charPool_kuerzer_als_26() {

		final Throwable ex = assertThrows(MkRuntimeException.class, () -> {

			KuerzelGenerator.generateKuerzel(3, new String("ABCDEFGHIJKLMNOPQRTSUVWXY").toCharArray());
		});
		assertEquals("charPool muss mindestlaenge 26 haben", ex.getMessage());
	}

	@Test
	@DisplayName("Kuerzel mit Timestamp")
	void kuerzelLang() {

		final String kuerzel = KuerzelGenerator.generateDefaultKuerzelWithTimestamp();
		assertEquals(22, kuerzel.length());
		System.out.println(kuerzel);
	}
}
