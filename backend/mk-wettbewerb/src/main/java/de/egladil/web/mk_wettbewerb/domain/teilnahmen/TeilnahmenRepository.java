// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import java.util.Optional;

import de.egladil.web.mk_wettbewerb.domain.semantik.Repository;

/**
 * TeilnahmenRepository
 */
@Repository
public interface TeilnahmenRepository {

	/**
	 * Gibt die Teilnahme mit dem gegebenen identifier zurück, falls sie existiert.
	 *
	 * @param  identifier
	 *                    String
	 * @return            Optional
	 */
	Optional<Teilnahme> ofIdentifier(String identifier);

}
