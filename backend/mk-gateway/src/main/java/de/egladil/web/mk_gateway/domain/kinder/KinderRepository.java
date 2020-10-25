// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.List;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * KinderRepository
 */
public interface KinderRepository {

	/**
	 * Gibt alle Kinder mit dem gleichen TeilnahmeIdentifier zurück.
	 *
	 * @param  teilnahmeIdentifier
	 * @param  wettbewerbID
	 *                             WettbewerbID
	 * @return                     List
	 */
	List<Kind> findKinderWithTeilnahme(TeilnahmeIdentifier teilnahmeIdentifier, WettbewerbID wettbewerbID);

	/**
	 * Fügt ein neues Kind hinzu
	 *
	 * @param  kind
	 *              Kind
	 * @return      Kind
	 */
	Kind addKind(Kind kind);

}
