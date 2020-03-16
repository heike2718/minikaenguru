// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import de.egladil.web.mk_commons.constants.MkConstants;
import de.egladil.web.mk_commons.exception.MkRuntimeException;

/**
 * KuerzelGenerator generiert Kürzel.
 */
public final class KuerzelGenerator {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	private static Random random = new Random();

	/**
	 *
	 */
	private KuerzelGenerator() {

	}

	/**
	 * Generiert einen Zufallsstring gegeben Länge mit den Zeichen aus charPool. Basiert auf Random.
	 *
	 * @param  laenge
	 *                  int die Länge. Muss mindestens gleich 6 sein.
	 * @param  charPool
	 *                  die verwendeten Zeichen. Muss Mindestlänge 26 haben.
	 * @return          String
	 */
	public static String generateKuerzel(final int length, final char[] charPool) {

		if (charPool == null) {

			throw new MkRuntimeException("charPool darf nicht null sein");
		}

		if (charPool.length < 26) {

			throw new MkRuntimeException("charPool muss mindestlaenge 26 haben");
		}
		final StringBuilder sb = new StringBuilder();

		for (int loop = 0; loop < length; loop++) {

			final int index = random.nextInt(charPool.length);
			sb.append(charPool[index]);
		}
		final String nonce = sb.toString();
		return nonce;
	}

	/**
	 * Generiert ein Kürzel der Standardlänge 8 mit dem Standardzeichensatz A-Z0-0.
	 *
	 * @return
	 */
	public static String generateDefaultKuerzel() {

		final String result = generateKuerzel(MkConstants.DEFAULT_LENGTH, MkConstants.DEFAULT_CHARS);
		return result;
	}

	/**
	 * Generiert ein Kürzel der Standardlänge 22 aus einem Default-Kuerzel und dem aktuellen timestamp format
	 * yyyyMMddHHmmss.
	 *
	 * @return String
	 */
	public static String generateDefaultKuerzelWithTimestamp() {

		final String result = generateKuerzel(MkConstants.DEFAULT_LENGTH, MkConstants.DEFAULT_CHARS);
		final String formatDateTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
		return result + formatDateTime;
	}
}
