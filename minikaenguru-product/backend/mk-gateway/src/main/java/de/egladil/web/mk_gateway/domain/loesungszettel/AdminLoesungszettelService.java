// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import de.egladil.web.mk_gateway.domain.statistik.admin.AdminStatistikGruppeninfo;

/**
 * AdminLoesungszettelService
 */
public interface AdminLoesungszettelService {

	/**
	 * Erzeugt die aktuell implementierte Statistik für die Lösungszettel des aktuellen Wettbewerbs.
	 *
	 * @return AdminStatistikGruppeninfo
	 */
	AdminStatistikGruppeninfo createKurzstatistikLoesungszettel();

}
