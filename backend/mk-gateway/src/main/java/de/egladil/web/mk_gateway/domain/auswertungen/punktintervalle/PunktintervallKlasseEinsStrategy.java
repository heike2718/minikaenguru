// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.auswertungen.punktintervalle;

import de.egladil.web.mk_gateway.domain.auswertungen.Punktintervall;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * PunktintervallKlasseEinsStrategy
 */
public class PunktintervallKlasseEinsStrategy extends AbstractPunktintervallStrategy {

	PunktintervallKlasseEinsStrategy() {

	}

	@Override
	protected int anzahlIntervalle() {

		return 13;
	}

	@Override
	public Klassenstufe klassenstufe() {

		return Klassenstufe.EINS;
	}

	@Override
	protected Punktintervall createPunktintervall(final int minVal) {

		if (minVal < 0 || minVal > 6000) {

			throw new IllegalArgumentException("minVal muss zwischen 0 und 6000 liegen: minVal=" + minVal);
		}
		int obere = minVal + Punktintervall.DEFAULT_LAENGE;

		switch (obere) {

		case 5475:
			obere = 5400;
			break;

		case 5975:
			obere = 5700;
			break;

		default:
			break;
		}

		if (obere > 6000) {

			obere = 6000;
		}
		return new Punktintervall.Builder(minVal).maxVal(obere).build();
	}

}
