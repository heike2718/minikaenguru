// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mustertexte;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * MustertexteRepository
 */
public interface MustertexteRepository {

	/**
	 * Läd die Mustertexte der gegebenen Kategorie
	 *
	 * @param  kategorie
	 * @return           List
	 */
	List<Mustertext> loadMustertexteByKategorie(Mustertextkategorie kategorie);

	/**
	 * @param  identifier
	 * @return            Optional
	 */
	Optional<Mustertext> findMustertextByIdentifier(Identifier identifier);

	/**
	 * Fügt einen Mustertext hinzu oder aktualsiert einen vorgandenen.
	 *
	 * @param  mustertext
	 * @return
	 */
	Mustertext addOrUpdate(Mustertext mustertext);

	/**
	 * @param identifier
	 *                   Identifier
	 */
	boolean deleteMustertext(Identifier identifier);
}
