// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * SchuleRepository zum Peristieren und updaten von Schulen.
 */
public interface SchuleRepository {

	/**
	 * Eine neue Schule wird importiert.
	 *
	 * @param schule
	 */
	void addSchule(Schule schule);

	/**
	 * Daten einer neuen Schule werden geändert. Bezieht sich ich Änderung auf den Ortnamen, dann werden gleichzeitig alle Schulen
	 * geändert, die das gleiche Ortkuerzel haben.
	 */
	Schule updateSchule(Schule schule);

}
