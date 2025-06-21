// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.egladil.web.mk_gateway.domain.urkunden.daten.KinddatenUebersicht;

/**
 * DatenKaengurusprungKaengurugewinnerMapper sammelt aus einer List von AbstractDatenUrkunde die mit dem weitseten Kängurusprung
 * heraus.
 */
public class DatenKaengurusprungKaengurugewinnerMapper implements Function<List<KinddatenUebersicht>, List<KinddatenUebersicht>> {

	@Override
	public List<KinddatenUebersicht> apply(final List<KinddatenUebersicht> daten) {

		if (daten == null) {

			return new ArrayList<>();
		}

		final Integer maximum = new DatenKaengurusprungMaximumMapper().apply(daten);

		if (maximum.intValue() == 0) {

			return new ArrayList<>();
		}

		List<KinddatenUebersicht> result = daten.stream().filter(d -> maximum.equals(Integer.valueOf(d.laengeKaengurusprung())))
			.collect(Collectors.toList());

		return result;
	}
}
