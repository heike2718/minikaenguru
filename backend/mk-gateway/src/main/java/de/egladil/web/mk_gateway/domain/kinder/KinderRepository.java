// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

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
	 * Sucht das Kind anhand seiner UUID.
	 *
	 * @param  identifier
	 * @return            Optional
	 */
	Optional<Kind> findKindWithIdentifier(Identifier identifier, WettbewerbID wettbewerbID);

	/**
	 * Fügt ein neues Kind hinzu
	 *
	 * @param  kind
	 *              Kind
	 * @return      Kind
	 */
	Kind addKind(Kind kind);

	/**
	 * Ändert ein vorhandenes Kind
	 *
	 * @param  kind
	 * @return      boolean
	 */
	boolean changeKind(Kind kind);

	/**
	 * Löscht das persistente Kind.
	 *
	 * @param  kind
	 *              Kind
	 * @return      boolean
	 */
	boolean removeKind(Kind kind);

}
