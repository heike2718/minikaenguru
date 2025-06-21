// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.statistik.punktintervalle;

import java.util.List;

import de.egladil.web.mk_gateway.domain.statistik.Punktintervall;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * PunktintervallStrategy
 */
public interface PunktintervallStrategy {

	/**
	 * Gibt alle Punktintervalle eines Minikänguruwettbewerbs in absteigender Reihenfolge sortiert zurück;
	 *
	 * @return List von Punktintervall-Instanzen.
	 */
	List<Punktintervall> getPunktintervalleDescending();

	/**
	 * @return Klassenstufe
	 */
	Klassenstufe klassenstufe();

	static PunktintervallStrategy createStrategy(final Klassenstufe klassenstufe, final WettbewerbID wettbewerbId) {

		switch (klassenstufe) {

		case EINS:
			if (wettbewerbId.jahr() < 2017) {

				return new PunktintervallKlasseZweiStrategy();
			}
			return new PunktintervallKlasseEinsStrategy();

		case ZWEI:
			return new PunktintervallKlasseZweiStrategy();

		case IKID:
			return new PunktintervallInklusionStrategy();

		default:
			throw new IllegalArgumentException("Keine Factory für Klassenstufe " + klassenstufe + " implementiert");
		}
	}

}
