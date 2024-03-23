// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers;

/**
 * SortnumberRepository
 */
public interface SortnumberRepository {

	/**
	 * Gibt das aktuelle Maximum der SORTNR der gegebenen Tabelle zurück.
	 *
	 * @param  sortedTable
	 *                     SortedTable
	 * @return             long
	 */
	long getMaxSortnumber(SortedTable sortedTable);

}
