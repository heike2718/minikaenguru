// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.veranstalter.api.Wettbewerbsauswertungsart;

/**
 * WettbewerbsauswertungsartInfoService
 */
public interface WettbewerbsauswertungsartInfoService {

	/**
	 * Ermittelt aus verschiedenen Parametern die mutmaßliche Wettbewerbsauswertungsart.
	 *
	 * @param  teilnahmeIdentifier
	 * @return                     Wettbewerbsauswertungsart
	 */
	Wettbewerbsauswertungsart ermittleAuswertungsartFuerTeilnahme(TeilnahmeIdentifier teilnahmeIdentifier);

}
