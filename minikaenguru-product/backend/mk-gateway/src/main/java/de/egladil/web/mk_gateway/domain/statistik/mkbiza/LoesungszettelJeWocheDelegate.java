//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
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
	 * @param anzahlWettbewerbswochen int
	 * @return List
	 */
	List<MkBiZaGruppierungsitem> berechneKumulierteWochenstatistik(List<WochenstatistikItem> persistenteWochenstatistiken,
		int anzahlWettbewerbswochen) {

		Collections.sort(persistenteWochenstatistiken);

		List<Integer> anzahlen = getAnzahlenAsList(persistenteWochenstatistiken, anzahlWettbewerbswochen);

		List<MkBiZaGruppierungsitem> listeReverse = new ArrayList<>();

		long woche = 1L;
		long aktuelleAnzahl = anzahlen.stream().collect(Collectors.summingInt(Integer::intValue));

		while (woche <= anzahlWettbewerbswochen) {
			long wochennummer = anzahlWettbewerbswochen + 1 - woche;
			MkBiZaGruppierungsitem aktuelles = new MkBiZaGruppierungsitem().withName(PREFIX_NAME + wochennummer)
				.withAnzahl(aktuelleAnzahl);
			int index = anzahlen.size() - Long.valueOf(woche).intValue();
			aktuelleAnzahl = aktuelleAnzahl - anzahlen.get(index);
			listeReverse.add(aktuelles);
			woche++;
		}

		final List<MkBiZaGruppierungsitem> result = new ArrayList<>();

		for (int i = anzahlen.size() - 1; i >= 0; i--) {
			result.add(listeReverse.get(i));
		}

		return result;
	}

	List<Integer> getAnzahlenAsList(List<WochenstatistikItem> persistenteWochenstatistiken, int anzahlWettbewerbswochen) {

		if (persistenteWochenstatistiken.size() > anzahlWettbewerbswochen) {
			String message = "Anzahl WochenstatistikItem fuer Wettbewerb " + persistenteWochenstatistiken.get(0).getWettbewerbUUID()
				+ " ist größer als " + anzahlWettbewerbswochen;
			throw new MkGatewayRuntimeException(message);
		}

		List<Integer> list = new ArrayList<>(
			persistenteWochenstatistiken.stream().map(item -> Long.valueOf(item.getAnzahlLoesungszettel()).intValue()).toList());

		int size = list.size();

		while (size < anzahlWettbewerbswochen) {
			list.add(0);
			size++;
		}

		return list;
	}
}
