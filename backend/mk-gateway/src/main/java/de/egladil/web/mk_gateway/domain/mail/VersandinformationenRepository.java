// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * VersandinformationenRepository
 */
public interface VersandinformationenRepository {

	/**
	 * Gibt alle Versandinformationen zum gegebenen Newsletter zurück.
	 *
	 * @param  newsletterID
	 *                      Identifier
	 * @return              List
	 */
	List<Versandinformation> findForNewsletter(Identifier newsletterID);

	/**
	 * Gibt eine Versandinformation mit einer ID zurück.
	 *
	 * @param  identifier
	 *                    Identifier
	 * @return            Optional
	 */
	Optional<Versandinformation> ofId(Identifier identifier);

	/**
	 * Speichert eine neue Versandinformation und gibt sie mit der generierten ID zurück.
	 *
	 * @param  versandinformation
	 *                            Versandinformation
	 * @return                    Versandinformation
	 */
	Versandinformation addVersandinformation(Versandinformation versandinformation);

	/**
	 * Ändert eine vorhandene Versandinformation und gibt die geänderte zurück.
	 *
	 * @param  versandinformation
	 * @return
	 */
	Versandinformation updateVersandinformation(Versandinformation versandinformation);

	/**
	 * Löscht alle Versandinformationen, die den gegebenen Newsletter referenzieren.
	 *
	 * @param  newsletterID
	 *                      Identifier
	 * @return              int die Anzahl der gelöschten Versandinformationen.
	 */
	int removeAll(Identifier newsletterID);

}
