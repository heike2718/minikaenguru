// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;

/**
 * KinderRepository
 */
public interface KinderRepository {

	/**
	 * Gibt alle Kinder mit dem gegebenen teilnahmeIdentifier zurück.
	 *
	 * @param  teilnahmeIdentifier
	 *                             TeilnahmeIdentifierAktuellerWettbewerb
	 * @return                     List
	 */
	List<Kind> withTeilnahme(TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier);

	/**
	 * Sucht das Kind anhand seiner UUID.
	 *
	 * @param  identifier
	 * @return            Optional
	 */
	Optional<Kind> ofId(Identifier identifier);

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

	/**
	 * @param  klasseID
	 * @return
	 */
	int removeKinder(List<Kind> kinder);

	/**
	 * Gib die Anzahl von Kindern in der gegebenen Klasse zurück.
	 *
	 * @param  klasse
	 * @return
	 */
	long countKinderInKlasse(Klasse klasse);
}
