// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

/**
 * StringListLiner normalisiert (trimshrinkt) die Strings in der Liste und fügt sie in einen durch Leerzeichen getrennten String
 * zusammen.
 */
public class StringListLiner implements Function<List<String>, String> {

	@Override
	public String apply(final List<String> input) {

		if (input == null) {

			return null;
		}

		final List<String> normalizedStrings = new ArrayList<>();
		final TrimShrinker trimShrinker = new TrimShrinker();

		input.forEach(element -> {

			if (element != null) {

				normalizedStrings.add(trimShrinker.apply(element));
			}
		});

		return StringUtils.join(normalizedStrings, " ");
	}

}
