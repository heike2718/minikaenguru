// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.UploadFormatException;

/**
 * StringKlassenimportZeileMapper
 */
public class StringKlassenimportZeileMapper implements Function<Pair<Integer, String>, Optional<KlassenimportZeile>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringKlassenimportZeileMapper.class);

	private static final String FEHLERMESSAGE_PATTERN = "Zeile {0}: Fehler! \"{1}\" wird nicht importiert: Vorname, Nachname, Klasse und Klassenstufe lassen sich nicht zuordnen.";

	private Map<KlassenlisteFeldart, Integer> feldartenIndizes = new HashMap<>();

	public StringKlassenimportZeileMapper(final KlassenlisteUeberschrift klassenlisteUeberschrift) {

		for (KlassenlisteFeldart feldart : KlassenlisteFeldart.values()) {

			Optional<Integer> optIndex = klassenlisteUeberschrift.getIndexFeldart(feldart);

			if (optIndex.isEmpty()) {

				String message = "klassenlisteUeberschrift wurde nicht korrekt ermittelt: kein Mapping für KlassenlisteFeldart "
					+ feldart
					+ " moeglich";

				LOGGER.warn(message);

				throw new UploadFormatException(
					"Die hochgeladene Datei kann nicht verarbeitet werden. Die erste Zeile enthält nicht die Felder \"Nachname\", \"Vorname\", \"Klasse\", \"Klassenstufe\".");
			}

			feldartenIndizes.put(feldart, optIndex.get());
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

		String fehlermeldung = null;

		if (tokens.length != 4) {

			fehlermeldung = MessageFormat.format(FEHLERMESSAGE_PATTERN,
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
