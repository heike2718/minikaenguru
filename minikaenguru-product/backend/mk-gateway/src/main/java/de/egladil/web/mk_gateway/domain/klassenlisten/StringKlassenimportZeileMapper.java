// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StringKlassenimportZeileMapper
 */
public class StringKlassenimportZeileMapper implements Function<Pair<Integer, String>, Optional<KlassenimportZeile>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringKlassenimportZeileMapper.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	private Map<KlassenlisteFeldart, Integer> feldartenIndizes = new HashMap<>();

	public StringKlassenimportZeileMapper(final KlassenlisteUeberschrift klassenlisteUeberschrift) {

		for (KlassenlisteFeldart feldart : KlassenlisteFeldart.values()) {

			feldartenIndizes.put(feldart, klassenlisteUeberschrift.getIndexFeldart(feldart).get());
		}
	}

	@Override
	public Optional<KlassenimportZeile> apply(final Pair<Integer, String> semikolonseparierteZeileMitIndex) {

		if (semikolonseparierteZeileMitIndex == null) {

			throw new NullPointerException("semikolonseparierteZeileMitIndex");
		}

		if (StringUtils.isBlank(semikolonseparierteZeileMitIndex.getRight())) {

			LOGGER.debug("Zeile {} ist leer. Wird übersprungen", semikolonseparierteZeileMitIndex.getLeft());
			return Optional.empty();
		}

		String[] tokens = StringUtils.split(semikolonseparierteZeileMitIndex.getRight(), ';');

		if (Arrays.stream(tokens).filter(t -> StringUtils.isNotBlank(t)).count() == 0) {

			return Optional.empty();
		}

		String fehlermeldung = null;

		if (tokens.length != 4) {

			fehlermeldung = tokens.length < 4
				? MessageFormat.format(applicationMessages.getString("upload.klassenliste.zeile.zuWenigeAngaben"),
					new Object[] { semikolonseparierteZeileMitIndex.getLeft(), semikolonseparierteZeileMitIndex.getRight() })
				: MessageFormat.format(applicationMessages.getString("upload.klassenliste.zeile.zuVieleAngaben"),
					new Object[] { semikolonseparierteZeileMitIndex.getLeft(), semikolonseparierteZeileMitIndex.getRight() });

			KlassenimportZeile result = new KlassenimportZeile().withFehlermeldung(fehlermeldung)
				.withIndex(semikolonseparierteZeileMitIndex.getLeft().intValue())
				.withImportRohdaten(semikolonseparierteZeileMitIndex.getRight());

			return Optional.of(result);
		}

		String klassenstufeString = tokens[feldartenIndizes.get(KlassenlisteFeldart.KLASSENSTUFE).intValue()].trim();

		Integer klassenstufe = Integer.valueOf(Double.valueOf(klassenstufeString).intValue());

		KlassenimportZeile result = new KlassenimportZeile()
			.withKlasse(tokens[feldartenIndizes.get(KlassenlisteFeldart.KLASSE).intValue()].trim())
			.withKlassenstufe(klassenstufe.toString())
			.withVorname(tokens[feldartenIndizes.get(KlassenlisteFeldart.VORNAME).intValue()].trim())
			.withNachname(tokens[feldartenIndizes.get(KlassenlisteFeldart.NACHNAME).intValue()].trim())
			.withIndex(semikolonseparierteZeileMitIndex.getLeft())
			.withImportRohdaten(semikolonseparierteZeileMitIndex.getRight());

		return Optional.of(result);
	}

}
