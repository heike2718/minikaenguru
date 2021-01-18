// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import io.reactivex.functions.BiFunction;

/**
 * LoesungszettelLandAggregator
 */
public class LoesungszettelLandAggregator implements BiFunction<List<Loesungszettel>, List<SchuleAPIModel>, Map<String, List<Loesungszettel>>> {

	@Override
	public Map<String, List<Loesungszettel>> apply(final List<Loesungszettel> loesungszettel, final List<SchuleAPIModel> schulen) {

		if (loesungszettel == null) {

			throw new IllegalArgumentException("loesungszettel null");
		}

		if (schulen == null) {

			throw new IllegalArgumentException("schulen null");
		}

		Map<String, List<Loesungszettel>> result = new HashMap<>();

		loesungszettel.stream().filter(z -> Teilnahmeart.SCHULE == z.teilnahmeIdentifier().teilnahmeart()).forEach(z -> {

			Optional<String> optLand = schulen.stream()
				.filter(s -> s.kuerzel().equals(z.getTheTeilnahmenummer().identifier()))
				.map(s -> s.land()).findFirst();

			if (optLand.isPresent()) {

				String land = optLand.get();

				List<Loesungszettel> loesungszettelImLand = result.get(land);

				if (loesungszettelImLand == null) {

					loesungszettelImLand = new ArrayList<>();
				}
				loesungszettelImLand.add(z);
				result.put(land, loesungszettelImLand);
			}
		});

		return result;

	}

}
