// =====================================================
// Project: Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Land;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Ort;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * KatalogeRepository
 */
public interface KatalogeRepository {

	/**
	 * Gibt alle freigegebenen Länder zurück, deren Name mit searchTerm beginnt.
	 *
	 * @param  searchTerm
	 * @return            List
	 */
	List<Land> findLander(String searchTerm);

	/**
	 * Gibt alle Orte zurück, deren Name mit searchTerm beginnt.
	 *
	 * @param  searchTerm
	 * @return            List
	 */
	List<Ort> findOrte(String searchTerm);

	/**
	 * Gibt alle Schulen zurück, deren Name mit searchTerm beginnt.
	 *
	 * @param  searchTerm
	 * @return            List
	 */
	List<Schule> findSchulen(String searchTerm);

	/**
	 * Gibt alle Orte im Land mit dem kuerzel landKuerzel zurück, deren Name mit searchTerm beginnt.
	 *
	 * @param  landKuerzel
	 *                     String
	 * @param  searchTerm
	 *                     String
	 * @return             List
	 */
	List<Ort> findOrteInLand(String landKuerzel, String searchTerm);

	/**
	 * Gibt alle Schulen im Ort mit dem kuerzel ortKuerzel zurück, deren Name searchTerm enthhält.
	 *
	 * @param  ortKuerzel
	 *                    String
	 * @param  searchTerm
	 *                    String
	 * @return            List
	 */
	List<Schule> findSchulenInOrt(String ortKuerzel, String searchTerm);

	/**
	 * Läd die Länder mit der Anzahl der Orte.
	 *
	 * @return List
	 */
	List<Land> loadLaender();

	/**
	 * Gibt die Anzahl der Schulen im Ort mit dem gegebenen Kürzel zurück
	 *
	 * @param  kuerzel
	 * @return
	 */
	int countSchulenInOrt(String kuerzel);

	/**
	 * Läd alle Schulen im Ort mit dem kuerzel ortKuerzel.
	 *
	 * @param  ortKuerzel
	 * @return            List
	 */
	List<Schule> loadSchulenInOrt(String ortKuerzel);

	/**
	 * Gibt die Anzahl der Orte im Land mit dem gegebenen Kürzel zurück
	 *
	 * @param  kuerzel
	 * @return
	 */
	int countOrteInLand(String kuerzel);

	/**
	 * Gibt die Anzahl der Schulen mit dem gegebenen Kürzel zurück
	 *
	 * @param  kuerzel
	 * @return         int
	 */
	int countSchulenMitKuerzel(String kuerzel);

	/**
	 * Gibt die Anzahl der Orte mit dem gegebenen Kürzel zurück
	 *
	 * @param  kuerzel
	 * @return         int
	 */
	int countOrteMitKuerzel(String kuerzel);

	/**
	 * Läd alle Orte im Land mit dem kuerzel landKuerzel.
	 *
	 * @param  landKuerzel
	 * @return             List
	 */
	List<Ort> loadOrteInLand(String landKuerzel);

	/**
	 * Sucht das Land mit dem gegebenen Kuerzel.
	 *
	 * @param  landKuerzel
	 * @return             Optional
	 */
	Optional<Land> findLandWithKuerzel(String landKuerzel);

	/**
	 * Sucht den Ort mit dem gegebenen Kuerzel.
	 *
	 * @param  ortKuerzel
	 * @return            Optional
	 */
	Optional<Ort> findOrtWithKuerzel(String ortKuerzel);

	/**
	 * Sucht die Schule mit dem gegebenen schulkuerzel.
	 *
	 * @param  schulkuerzel
	 * @return
	 */
	Optional<Schule> findSchuleWithKuerzel(String schulkuerzel);

	/**
	 * Sucht alle Schulen mit den gegebenen Kürzeln.
	 *
	 * @param  schulkuerzel
	 *                      List
	 * @return
	 */
	List<Schule> findSchulenWithKuerzeln(List<String> schulkuerzel);
}
