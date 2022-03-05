// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mustertexte;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * MustertexteService
 */
public interface MustertexteService {

	/**
	 * Läd die Mustertexte ohne den Text zu der gegebenen Kategorie.
	 *
	 * @param  kategorie
	 * @return           ResponsePayload
	 */
	ResponsePayload getMustertexteByKategorie(final Mustertextkategorie kategorie);

	ResponsePayload loadDetails(Identifier identifier);

}
