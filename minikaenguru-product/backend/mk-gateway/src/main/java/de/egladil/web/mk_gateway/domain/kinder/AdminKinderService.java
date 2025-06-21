// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import de.egladil.web.mk_gateway.domain.statistik.admin.AdminStatistikGruppeninfo;

/**
 * AdminKinderService
 */
public interface AdminKinderService {

	/**
	 * Erzeugt die aktuell implementierte Statistik für die eingetragenen Kinder.
	 *
	 * @return AdminStatistikGruppeninfo
	 */
	AdminStatistikGruppeninfo createKurzstatistikKinder();

}
