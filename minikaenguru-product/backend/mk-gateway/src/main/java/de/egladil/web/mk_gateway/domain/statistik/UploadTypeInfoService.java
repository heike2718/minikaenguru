// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.Map;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;

/**
 * UploadTypeInfoService
 */
public interface UploadTypeInfoService {

	/**
	 * Ermittelt die Anzahl von Uploads gruppiert nach UploadType zu einer gegebenen Teilnahme. Die Map ist immer
	 * vollständig, d.h. für alle UploadTypes gibt es eine Anzahl.
	 *
	 * @param  teilnahmeIdentifier
	 *                             TeilnahmeIdentifier
	 * @return                     Map
	 */
	Map<UploadType, Long> ermittleUploadTypesForTeilnahme(TeilnahmeIdentifier teilnahmeIdentifier);

}
