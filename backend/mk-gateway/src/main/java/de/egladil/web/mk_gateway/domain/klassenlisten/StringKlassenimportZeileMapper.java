// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;

/**
 * StringKlassenimportZeileMapper
 */
public class StringKlassenimportZeileMapper implements Function<String, KlassenimportZeile> {

	private Map<KlassenlisteFeldart, Integer> feldartenIndizes = new HashMap<>();

	public StringKlassenimportZeileMapper(final KlassenlisteUeberschrift klassenlisteUeberschrift) {

		for (KlassenlisteFeldart feldart : KlassenlisteFeldart.values()) {

			Optional<Integer> optIndex = klassenlisteUeberschrift.getIndexFeldart(feldart);

			if (optIndex.isEmpty()) {

				throw new MkGatewayRuntimeException(
					"klassenlisteUeberschrift wurde nicht korrekt ermittelt: kein Mapping für KlassenlisteFeldart " + feldart
						+ " moeglich");
			}

			feldartenIndizes.put(feldart, optIndex.get());
		}
	}

	@Override
	public KlassenimportZeile apply(final String kommaseparierteZeile) {

		if (kommaseparierteZeile == null) {

			throw new NullPointerException("kommaseparierteZeile");
		}

		String[] tokens = StringUtils.split(kommaseparierteZeile, ',');

		if (tokens.length != 4) {

			String msg = "Die Klassenliste kann nicht importiert werden: erwarte genau 4 Einträge in jeder Zeile.";
			throw new UploadFormatException(msg);
		}

		KlassenimportZeile result = new KlassenimportZeile()
			.withKlasse(tokens[feldartenIndizes.get(KlassenlisteFeldart.KLASSE).intValue()].trim())
			.withKlassenstufe(tokens[feldartenIndizes.get(KlassenlisteFeldart.KLASSENSTUFE).intValue()].trim())
			.withVorname(tokens[feldartenIndizes.get(KlassenlisteFeldart.VORNAME).intValue()].trim())
			.withNachname(tokens[feldartenIndizes.get(KlassenlisteFeldart.NACHNAME).intValue()].trim());

		return result;
	}

}
