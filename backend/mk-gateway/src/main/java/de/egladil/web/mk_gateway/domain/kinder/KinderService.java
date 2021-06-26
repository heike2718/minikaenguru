// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.List;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.klassenlisten.impl.KindImportDaten;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * KinderService
 */
public interface KinderService {

	/**
	 * Prüft, ob das gegebene Kind evtl. eine Dublette wäre.
	 *
	 * @param  daten
	 *                          KindRequestData
	 * @param  veranstalterUuid
	 *                          String
	 * @return                  boolean
	 */
	boolean pruefeDublette(KindRequestData daten, String veranstalterUuid);

	String getWarnungstext(KindEditorModel kind);

	/**
	 * Legt ein neues Kind an.
	 *
	 * @param  daten
	 *                          KindRequestData
	 * @param  veranstalterUuid
	 *                          String
	 * @return                  KindAPIModel
	 */
	KindAPIModel kindAnlegen(KindRequestData daten, String veranstalterUuid);

	KindAPIModel kindAendern(KindRequestData daten, String veranstalterUuid);

	/**
	 * Löscht das gegebene Kind.
	 *
	 * @param  uuid
	 *                          String UUID des Kindes
	 * @param  veranstalterUuid
	 *                          UUID des Veranstalters
	 * @return                  boolean true, falls gelöscht, false sonst.
	 */
	KindAPIModel kindLoeschen(String uuid, String veranstalterUuid);

	List<KindAPIModel> kinderZuTeilnahmeLaden(String teilnahmenummer, String veranstalterUuid);

	WettbewerbID getWettbewerbID();

	/**
	 * Erzeugt Kinder aus einem Import.
	 *
	 * @param  veranstalterID
	 * @param  schulkuerzel
	 * @param  importDaten
	 * @param  vorhandeneKinder
	 *                          List die Kinder, die vor dem Start des Imports bereits in der DB vorhanden waren.
	 * @return                  List
	 */
	List<Kind> importiereKinder(Identifier veranstalterID, String schulkuerzel, List<KindImportDaten> importDaten, List<Kind> vorhandeneKinder);

	/**
	 * Gibt alle Kinder zu einer gegebenen Schulteilnahme am aktuellen Wettbewerb zurück.
	 *
	 * @param  schulkuerzel
	 *                      String
	 * @return              List
	 */
	List<Kind> findWithSchulteilname(String schulkuerzel);

}
