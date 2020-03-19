// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain;

/**
 * ILoesungszettel ist das, was von einem Lösunszettel für die Statistik benötigt wird.
 */
public interface ILoesungszettel {

	/**
	 * @return Integer die Punktzahl dieses Lösungszettels
	 */
	Integer getPunkte();

	/**
	 * @return String den Wertungscode dieses Lösungszettels ohne Komma.
	 */
	String getWertungscode();

	/**
	 * @return Integer Länge des Kängurusprungs
	 */
	Integer getKaengurusprung();

}
