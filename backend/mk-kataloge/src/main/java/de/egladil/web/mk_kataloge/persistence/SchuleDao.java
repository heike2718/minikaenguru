// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence;

import de.egladil.web.mk_kataloge.persistence.impl.entities.Schule;

/**
 * SchuleDao zum Peristieren und updaten von Schulen.
 */
public interface SchuleDao {

	/**
	 * Eine neue Schule wird importiert.
	 *
	 * @param schule
	 */
	void importiereSchule(Schule schule);

	/**
	 * Daten einer neuen Schule werden geändert. Bezieht sich ich Änderung auf den Ortnamen, dann werden gleichzeitig alle Schulen
	 * geändert, die das gleiche Ortkuerzel haben.
	 */
	Schule updateSchule(Schule schule);

}
