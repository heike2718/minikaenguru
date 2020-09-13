// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen;

import java.util.List;

import de.egladil.web.mk_gateway.domain.auswertungen.statistik.GesamtpunktverteilungKlassenstufeDaten;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * VerteilungRechner
 */
public interface VerteilungRechner {

	/**
	 * Sammelt die Dinge zusammen.
	 *
	 * @param  wettbewerbId
	 * @param  klassenstufe
	 * @param  alleLoesungszettel
	 * @return
	 */
	GesamtpunktverteilungKlassenstufeDaten berechne(WettbewerbID wettbewerbId, Klassenstufe klassenstufe, List<Loesungszettel> alleLoesungszettel);

	/**
	 * Berechnet den Median über die gegebenen Lösungszettel.
	 *
	 * @param  loesungszettel
	 *                        List
	 * @return                String auf 3 Nachkommastellen gerundet mit Komma (kein Dezimalpunkt).
	 */
	String berechneMedian(List<Loesungszettel> loesungszettel);
}
