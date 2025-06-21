// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.daten;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * KinddatenUebersichtKlassenstufeMapper
 */
public class KinddatenUebersichtKlassenstufeMapper implements Function<List<KinddatenUebersicht>, Map<Klassenstufe, List<KinddatenUebersicht>>> {

	@Override
	public Map<Klassenstufe, List<KinddatenUebersicht>> apply(final List<KinddatenUebersicht> kinder) {

		Map<Klassenstufe, List<KinddatenUebersicht>> result = new HashMap<>();

		for (KinddatenUebersicht kind : kinder) {

			Klassenstufe klassenstufe = kind.klassenstufe();
			List<KinddatenUebersicht> listForKlassenstufe = result.get(klassenstufe);

			if (listForKlassenstufe == null) {

				listForKlassenstufe = new ArrayList<>();
			}

			listForKlassenstufe.add(kind);
			result.put(klassenstufe, listForKlassenstufe);
		}

		return result;
	}

}
