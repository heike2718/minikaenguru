// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.comparators;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import de.egladil.web.mk_gateway.domain.statistik.AufgabeErgebnisItem;

/**
 * AufgabeErgebnisItemComparator
 */
public class AufgabeErgebnisItemComparator implements Comparator<AufgabeErgebnisItem> {

	@Override
	public int compare(final AufgabeErgebnisItem arg0, final AufgabeErgebnisItem arg1) {

		String erstesZeichen0 = arg0.getNummer().substring(0, 1);
		String erstesZeichen1 = arg1.getNummer().substring(0, 1);

		int wertKategorie = Collator.getInstance(Locale.GERMAN).compare(erstesZeichen0, erstesZeichen1);

		if (wertKategorie != 0) {

			return wertKategorie;
		}

		Integer laufendeNummer0 = Integer.valueOf(arg0.getNummer().substring(2, 3));
		Integer laufendeNummer1 = Integer.valueOf(arg1.getNummer().substring(2, 3));

		return laufendeNummer0.intValue() - laufendeNummer1.intValue();
	}

}
