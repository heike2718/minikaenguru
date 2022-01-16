// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Gruppeninfo;

/**
 * AdminLoesungszettelService
 */
public interface AdminLoesungszettelService {

	/**
	 * Erzeugt die aktuell implementierte Statistik für die Lösungszettel des aktuellen Wettbewerbs.
	 *
	 * @return Gruppeninfo
	 */
	Gruppeninfo createKurzstatistikLoesungszettel();

}
