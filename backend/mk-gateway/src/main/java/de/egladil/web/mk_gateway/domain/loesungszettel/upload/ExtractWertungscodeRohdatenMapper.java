// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

/**
 * ExtractWertungscodeRohdatenMapper
 */
public class ExtractWertungscodeRohdatenMapper implements Function<String, String> {

	private final boolean withNamen;

	public ExtractWertungscodeRohdatenMapper(final boolean withNamen) {

		this.withNamen = withNamen;
	}

	@Override
	public String apply(final String rohdaten) {

		if (rohdaten == null) {

			throw new IllegalArgumentException("rohdaten null");
		}

		String[] tokens = StringUtils.splitPreserveAllTokens(rohdaten, ';');

		List<String> nans = new ArrayList<>();

		int startIndex = withNamen ? 1 : 0;

		for (int index = startIndex; index < tokens.length; index++) {

			String trToken = tokens[index].trim();

			try {

				Double.valueOf(trToken);
			} catch (NumberFormatException e) {

				if (trToken.length() == 1) {

					nans.add(trToken);
				}
			}
		}

		return StringUtils.join(nans, ";");
	}

}
