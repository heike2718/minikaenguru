// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.daten;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * KinddatenUebersichtComparator: sortiert KinddatenUebersicht nach Anzahl der Punkte absteigend. Bei gleicher
 * Punktzahl werden die KinderDatenTeilnahmeurkundenMapper alphabetisch nach fullName sortiert.<br>
 * <br>
 * <strong>Hinweis:</strong> Es wird implizit vorausgesetzt, dass die Kinder zur gleichen Klassenstufe gehören.
 */
public class KinddatenUebersichtComparator implements Comparator<KinddatenUebersicht> {

	private final Collator collator = Collator.getInstance(Locale.GERMAN);

	@Override
	public int compare(final KinddatenUebersicht kind1, final KinddatenUebersicht kind2) {

		int difference = kind2.punkte() - kind1.punkte();

		if (difference != 0) {

			return difference;
		}

		return collator.compare(kind1.fullName(), kind2.fullName());
	}

}
