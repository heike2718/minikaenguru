// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.Optional;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.semantik.Repository;

/**
 * SchulkollegienRepository
 */
@Repository
public interface SchulkollegienRepository {

	/**
	 * Sucht das gegebene Schulkollegium und gibt es zuück sofern vorhanden.
	 *
	 * @param  identifier
	 * @return
	 */
	Optional<Schulkollegium> ofSchulkuerzel(Identifier identifier);

	/**
	 * @param schulkollegium
	 */
	void addKollegium(Schulkollegium schulkollegium) throws IllegalStateException;

	/**
	 * Tauscht das gesamte Schulkollegium aus.
	 *
	 * @param schulkollegium
	 */
	void replaceKollegen(Schulkollegium schulkollegium);

	/**
	 * Entfernt das Schulkollegium.
	 *
	 * @param kollegium
	 */
	void deleteKollegium(Schulkollegium kollegium);
}
