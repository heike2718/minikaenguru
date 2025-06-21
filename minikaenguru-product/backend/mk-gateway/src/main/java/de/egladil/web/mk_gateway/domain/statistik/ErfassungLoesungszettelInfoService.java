// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.Map;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * ErfassungLoesungszettelInfoService
 */
public interface ErfassungLoesungszettelInfoService {

	/**
	 * Ermittelt die Anzahl von Lösungszetteln gruppiert nach Auswertungsquelle zu einem gegebenen Wettbewerb. Die Map ist immer
	 * vollständig, d.h. für beide Auswertungsquellen gibt es eine Anzahl.
	 *
	 * @param  teilnahme
	 *                   Teilnahme
	 * @return           Map
	 */
	Map<Auswertungsquelle, Long> ermittleLoesungszettelMitAuswertungsquellenForTeilnahme(TeilnahmeIdentifier teilnahme);

}
