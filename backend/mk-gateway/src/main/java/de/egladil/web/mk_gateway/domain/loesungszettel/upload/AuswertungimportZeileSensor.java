// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * AuswertungimportZeileSensor
 */
public class AuswertungimportZeileSensor {

	private static final String UEBESCHRIFT_INDIKATOR = "punkte";

	private static final String LEERE_ZEILE_INDIKATOR = ",,0.0,,";

	/**
	 * @param  string
	 * @return        boolean
	 */
	public boolean isUeberschrift(final String string) {

		return StringUtils.isBlank(string) ? false : string.toLowerCase().endsWith(UEBESCHRIFT_INDIKATOR);
	}

	/**
	 * @param  string
	 * @return        boolean
	 */
	public boolean isLeereZeile(final String string) {

		return StringUtils.isBlank(string) ? true : string.contains(LEERE_ZEILE_INDIKATOR);
	}

	/**
	 * @param  ueberschrift
	 * @return              Klassenstufe
	 */
	public Klassenstufe detectKlassenstufe(final String ueberschrift) {

		if (ueberschrift == null) {

			throw new IllegalArgumentException("ueberschrift null");
		}

		if (!isUeberschrift(ueberschrift)) {

			throw new MkGatewayRuntimeException(
				"'" + ueberschrift + "' ist keine Ueberschrift. Klassenstufe laesst sich nicht ermitteln");
		}

		String[] tokens = StringUtils.split(ueberschrift.toUpperCase(), ",");

		int anzahlA = Long.valueOf(Arrays.stream(tokens).filter(t -> t.contains("A-")).count()).intValue();

		switch (anzahlA) {

		case 2:
			return Klassenstufe.IKID;

		case 4:
			return Klassenstufe.EINS;

		case 5:
			return Klassenstufe.ZWEI;

		default:
			break;
		}
		throw new UploadFormatException("unerwartete Anzahl A- in '" + ueberschrift + "'. Klassenstufe lässt sich nicht ermitteln");
	}
}
