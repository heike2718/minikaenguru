// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.wettbewerb;

import java.util.List;

/**
 * WettbewerbRepository
 */
public interface WettbewerbRepository {

	/**
	 * Läd alle Wettbewerbe.
	 *
	 * @return List
	 */
	List<Wettbewerb> loadWettbewerbe();

}
