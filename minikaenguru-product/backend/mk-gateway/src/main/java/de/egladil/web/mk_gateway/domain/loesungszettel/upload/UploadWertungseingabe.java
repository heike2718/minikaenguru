// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import org.apache.commons.lang3.StringUtils;

/**
 * UploadWertungseingabe
 */
public enum UploadWertungseingabe {

	f,
	n,
	r;

	public static UploadWertungseingabe valueOfString(final String string) {

		if (StringUtils.isBlank(string)) {

			throw new IllegalArgumentException("'" + string + "' ist keine zulaessige Eingabe");
		}

		String trimed = string.trim();

		if (trimed.toLowerCase().equals(trimed)) {

			return UploadWertungseingabe.valueOf(trimed);
		}

		char c = trimed.toLowerCase().charAt(0);

		switch (c) {

		case 'f':
			return f;

		case 'n':
			return n;

		case 'r':
			return r;

		default:
			throw new IllegalArgumentException(
				"No enum constant de.egladil.web.mk_gateway.domain.loesungszettel.upload.UploadWertungseingabe." + trimed);
		}

	}
}
