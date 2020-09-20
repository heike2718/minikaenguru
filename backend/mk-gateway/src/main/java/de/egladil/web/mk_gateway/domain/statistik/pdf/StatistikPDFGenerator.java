// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.pdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungKlassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * StatistikPDFGenerator
 */
public class StatistikPDFGenerator {

	private final AufgabenuebersichtPDFGenerator aufgabenuebersichtGenerator = new AufgabenuebersichtPDFGenerator();

	private final RohpunkteuebersichtPDFGenerator rohpunkteuebersichtGenerator = new RohpunkteuebersichtPDFGenerator();

	/**
	 * Generiert die Tabellen je Klassenstufe.
	 *
	 * @param  verteilungenNachKlassenstufe
	 * @return
	 */
	public List<byte[]> generiereStatistikUebersicht(final Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> verteilungenNachKlassenstufe) {

		List<byte[]> result = new ArrayList<>();

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			GesamtpunktverteilungKlassenstufe verteilung = verteilungenNachKlassenstufe.get(klassenstufe);

			if (verteilung != null) {

				result.add(aufgabenuebersichtGenerator.generiereAufgabenuebersichtKlassenstufe(verteilung));
				result.add(rohpunkteuebersichtGenerator.generiereRohpunktuebersichtKlassenstufe(verteilung));

			}

		}

		return result;
	}

}
