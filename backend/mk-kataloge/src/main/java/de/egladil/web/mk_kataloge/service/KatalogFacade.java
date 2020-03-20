// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.service;

import java.util.List;

import de.egladil.web.mk_kataloge.domain.KatalogItem;

/**
 * KatalogFacade
 */
public interface KatalogFacade {

	/**
	 * Gibt die Anzahl der Orte im Land mit dem gegebenen Kürzel zurück
	 *
	 * @param  kuerzel
	 * @return
	 */
	int countOrteInLand(String kuerzel);

	/**
	 * Läd alle Orte in einem Land, sofern es nicht zu viele sind.
	 *
	 * @param  kuerzel
	 * @return             List
	 */
	List<KatalogItem> loadOrteInLand(String kuerzel);

	/**
	 * Gibt die Anzahl der Schulen im Ort mit dem gegebenen Kürzel zurück
	 *
	 * @param  kuerzel
	 * @return
	 */
	int countSchulenInOrt(String kuerzel);

	/**
	 * Läd alle Schulen in einem Ort, sofern es nicht zu viele sind.
	 *
	 * @param  kuerzel
	 * @return             List
	 */
	List<KatalogItem> loadSchulenInOrt(String kuerzel);
}
