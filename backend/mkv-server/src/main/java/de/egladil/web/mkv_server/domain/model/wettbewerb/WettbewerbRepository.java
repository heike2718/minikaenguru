// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.domain.model.wettbewerb;

import java.util.Optional;

/**
 * WettbewerbRepository
 */
public interface WettbewerbRepository {

	/**
	 * Sucht den Wettbewerb mit der gegebenen WettbewerbID.
	 *
	 * @param  wettbewerbID
	 * @return              Optional eines Wettbewerbs
	 */
	Optional<Wettbewerb> wettbewerbMitID(WettbewerbID wettbewerbID);

}
