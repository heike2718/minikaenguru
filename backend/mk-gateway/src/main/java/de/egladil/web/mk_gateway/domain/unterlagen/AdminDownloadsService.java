// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.unterlagen;

import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Gruppeninfo;

/**
 * AdminDownloadsService
 */
public interface AdminDownloadsService {

	/**
	 * Statistik für aktuellen Wettbewerb.
	 *
	 * @return Gruppeninfo
	 */
	Gruppeninfo createKurzstatistikDownloads();

}
