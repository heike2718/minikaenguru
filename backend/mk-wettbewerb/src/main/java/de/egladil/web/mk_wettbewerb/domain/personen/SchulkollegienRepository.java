// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_wettbewerb.domain.Identifier;

/**
 * SchulkollegienRepository
 */
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
	void addKollegium(Schulkollegium schulkollegium);

	/**
	 * Tauscht das gesamte Schulkollegium aus.
	 *
	 * @param schulkollegium
	 */
	void replaceKollegen(Schulkollegium schulkollegium);

	/**
	 * Tauscht in allen Schulkollegien das Kollegium-Objekt aus.
	 *
	 * @param geaenderteSchulkollegien
	 */
	void replaceKollegien(List<Schulkollegium> geaenderteSchulkollegien);
}
