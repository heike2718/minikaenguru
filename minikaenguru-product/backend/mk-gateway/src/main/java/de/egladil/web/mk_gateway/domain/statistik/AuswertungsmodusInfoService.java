// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.veranstalter.api.Auswertungsmodus;

/**
 * AuswertungsmodusInfoService
 */
public interface AuswertungsmodusInfoService {

	/**
	 * Ermittelt aus verschiedenen Parametern die mutmaßliche Auswertungsmodus.
	 *
	 * @param  teilnahmeIdentifier
	 * @return                     Auswertungsmodus
	 */
	Auswertungsmodus ermittleAuswertungsmodusFuerTeilnahme(TeilnahmeIdentifier teilnahmeIdentifier);

}
