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

	@Override
	public String apply(final String rohdaten) {

		if (rohdaten == null) {

			throw new IllegalArgumentException("rohdaten null");
		}

		String[] tokens = StringUtils.splitPreserveAllTokens(rohdaten, ',');

		List<String> nans = new ArrayList<>();

		for (String token : tokens) {

			String trToken = token.trim();

			try {

				Double.valueOf(trToken);
			} catch (NumberFormatException e) {

				if (trToken.length() == 1) {

					nans.add(trToken);
				}
			}
		}

		return StringUtils.join(nans, ",,");
	}

}
