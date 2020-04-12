// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.wettbewerb;

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
