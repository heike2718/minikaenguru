//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities.KalenderwochenIntervall;
import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities.WochenstatistikItem;

/**
 *
 */
public class LoesungszettelJeWocheDelegate {

	private static final String PREFIX_NAME = "Woche ";

	/**
	 * Aggregiert die Anzahlen der Lösungszettel je Woche, indem die Partialsummen berechnet werden.
	 *
	 * @param persistenteWochenstatistiken List
	 * @param intervall KalenderwochenIntervall
	 * @return List
	 */
	List<MkBiZaGruppierungsitem> berechneKumulierteWochenstatistik(List<WochenstatistikItem> persistenteWochenstatistiken,
		KalenderwochenIntervall intervall) {

		final List<MkBiZaGruppierungsitem> result = new ArrayList<>();

		if (persistenteWochenstatistiken.isEmpty()) {

			for (int woche = intervall.getMinKw(); woche <= intervall.getMaxKw(); woche++) {
				result.add(new MkBiZaGruppierungsitem().withName(PREFIX_NAME + woche));
			}

			return result;
		}

		Collections.sort(persistenteWochenstatistiken);

		// de Liste enthält ausschließlich Wochen mit mehr als 0 Lösungszetteln. Es könnte Lücken geben, wenn zwischen
		// liste_min_woche und liste_max_woche Wochen ohne Lösungszettel liegen.
		List<MkBiZaGruppierungsitem> normalisierteItems = normalisiereTrefferliste(persistenteWochenstatistiken);

		// jetzt werden die Anzahlen rekursiv von hinten beginnend kumuliert
		List<Integer> kumulierteAnzahlen = kumuliereAnzahlen(normalisierteItems);

		// jetzt müssen vorn und hinten evtl. noch weitere Items eingefügt werden, um intervall zu füllen
		int minWoche = persistenteWochenstatistiken.get(0).getWoche();

		int woche = intervall.getMinKw();
		int anzahl = 0;

		while (woche < minWoche) {
			result.add(new MkBiZaGruppierungsitem().withAnzahl(anzahl).withName(PREFIX_NAME + woche));
			woche++;
		}

		for (Integer kumulierteAnzahl : kumulierteAnzahlen) {
			result.add(new MkBiZaGruppierungsitem().withAnzahl(kumulierteAnzahl).withName(PREFIX_NAME + woche));
			woche++;
		}

		// woche ist jetzt gleich maxWoche
		anzahl = kumulierteAnzahlen.get(kumulierteAnzahlen.size() - 1);
		while (woche <= intervall.getMaxKw()) {
			result.add(new MkBiZaGruppierungsitem().withAnzahl(anzahl).withName(PREFIX_NAME + woche));
			woche++;
		}

		return result;
	}

	List<MkBiZaGruppierungsitem> normalisiereTrefferliste(List<WochenstatistikItem> persistenteWochenstatistiken) {

		if (persistenteWochenstatistiken.isEmpty()) {
			return new ArrayList<>();
		}

		// die Liste ist bereits nach Wochen aufsteigend sortiert. Das erste Element und das letzte Element der Liste
		// hat jeweils eine Anzahl > 0.
		int maxWoche = persistenteWochenstatistiken.get(persistenteWochenstatistiken.size() - 1).getWoche();
		int aktuelleWoche = persistenteWochenstatistiken.get(0).getWoche();
		List<MkBiZaGruppierungsitem> result = new ArrayList<>();

		while (aktuelleWoche <= maxWoche) {
			Optional<WochenstatistikItem> opt = findWithWoche(persistenteWochenstatistiken, aktuelleWoche);
			if (opt.isPresent()) {
				result.add(mapFromDB(opt.get()));
			} else {
				result.add(new MkBiZaGruppierungsitem().withName(PREFIX_NAME + aktuelleWoche));
			}
			aktuelleWoche++;
		}

		return result;

	}

	private Optional<WochenstatistikItem> findWithWoche(final List<WochenstatistikItem> persistenteWochenstatistiken,
		final int aktuelleWoche) {
		return persistenteWochenstatistiken.stream().filter(i -> i.getWoche() == aktuelleWoche).findFirst();
	}

	List<Integer> kumuliereAnzahlen(List<MkBiZaGruppierungsitem> normalisierteItems) {

		if (normalisierteItems.isEmpty()) {
			return new ArrayList<>();
		}

		List<Integer> anzahlen = normalisierteItems.stream().map(item -> Long.valueOf(item.getAnzahl()).intValue()).toList();

		List<Integer> kumulierteAnzahlenReverse = new ArrayList<>();
		int aktuelleAnzahl = anzahlen.stream().collect(Collectors.summingInt(Integer::intValue));
		kumulierteAnzahlenReverse.add(aktuelleAnzahl);

		int index = normalisierteItems.size() - 1;

		// starten mit (size - 1) und ziehe von der gesamtsumme die Anzahl des letzten Elements der Liste ab.
		// dann geht es weiter zum Vorgänger: von der neuen aktuellen Anzahl wird die Anzahl des vorletzten Elements
		// abgezogen.
		// usw. Man muss beim Element mit index 1 stoppen, sonst hat man immer 1 Element mehr, das dann die Anzahl 0
		// hat.
		while (index > 0) {
			Integer anzahl = anzahlen.get(index);
			aktuelleAnzahl -= anzahl;
			kumulierteAnzahlenReverse.add(aktuelleAnzahl);
			index--;

		}

		List<Integer> result = new ArrayList<>();

		for (int i = kumulierteAnzahlenReverse.size() - 1; i >= 0; i--) {
			result.add(kumulierteAnzahlenReverse.get(i));
		}

		return result;

	}

	/**
	 * Wandelt die List von WochenstatistikItem in eine List von MkBiZaGruppierungsitem.
	 *
	 * @param persistenteWochenstatistiken List
	 * @return List
	 */
	List<MkBiZaGruppierungsitem> mapFromDB(List<WochenstatistikItem> persistenteWochenstatistiken) {
		Collections.sort(persistenteWochenstatistiken);

		return persistenteWochenstatistiken.stream().map(item -> mapFromDB(item)).toList();
	}

	private MkBiZaGruppierungsitem mapFromDB(WochenstatistikItem fromDB) {

		return new MkBiZaGruppierungsitem().withAnzahl(fromDB.getAnzahlLoesungszettel()).withName(PREFIX_NAME + fromDB.getWoche());
	}
}
