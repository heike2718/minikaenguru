// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers;

/**
 * SortNumberGenerator
 */
public interface SortNumberGenerator {

	/**
	 * Erzeugt die nächste Sortnumber für LOESUNGSTZETTEL.
	 *
	 * @return long
	 */
	long getNextSortnumberLoesungszettel();

	/**
	 * Erzeugt die nächste Sortnumber für UPLOADS.
	 *
	 * @return long
	 */
	long getNextSortnumberUploads();

}
