// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.daten;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * KinddatenUrkundenComparator sortiert die Kinddaten nach alphabetisch nach Klassenname und innerhalb der gleichen Klasse
 * alphabetisch nach fullName.
 */
public class KinddatenUrkundenComparator implements Comparator<AbstractDatenUrkunde> {

	private final Collator collator = Collator.getInstance(Locale.GERMAN);

	@Override
	public int compare(final AbstractDatenUrkunde kind1, final AbstractDatenUrkunde kind2) {

		int klassennameResult = collator.compare(kind1.nameKlasse(), kind2.nameKlasse());

		if (klassennameResult != 0) {

			return klassennameResult;
		}

		return collator.compare(kind1.fullName(), kind2.fullName());
	}

}
