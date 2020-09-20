// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.pdf;

import java.util.Map;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungKlassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * PrivatteilnahmenuebersichtPDFGenerator
 */
public class PrivatteilnahmenuebersichtPDFGenerator {

	/**
	 * Erzeugt das PDF für eine Privatteilnahme.
	 *
	 * @param  wettbewerbID
	 *                                      Die WettbewerbID
	 * @param  verteilungenNachKlassenstufe
	 *                                      Map die Auswertungsdaten und Texte nach Klassenstufe.
	 * @return
	 */
	public DownloadData generierePdf(final WettbewerbID wettbewerbID, final Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> verteilungenNachKlassenstufe) {

		return null;

	}

}
