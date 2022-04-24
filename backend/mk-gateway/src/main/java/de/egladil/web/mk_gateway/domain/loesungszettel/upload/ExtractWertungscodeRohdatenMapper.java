// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.statistik.Wertung;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * ExtractWertungscodeRohdatenMapper
 */
public class ExtractWertungscodeRohdatenMapper implements BiFunction<String, String, Pair<String, String>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExtractWertungscodeRohdatenMapper.class);

	private static final List<String> ERWARTBARE_FEHLEINGABEN_IKIDS = Arrays.asList(new String[] { "a", "b", "c" });

	private static final List<String> ERWARTBARE_FEHLEINGABEN = Arrays.asList(new String[] { "a", "b", "c", "d", "e" });

	private final boolean withNamen;

	public ExtractWertungscodeRohdatenMapper(final boolean withNamen) {

		this.withNamen = withNamen;
	}

	@Override
	public Pair<String, String> apply(final String nutzereingabe, final String loesungsbuchstaben) {

		if (nutzereingabe == null) {

			throw new IllegalArgumentException("rohdaten null");
		}

		List<String> nutzertokenOhneName = new ArrayList<>();

		String[] tokens = StringUtils.splitPreserveAllTokens(nutzereingabe, ';');
		char[] loesungsbuchstabenArray = loesungsbuchstaben.toCharArray();

		List<String> nans = new ArrayList<>();

		int startIndex = withNamen ? 1 : 0;

		for (int index = startIndex; index < tokens.length; index++) {

			String trToken = tokens[index].trim();

			try {

				Double.valueOf(trToken);
			} catch (NumberFormatException e) {

				if (trToken.length() == 1) {

					nutzertokenOhneName.add(trToken);

					try {

						Wertung.valueOfStringIgnoringCase(trToken);
					} catch (IllegalArgumentException illegalArgException) {

						String korrekterLoesungsbuchstabe = null;

						if (withNamen) {

							korrekterLoesungsbuchstabe = loesungsbuchstabenArray[index - 1] + "";
						} else {

							korrekterLoesungsbuchstabe = loesungsbuchstabenArray[index] + "";
						}

						Klassenstufe klassenstufe = loesungsbuchstaben.length() == 6 ? Klassenstufe.IKID : Klassenstufe.ZWEI;

						trToken = this.versucheEingabefehlerZuKorrigieren(trToken, index, korrekterLoesungsbuchstabe, klassenstufe);
					}

					nans.add(trToken);
				}
			}
		}

		return Pair.of(StringUtils.join(nans, ";"), StringUtils.join(nutzertokenOhneName, ";"));
	}

	private String versucheEingabefehlerZuKorrigieren(final String input, final int index, final String korrekterLoesungsbuchstabe, final Klassenstufe klassenstufe) {

		String trToken = input.toLowerCase();

		List<String> erwartbareFehleingaben = klassenstufe == Klassenstufe.IKID ? ERWARTBARE_FEHLEINGABEN_IKIDS
			: ERWARTBARE_FEHLEINGABEN;

		if (!erwartbareFehleingaben.contains(trToken)) {

			LOGGER.warn("token {} wird zu n", input);
			return "n";
		}

		if (trToken.equalsIgnoreCase(korrekterLoesungsbuchstabe)) {

			LOGGER.warn("verwechselte Eingabe {} wird zu r", input);

			return "r";
		}

		LOGGER.warn("token {} wird zu f", input);
		return "f";
	}
}
