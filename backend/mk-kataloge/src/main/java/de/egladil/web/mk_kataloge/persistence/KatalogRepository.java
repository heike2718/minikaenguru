// =====================================================
// Project: Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence;

import java.util.List;

import de.egladil.web.mk_kataloge.domain.InverseKatalogItem;

/**
 * KatalogRepository
 */
public interface KatalogRepository {

	/**
	 * Gibt alle Orte zurück, deren Name mit searchTerm beginnt.
	 *
	 * @param  searchTerm
	 * @return            List
	 */
	List<InverseKatalogItem> findOrte(String searchTerm);

	/**
	 * Gibt alle Orte im Land mit dem kuerzel landKuerzel zurück, deren Name mit searchTerm beginnt.
	 *
	 * @param  landKuerzel
	 *                     String
	 * @param  searchTerm
	 *                     String
	 * @return             List
	 */
	List<InverseKatalogItem> findOrteInLand(String landKuerzel, String searchTerm);

	/**
	 * Gibt alle Schulen im Ort mit dem kuerzel ortKuerzel zurück, deren Name mit searchTerm beginnt.
	 *
	 * @param  ortKuerzel
	 *                    String
	 * @param  searchTerm
	 *                    String
	 * @return            List
	 */
	List<InverseKatalogItem> findSchulenInOrt(String ortKuerzel, String searchTerm);

	/**
	 * Läd die Länder mit der Anzahl der Orte.
	 *
	 * @return List
	 */
	List<InverseKatalogItem> loadLaenderInverse();

	/**
	 * Läd alle Schulen im Ort mit dem kuerzel ortKuerzel.
	 *
	 * @param  ortKuerzel
	 * @return            List
	 */
	List<InverseKatalogItem> loadSchulenInOrt(String ortKuerzel);

	/**
	 * Läd alle Orte im Land mit dem kuerzel landKuerzel.
	 *
	 * @param  landKuerzel
	 * @return             List
	 */
	List<InverseKatalogItem> loadOrteInLand(String landKuerzel);

}
