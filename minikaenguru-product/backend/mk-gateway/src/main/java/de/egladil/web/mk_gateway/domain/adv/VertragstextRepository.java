// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * VertragstextRepository
 */
public interface VertragstextRepository {

	/**
	 * @param  identifier
	 * @return
	 */
	Optional<Vertragstext> ofIdentifier(Identifier identifier);

	/**
	 * @return
	 */
	List<Vertragstext> loadVertragstexte();

}
