// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * TrimShrinker entfernt einschließende Leerzeichen und schrumpft innere Leerzeichen auf eins zusammen.
 */
public class TrimShrinker implements Function<String, String> {

	@Override
	public String apply(final String str) {

		if (str == null) {

			return null;
		}

		String[] worte = str.split(" ");
		List<String> trimmedWorte = Arrays.stream(worte).map(wort -> wort.trim()).filter(wort -> !wort.isEmpty())
			.collect(Collectors.toList());

		String result = StringUtils.join(trimmedWorte, " ");
		return result.trim();
	}

}
