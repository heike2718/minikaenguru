// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;

/**
 * SchulanmeldungenLandAggregator
 */
public class SchulanmeldungenLandAggregator implements Function<java.util.List<de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel>, Map<String, List<SchuleAPIModel>>> {

	@Override
	public Map<String, List<SchuleAPIModel>> apply(final List<SchuleAPIModel> schulen) {

		if (schulen == null) {

			throw new IllegalArgumentException("schulen null");
		}

		Map<String, List<SchuleAPIModel>> schulenNachBundeslaendern = new HashMap<>();

		schulen.stream().forEach(s -> {

			String land = s.land();
			List<SchuleAPIModel> schulenImLand = schulenNachBundeslaendern.get(land);

			if (schulenImLand == null) {

				schulenImLand = new ArrayList<>();
			}
			schulenImLand.add(s);

			schulenNachBundeslaendern.put(land, schulenImLand);
		});

		return schulenNachBundeslaendern;
	}

}
