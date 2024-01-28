// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * VersandauftraegeRepository
 */
public interface VersandauftraegeRepository {

	/**
	 * Gibt alle Versandinformationen zum gegebenen Newsletter zurück.
	 *
	 * @param  newsletterID
	 *                      Identifier
	 * @return              List
	 */
	List<Versandauftrag> findForNewsletter(Identifier newsletterID);

	/**
	 * Gibt eine Versandauftrag mit einer ID zurück.
	 *
	 * @param  identifier
	 *                    Identifier
	 * @return            Optional
	 */
	Optional<Versandauftrag> ofId(Identifier identifier);

	/**
	 * Speichert eine neue Versandauftrag und gibt sie mit der generierten ID zurück.
	 *
	 * @param  versandinformation
	 *                            Versandauftrag
	 * @return                    Versandauftrag
	 */
	Versandauftrag addVersandinformation(Versandauftrag versandinformation);

	/**
	 * Ändert eine vorhandene Versandauftrag und gibt die geänderte zurück.
	 *
	 * @param  versandinformation
	 * @return
	 */
	Versandauftrag updateVersandinformation(Versandauftrag versandinformation);

	/**
	 * Löscht alle Versandinformationen, die den gegebenen Newsletter referenzieren.
	 *
	 * @param  newsletterID
	 *                      Identifier
	 * @return              int die Anzahl der gelöschten Versandinformationen.
	 */
	int removeAll(Identifier newsletterID);

	/**
	 * Gibt eine Liste aller nicht beendeten Auslieferungen.
	 *
	 * @return List
	 */
	List<Versandauftrag> findNichtBeendeteVersandinfos();

}
