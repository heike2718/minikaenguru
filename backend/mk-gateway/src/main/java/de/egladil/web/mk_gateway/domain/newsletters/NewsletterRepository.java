// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletters;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * NewsletterRepository
 */
public interface NewsletterRepository {

	/**
	 * @return List
	 */
	List<Newsletter> loadAll();

	/**
	 * @param  id
	 * @return    Optional
	 */
	Optional<Newsletter> ofId(Identifier id);

	/**
	 * Fügt einen Newsletter hinzu und gibt die ID zurück.
	 *
	 * @param  newsletter
	 * @return            Identifier
	 */
	Newsletter addNewsletter(Newsletter newsletter);

	/**
	 * Aktualisiert den gegebenen Newsletter
	 *
	 * @param newsletter
	 */
	Newsletter updateNewsletter(Newsletter newsletter);

	/**
	 * Löscht denNewsletter mit der gegebenen ID.
	 *
	 * @param identifier
	 *                   Identifier
	 */
	void removeNewsletter(Identifier identifier);
}
