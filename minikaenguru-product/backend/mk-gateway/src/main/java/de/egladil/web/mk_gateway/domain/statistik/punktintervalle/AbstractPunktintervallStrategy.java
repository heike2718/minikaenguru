// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.statistik.punktintervalle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.egladil.web.mk_gateway.domain.statistik.Punktintervall;
import de.egladil.web.mk_gateway.domain.statistik.PunktintervallDescendingComparator;

/**
 * AbstractPunktintervallStrategy
 */
public abstract class AbstractPunktintervallStrategy implements PunktintervallStrategy {

	AbstractPunktintervallStrategy() {

	}

	@Override
	public List<Punktintervall> getPunktintervalleDescending() {

		final List<Punktintervall> result = new ArrayList<>();
		int minVal = 0;

		for (int i = 0; i < anzahlIntervalle(); i++) {

			minVal = i * 500;
			result.add(createPunktintervall(minVal));
		}
		Collections.sort(result, new PunktintervallDescendingComparator());
		return result;
	}

	/**
	 * Gibt die Anzahl der Intervalle zurück.
	 *
	 * @return int
	 */
	protected abstract int anzahlIntervalle();

	/**
	 * Erzeugt das Punktintervall, das bei minVal beginnt.
	 *
	 * @param  minVal
	 * @return                          PunktIntervall
	 * @throws IllegalArgumentException
	 *                                  wenn minVal kleiner 0 oder größer als die maximal zu erreichende Punktzahl ist.
	 */
	protected abstract Punktintervall createPunktintervall(int minVal);

}
