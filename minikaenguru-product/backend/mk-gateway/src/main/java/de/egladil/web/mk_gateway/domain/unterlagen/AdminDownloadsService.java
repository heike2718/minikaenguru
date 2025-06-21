// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.unterlagen;

import de.egladil.web.mk_gateway.domain.statistik.admin.AdminStatistikGruppeninfo;

/**
 * AdminDownloadsService
 */
public interface AdminDownloadsService {

	/**
	 * Statistik für aktuellen Wettbewerb.
	 *
	 * @return AdminStatistikGruppeninfo
	 */
	AdminStatistikGruppeninfo createKurzstatistikDownloads();

}
