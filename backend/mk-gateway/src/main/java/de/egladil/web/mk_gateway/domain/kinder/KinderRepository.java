// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.List;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * KinderRepository
 */
public interface KinderRepository {

	/**
	 * Gibt alle Kinder mit dem gleichen TeilnahmeIdentifier zurück.
	 *
	 * @param  teilnahmeIdentifier
	 * @return                     List
	 */
	List<Kind> findKinderWithTeilnahme(TeilnahmeIdentifier teilnahmeIdentifier);

	/**
	 * Fügt ein neues Kind hinzu
	 *
	 * @param  kind
	 *              Kind
	 * @return      Kind
	 */
	Kind addKind(Kind kind);

}
