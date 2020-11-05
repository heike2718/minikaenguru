// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassen;

import java.util.List;
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

	/**
	 * Sucht alle Klassen einer Schule.
	 *
	 * @param  schuleID
	 * @return          List
	 */
	List<Klasse> findKlassenWithSchule(Identifier schuleID);

	/**
	 * Fügt eine neue PersistenteKlasse hinzu.
	 *
	 * @param  klasse
	 *                Klasse
	 * @return        Klasse die neue
	 */
	Klasse addKlasse(Klasse klasse);

}
