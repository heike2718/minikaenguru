// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.application;

import java.util.List;

import de.egladil.web.mk_kataloge.domain.KatalogItem;

/**
 * KatalogsucheFacade stellt Methoden für den lesenden Zugriff auf die Kataloge zur Verfügung.
 */
public interface KatalogsucheFacade {

	/**
	 * Gibt alle Länder zurück, deren Name mit suchbegriff beginnt.
	 *
	 * @param  suchbegriff
	 *                     String
	 * @return             List
	 */
	List<KatalogItem> sucheLaenderMitNameBeginnendMit(String suchbegriff);

	/**
	 * Gibt alle Orte zurück, deren Name mit suchbegriff beginnt.
	 *
	 * @param  suchbegriff
	 * @return             List
	 */
	List<KatalogItem> sucheOrteMitNameBeginnendMit(String suchbegriff);

	/**
	 * Gibt alle Schulen zurück, deren Name mit suchbegriff beginnt.
	 *
	 * @param  suchbegriff
	 *                     String
	 * @return             List
	 */
	List<KatalogItem> sucheSchulenMitNameEnthaltend(String suchbegriff);

	/**
	 * Gibt alle Orte im Land mit dem Kuerzel landkuerzel zurück, deren Name mit suchbegriff beginnt.
	 *
	 * @param  landkuerzel
	 *                     String
	 * @param  suchbegriff
	 *                     String
	 * @return             List
	 */
	List<KatalogItem> sucheOrteInLandMitNameBeginnendMit(String landkuerzel, String suchbegriff);

	/**
	 * Gibt alle Schulen im Ort mit dem Kuerzel ortkuerzel zurück, deren Name mit suchbegriff beginnt.
	 *
	 * @param  ortkuerzel
	 *                     String
	 * @param  suchbegriff
	 *                     String
	 * @return             List
	 */
	List<KatalogItem> sucheSchulenInOrtMitNameEnthaltend(String ortkuerzel, String suchbegriff);
}
