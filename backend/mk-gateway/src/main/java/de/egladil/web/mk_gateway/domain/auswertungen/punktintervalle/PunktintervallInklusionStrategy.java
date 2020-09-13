// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.auswertungen.punktintervalle;

import de.egladil.web.mk_gateway.domain.auswertungen.Punktintervall;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * PunktintervallInklusionStrategy
 */
public class PunktintervallInklusionStrategy extends AbstractPunktintervallStrategy {

	/**
	 * PunktintervallInklusionStrategy
	 */
	PunktintervallInklusionStrategy() {

	}

	@Override
	protected int anzahlIntervalle() {

		return 8;
	}

	@Override
	public Klassenstufe klassenstufe() {

		return Klassenstufe.IKID;
	}

	@Override
	protected Punktintervall createPunktintervall(final int minVal) {

		if (minVal < 0 || minVal > 3600) {

			throw new IllegalArgumentException("minVal muss zwischen 0 und 3600 liegen: minVal=" + minVal);
		}
		int obere = minVal + Punktintervall.LAENGE_INKLUSION;

		switch (obere) {

		case 2950:
			obere = 2900;
			break;

		case 3450:
			obere = 3300;
			break;

		default:
			break;
		}

		if (obere >= 3500) {

			obere = 3600;
		}

		if (minVal == 3500) {

			return new Punktintervall.Builder(3600).maxVal(obere).build();
		}
		return new Punktintervall.Builder(minVal).maxVal(obere).build();
	}
}
