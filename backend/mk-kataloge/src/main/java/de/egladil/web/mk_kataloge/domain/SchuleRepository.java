// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Ort;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * SchuleRepository zum Peristieren und updaten von Schulen.
 */
public interface SchuleRepository {

	/**
	 * Eine neue Schule wird importiert.
	 *
	 * @param  schule
	 * @return        boolean
	 */
	boolean addSchule(Schule schule);

	/**
	 * @param  schulen
	 * @return         List
	 */
	boolean replaceSchulen(List<Schule> schulen);

	/**
	 * Sucht die Schule mit dem gegeben kuerzel.
	 *
	 * @param  kuerzel
	 * @return         Optional
	 */
	Optional<Schule> getSchule(String kuerzel);

	/**
	 * Sucht den Ort mit dem gegebenen kuerzel.
	 *
	 * @param  kuerzel
	 * @return         Optional
	 */
	Optional<Ort> getOrt(String kuerzel);

	/**
	 * @param  ortKuerzel
	 * @return            List
	 */
	List<Schule> findSchulenInOrt(String ortKuerzel);

	/**
	 * @param  landKuerzel
	 *                     String
	 * @return             List
	 */
	List<Ort> findOrteInLand(String landKuerzel);

}
