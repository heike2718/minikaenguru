// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import java.util.function.BiFunction;

/**
 * AntwortcodeWertungscodeMapper
 */
public class AntwortcodeWertungscodeMapper implements BiFunction<String, String, String> {

	@Override
	public String apply(final String antwortcode, final String loesungsbuchstaben) {

		String losungscode = loesungsbuchstaben.replaceAll("-", "").toUpperCase();

		if (antwortcode.length() != losungscode.length()) {

			throw new IllegalArgumentException(
				"Antwortcode " + antwortcode + " und loesungscode " + losungscode + " sind inkompatibel");
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < antwortcode.length(); i++) {

			char a = antwortcode.charAt(i);

			if (a == 'N') {

				sb.append("n");
			} else {

				char l = losungscode.charAt(0);

				if (a == l) {

					sb.append("r");
				} else {

					sb.append("f");
				}
			}
		}

		return sb.toString();
	}

}
