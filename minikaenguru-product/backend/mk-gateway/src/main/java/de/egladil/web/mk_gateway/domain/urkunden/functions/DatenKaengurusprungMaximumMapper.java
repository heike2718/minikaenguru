// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.functions;

import java.util.List;
import java.util.function.Function;

import de.egladil.web.mk_gateway.domain.urkunden.daten.KinddatenUebersicht;

/**
 * DatenKaengurusprungMaximumMapper
 */
public class DatenKaengurusprungMaximumMapper implements Function<List<KinddatenUebersicht>, Integer> {

	@Override
	public Integer apply(final List<KinddatenUebersicht> daten) {

		if (daten == null) {

			return 0;
		}

		int aktuellesMaximum = 0;

		for (KinddatenUebersicht datenKind : daten) {

			int value = datenKind.laengeKaengurusprung();

			if (value > aktuellesMaximum) {

				aktuellesMaximum = value;
			}
		}

		return aktuellesMaximum;
	}

}
