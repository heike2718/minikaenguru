// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * MailempfaengerRepository
 */
public interface MailempfaengerRepository {

	/**
	 * Gibt alle Veranstalter zurück, die den newsletter abonniert haben.
	 *
	 * @return
	 */
	List<Mailempfaenger> findAllNewsletterabonnenten();

	/**
	 * Gibt die Lehrer zurück, die den Newsletter abonniert haben.
	 *
	 * @return List
	 */
	List<Mailempfaenger> findAbonnierendeLehrer();

	/**
	 * Gibt die Privatveranstalter zurück, die den Newsletter abonniert haben.
	 *
	 * @return List
	 */
	List<Mailempfaenger> findAbonnierendePrivatveranstalter();

	/**
	 * @param  identifier
	 *                    Identifier
	 * @return
	 */
	Optional<Mailempfaenger> ofIdentifier(Identifier identifier);

}
