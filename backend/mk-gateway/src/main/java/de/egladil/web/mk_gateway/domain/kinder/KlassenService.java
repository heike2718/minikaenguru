// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.List;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseRequestData;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * KlassenService
 */
public interface KlassenService {

	/**
	 * Läd die Klassen einer angemeldeten Schule.
	 *
	 * @param  schulkuerzel
	 *                      String Kürzel der Schule
	 * @param  lehrerUuid
	 *                      UUID eines Lehrers der Schule.
	 * @return              List
	 */
	List<KlasseAPIModel> klassenZuSchuleLaden(String schulkuerzel, String lehrerUuid);

	/**
	 * Prüft, ob es sich bei den gegebenen Klassendaten um ein mögliches Duplitakt handeln könnte.
	 *
	 * @param  data
	 * @param  lehrerUuid
	 * @return
	 */
	boolean pruefeDuplikat(KlasseRequestData data, String lehrerUuid);

	KlasseAPIModel klasseAnlegen(KlasseRequestData data, String lehrerUuid);

	/**
	 * Vorhandene Klasse wird umbenannt.
	 *
	 * @param  data
	 * @param  lehrerUuid
	 * @return
	 */
	KlasseAPIModel klasseUmbenennen(KlasseRequestData data, String lehrerUuid);

	/**
	 * Löcht die gegebene Klasse und gibt das API-Model der gelöschten Klasse zurück.
	 *
	 * @param  klasseUuid
	 * @param  lehrerUuid
	 * @return            KlasseAPIModel
	 */
	KlasseAPIModel klasseLoeschen(String klasseUuid, String lehrerUuid);

	WettbewerbID getWettbewerbID();

	/**
	 * Erzeugt eine Menge neuer Klassen. Falls es Dubletten gibt, wird keine neue Klasse angelegt, sondern die Dublette
	 * zurückgeliefert.
	 *
	 * @param  lehrerID
	 *                      Identifier
	 * @param  schuleID
	 *                      Identifier
	 * @param  klassendaten
	 * @return
	 */
	List<Klasse> importiereKlassen(Identifier lehrerID, Identifier schuleID, List<KlasseRequestData> klassendaten);

}
