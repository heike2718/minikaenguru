// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * StatistikKlassenstufeService
 */
public class StatistikKlassenstufeService {

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	/**
	 * Erstellt aus der gegebenen Liste von Lösungszetteln die Gesamtpunktverteilung.
	 *
	 * @param  klassenstufe
	 * @param  alleLoesungszettel
	 * @return
	 */
	public GesamtpunktverteilungKlassenstufe generiereGesamtpunktverteilung(final WettbewerbID wettbewerbID, final Klassenstufe klassenstufe, final List<Loesungszettel> alleLoesungszettel) {

		GesamtpunktverteilungKlassenstufeDaten daten = new VerteilungRechner().berechne(wettbewerbID, klassenstufe,
			alleLoesungszettel);

		GesamtpunktverteilungTexte texte = new GesamtpunktverteilungTexte()
			.withBasis(
				MessageFormat.format(applicationMessages.getString("statistik.gesamtpunktverteilung.grundlage.description"),
					new Object[] { Integer.toString(alleLoesungszettel.size()) }))
			.withBewertung(
				MessageFormat.format(applicationMessages.getString("statistik.gesamtpunktverteilung.bewertung.description"),
					new Object[] { klassenstufe.getStartguthaben(wettbewerbID.jahr()) }))
			.withTitel(MessageFormat.format(applicationMessages.getString("statistik.gesamtpunktverteilung.headline"),
				new Object[] { wettbewerbID.jahr().toString(), klassenstufe.getLabel() }))
			.withTitel(applicationMessages.getString("statistik.gesamtpunktverteilung.sectioneinzel"));

		return new GesamtpunktverteilungKlassenstufe(texte, daten);
	}

}
