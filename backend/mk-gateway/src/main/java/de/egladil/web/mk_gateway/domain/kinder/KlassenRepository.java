// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * KlassenRepository
 */
public interface KlassenRepository {

	/**
	 * @param  klasseID
	 * @return
	 */
	Optional<Klasse> ofIdentifier(Identifier klasseID);

}
