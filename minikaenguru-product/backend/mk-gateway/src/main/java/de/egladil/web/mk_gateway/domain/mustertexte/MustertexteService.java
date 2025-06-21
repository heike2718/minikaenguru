// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mustertexte;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mustertexte.api.MustertextAPIModel;

/**
 * MustertexteService
 */
public interface MustertexteService {

	/**
	 * Läd alle Mustertexte
	 *
	 * @return ResponsePayload mit einer Liste von MustertextAPIModel-Instanzen ohne text.
	 */
	ResponsePayload loadMustertexte();

	/**
	 * Läd die Details ds gegebenen Mustertextes.
	 *
	 * @param  identifier
	 * @return            ResponsePayload mit einem MustertextAPIModel
	 */
	ResponsePayload loadDetails(Identifier identifier);

	/**
	 * Falls der Musterxt neu ist (uuid 'keine-uuid')' und es noch keinen Mustertext mit der Kategorie und dem Namen gibt, dann wird
	 * einer angelegt. Wenn es einen mit dem Namen und der Kategrorie gibt, wird ein Error-Response zurückgegeben. Die Mustertexte
	 * sollten dann neu geladen werden.
	 *
	 * @param  apiModel
	 *                   MustertextAPIModel die Daten, die zu speichern sind.
	 * @param  uuidAdmin
	 *                   String fürs EventLog.
	 * @return           ResponsePayload mit dem MustertextAPIModel, das nach dem Speichern in der Datenbank geladen würde.
	 */
	ResponsePayload mustertextSpeichern(MustertextAPIModel apiModel, String uuidAdmin);

	/**
	 * Der Mustertext mit dem gegebenen Identifier wird gelöscht. Es kommt ein datenfreies ResponsePayload zurück.
	 *
	 * @param  identifier
	 *                    Identifier
	 * @param  uuidAdmin
	 *                    String fürs EventLog.
	 * @return            ResponsePayload
	 */
	ResponsePayload mustertextLoeschen(Identifier identifier, String uuidAdmin);

}
