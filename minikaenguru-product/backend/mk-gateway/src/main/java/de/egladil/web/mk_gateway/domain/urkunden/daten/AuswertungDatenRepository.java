// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.daten;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungKlassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.urkunden.functions.DatenKaengurusprungKaengurugewinnerMapper;

/**
 * AuswertungDatenRepository stellt Daten für die Generierung einer Übersichssteite sowie der Urkunden einer Gruppe von Kindern zur
 * Verfügung.
 */
public class AuswertungDatenRepository {

	private final List<AbstractDatenUrkunde> datenTeilnahmeurkunden;

	private final List<AbstractDatenUrkunde> datenKaengurusprungurkunden;

	private final Map<Klassenstufe, List<KinddatenUebersicht>> uebersichtsdaten;

	private final Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> gesamtpunktverteilungen;

	public AuswertungDatenRepository(final List<AbstractDatenUrkunde> datenTeilnahmeurkunden, final List<AbstractDatenUrkunde> datenKaengurusprungurkunden, final Map<Klassenstufe, List<KinddatenUebersicht>> uebersichtsdaten, final Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> gesamtpunktverteilungen) {

		this.datenTeilnahmeurkunden = datenTeilnahmeurkunden;
		Collections.sort(this.datenTeilnahmeurkunden, new KinddatenUrkundenComparator());

		this.datenKaengurusprungurkunden = datenKaengurusprungurkunden;
		this.uebersichtsdaten = uebersichtsdaten;

		this.gesamtpunktverteilungen = gesamtpunktverteilungen;
	}

	/**
	 * Gibt alle Überichtsdaten für die gegebene Klassenstufe absteigend nach Punkten und innerhalb gleicher Punkte sortiert nach
	 * fullName zurück.
	 *
	 * @param  klassenstufe
	 * @return
	 */
	public List<KinddatenUebersicht> uebersichtsdatenSorted(final Klassenstufe klassenstufe) {

		List<KinddatenUebersicht> result = uebersichtsdaten.get(klassenstufe);

		if (result == null) {

			return new ArrayList<>();
		}

		Collections.sort(result, new KinddatenUebersichtComparator());
		return result == null ? new ArrayList<>() : result;
	}

	/**
	 * Gibt alle Daten für die Teilnahmeurkunden sortiert nach den Klassennamen und innerhalb einer Klasse nach fullName zurück.
	 *
	 * @return List
	 */
	public List<AbstractDatenUrkunde> getAllDatenTeilnahmeurkundenSorted() {

		return this.datenTeilnahmeurkunden;
	}

	/**
	 * Gibt die Übersichtsdaten für den weitesten Kängurusprung in der gegebenen Klassenstufe zurück.
	 *
	 * @param  klassenstufe
	 *                      Klassenstufe
	 * @return              List leer, wenn es keine Gewinner gibt.
	 */
	public List<KinddatenUebersicht> getKaengurugewinner(final Klassenstufe klassenstufe) {

		List<KinddatenUebersicht> daten = uebersichtsdaten.get(klassenstufe);

		return new DatenKaengurusprungKaengurugewinnerMapper().apply(daten);
	}

	/**
	 * Gibt die Urkundendaten für die Kängurusprungurkunde des gegebenen Kindes zurück, falls sie existiert.
	 *
	 * @param  kindUUID
	 * @return          Optional
	 */
	public Optional<AbstractDatenUrkunde> findDatenKaengurusprungurkunde(final String kindUUID) {

		return datenKaengurusprungurkunden.stream().filter(d -> kindUUID.equals(d.uuid())).findFirst();

	}

	public Map<Klassenstufe, GesamtpunktverteilungKlassenstufe> getGesamtpunktverteilungen() {

		return gesamtpunktverteilungen;
	}
}
