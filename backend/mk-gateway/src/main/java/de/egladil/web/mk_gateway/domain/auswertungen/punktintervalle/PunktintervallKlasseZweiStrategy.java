// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.auswertungen.punktintervalle;

import de.egladil.web.mk_gateway.domain.auswertungen.Punktintervall;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * PunktintervallKlasseZweiStrategy
 */
public class PunktintervallKlasseZweiStrategy extends AbstractPunktintervallStrategy {

	PunktintervallKlasseZweiStrategy() {

	}

	@Override
	protected int anzahlIntervalle() {

		return 16;
	}

	@Override
	public Klassenstufe klassenstufe() {

		return Klassenstufe.ZWEI;
	}

	@Override
	protected Punktintervall createPunktintervall(final int minVal) {

		if (minVal < 0 || minVal > 7500) {

			throw new IllegalArgumentException("minVal muss zwischen 0 und 7500 liegen: minVal=" + minVal);
		}
		int obere = minVal + Punktintervall.DEFAULT_LAENGE;

		switch (obere) {

		case 6975:
			obere = 6900;
			break;

		case 7475:
			obere = 7200;
			break;

		default:
			break;
		}

		if (obere > 7500) {

			obere = 7500;
		}
		return new Punktintervall.Builder(minVal).maxVal(obere).build();
	}
}
