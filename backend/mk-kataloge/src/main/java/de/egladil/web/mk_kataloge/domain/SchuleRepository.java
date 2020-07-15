// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import java.util.Optional;

import de.egladil.web.mk_kataloge.domain.error.DataInconsistencyException;
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
	 * Name einer Schule wird geändert.
	 */
	boolean replaceSchule(Schule schule);

	/**
	 * Sucht eine Schule gleichen Namens im gegebenen Ort.
	 *
	 * @param  kuerzelOrt
	 * @param  name
	 * @return            Optional
	 */
	Optional<Schule> findSchuleInOrtMitName(String kuerzelOrt, String name) throws DataInconsistencyException;

	/**
	 * @param  kuerzel
	 * @return         Optional
	 */
	Optional<Schule> findSchuleByKuerzel(String kuerzel);

}
