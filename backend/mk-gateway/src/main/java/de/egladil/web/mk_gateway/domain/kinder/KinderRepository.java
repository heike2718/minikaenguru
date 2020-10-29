// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.List;

import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;

/**
 * KinderRepository
 */
public interface KinderRepository {

	/**
	 * Gibt alle Kinder mit der gleichen Teilnahmenummer zurück.
	 *
	 * @param  teilnahmenummer
	 *                         String
	 * @param  wettbewerbID
	 *                         WettbewerbID
	 * @return                 List
	 */
	List<Kind> findKinderWithTeilnahme(Teilnahme teilnahme);

	/**
	 * Fügt ein neues Kind hinzu
	 *
	 * @param  kind
	 *              Kind
	 * @return      Kind
	 */
	Kind addKind(Kind kind);

}
