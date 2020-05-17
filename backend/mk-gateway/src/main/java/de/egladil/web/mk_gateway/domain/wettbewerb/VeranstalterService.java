// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import javax.ws.rs.core.Response;

/**
 * VeranstalterService
 */
public interface VeranstalterService {

	/**
	 * Holt die Teilnahmenummern des gegebenen Veranstalters.
	 *
	 * @param  uuid
	 * @return       Response
	 */
	Response getTeilnahmenummern(String uuid);

}
