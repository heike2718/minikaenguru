// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.List;

import javax.ws.rs.NotFoundException;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.statistik.functions.ProzentRechner;
import de.egladil.web.mk_gateway.domain.statistik.functions.PunktintervallKumulierteHaufigkeitMapper;
import de.egladil.web.mk_gateway.domain.statistik.functions.RohpunktitemsKumulierteHaeufigkeitMapper;
import de.egladil.web.mk_gateway.domain.statistik.functions.RundenCommand;
import de.egladil.web.mk_gateway.domain.statistik.impl.UbersichtRohpunktItemsRechner;
import de.egladil.web.mk_gateway.domain.statistik.impl.UebersichtAufgabenErgebnisseRechner;
import de.egladil.web.mk_gateway.domain.statistik.impl.UebersichtGesamtpunktverteilungItemsRechner;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * VerteilungRechner
 */
public class VerteilungRechner {

	private static final int ANZAHL_NACHKOMMASTELLEN = 2;

	private final UebersichtGesamtpunktverteilungItemsRechner gesamtpunktverteilungRechner = new UebersichtGesamtpunktverteilungItemsRechner();

	private final UebersichtAufgabenErgebnisseRechner aufgabenErgebnisseRechner = new UebersichtAufgabenErgebnisseRechner();

	private final UbersichtRohpunktItemsRechner rohpunktItemsRechner = new UbersichtRohpunktItemsRechner();

	public GesamtpunktverteilungKlassenstufeDaten berechne(final WettbewerbID wettbewerbId, final Klassenstufe klassenstufe, final List<Loesungszettel> alleLoesungszettel) {

		validateParameters(wettbewerbId, klassenstufe, alleLoesungszettel);

		String median = new MedianRechner().berechneMedian(alleLoesungszettel);

		GesamtpunktverteilungKlassenstufeDaten result = new GesamtpunktverteilungKlassenstufeDaten()
			.withKlassenstufe(klassenstufe)
			.withAnzahlTeilnehmer(alleLoesungszettel.size())
			.withMedian(median)
			.withGesamtpunktverteilungItems(
				gesamtpunktverteilungRechner.berechneGesamtpunktverteilungItems(wettbewerbId, klassenstufe, alleLoesungszettel))
			.withAufgabeErgebnisItems(
				aufgabenErgebnisseRechner.berechneAufgabeErgebnisItems(wettbewerbId, klassenstufe, alleLoesungszettel))
			.withRohpunktItems(rohpunktItemsRechner.berechneRohpunktItems(alleLoesungszettel));

		this.addProzentraengeToGesamtpunktverteilungItems(result.gesamtpunktverteilungItems(), alleLoesungszettel.size());
		this.addProzentraengeToRohpunktItems(result.rohpunktItems(), alleLoesungszettel.size());

		return result;
	}

	/**
	 * Für Tests public.
	 *
	 * @param items
	 * @param anzahlTeilnehmer
	 */
	public void addProzentraengeToGesamtpunktverteilungItems(final List<GesamtpunktverteilungItem> items, final int anzahlTeilnehmer) {

		final ProzentRechner prozentRechner = new ProzentRechner();
		final RundenCommand runder = new RundenCommand();
		final PunktintervallKumulierteHaufigkeitMapper punktintervallKumulierteHaufigkeitMapper = new PunktintervallKumulierteHaufigkeitMapper();

		for (GesamtpunktverteilungItem item : items) {

			Integer anzahlGleichSchlechter = punktintervallKumulierteHaufigkeitMapper.apply(item.getPunktintervall(), items);

			Double prozentrang = runder.apply(prozentRechner.apply(anzahlGleichSchlechter, anzahlTeilnehmer),
				ANZAHL_NACHKOMMASTELLEN);

			item.withProzentrang(prozentrang);
		}
	}

	/**
	 * Für Tests public.
	 *
	 * @param items
	 * @param anzahlTeilnehmer
	 */
	void addProzentraengeToRohpunktItems(final List<RohpunktItem> items, final int anzahlTeilnehmer) {

		final ProzentRechner prozentRechner = new ProzentRechner();
		final RundenCommand runder = new RundenCommand();
		final RohpunktitemsKumulierteHaeufigkeitMapper rohpunktitemsKumulierteHaeufigkeitMapper = new RohpunktitemsKumulierteHaeufigkeitMapper();

		for (RohpunktItem item : items) {

			int anzahlGleichSchlechter = rohpunktitemsKumulierteHaeufigkeitMapper.apply(item.getPunkte(), items);

			Double prozentrang = runder.apply(prozentRechner.apply(anzahlGleichSchlechter, items.size()), ANZAHL_NACHKOMMASTELLEN);

			item.withProzentrang(prozentrang);
		}
	}

	void validateParameters(final WettbewerbID wettbewerbId, final Klassenstufe klassenstufe, final List<Loesungszettel> alleLoesungszettel) {

		if (wettbewerbId == null) {

			throw new IllegalArgumentException("wettbewerbId darf nicht null sein");
		}

		if (klassenstufe == null) {

			throw new IllegalArgumentException("klassenstufe darf nicht null sein");
		}

		if (alleLoesungszettel == null) {

			throw new IllegalArgumentException("alleLoeungszettel darf nicht null sein");
		}

		int jahr = wettbewerbId.jahr();

		switch (klassenstufe) {

		case IKID:
			if (jahr < 2019) {

				throw new NotFoundException("Im Jahr " + jahr + " gibt es keine Auswertung für " + klassenstufe.getLabel());
			}
			break;

		case EINS:
			if (jahr < 2014) {

				throw new NotFoundException("Im Jahr " + jahr + " gibt es keine Auswertung für " + klassenstufe.getLabel());
			}
			break;

		case ZWEI:
			if (jahr < 2010) {

				throw new NotFoundException("Im Jahr " + jahr + " gibt es keine Auswertung für " + klassenstufe.getLabel());
			}
			break;

		default:
			break;
		}

	}

}
