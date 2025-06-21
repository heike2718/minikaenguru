// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.unterlagen;

import java.util.List;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.statistik.admin.AdminStatistikAuspraegung;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * DownloadsRepository
 */
public interface DownloadsRepository {

	/**
	 * Sucht alle Downloads des gegebenen Veranstalters zum Wettbewerbsjahr.
	 *
	 * @param  veranstalterID
	 * @param  wettbewerbID
	 * @return
	 */
	List<Download> findDownloadsByVeranstalterAndWettbewerb(Identifier veranstalterID, WettbewerbID wettbewerbID);

	/**
	 * @param download
	 */
	Download addOrUpdate(Download download);

	/**
	 * @param  columnName
	 *                    String
	 * @return            List
	 */
	List<AdminStatistikAuspraegung> countAuspraegungenByColumnName(String columnName, Integer wettbewerbsjahr);
}
