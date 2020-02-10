// =====================================================
// Project: Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence;

import java.util.List;

import de.egladil.web.mk_kataloge.domain.KatalogItem;

/**
 * KatalogRepository
 */
public interface KatalogRepository {

	/**
	 * Läd die Länder, die angezeigt werden können, aus der Datenbank.
	 *
	 * @return
	 */
	List<KatalogItem> loadLaender();

	/**
	 * @param  landKuerzel
	 * @return             List
	 */
	List<KatalogItem> loadOrte(String landKuerzel);

}
