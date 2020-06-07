// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.wettbewerb;

import java.util.Optional;

/**
 * WettbewerbRepository
 */
public interface WettbewerbRepository {

	/**
	 * Holt den Wettbewerb mit dem maximalen Jahr.
	 *
	 * @return Optional
	 */
	Optional<Wettbewerb> loadWettbewerbWithMaxJahr();

}
