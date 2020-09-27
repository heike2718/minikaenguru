// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import de.egladil.web.mk_gateway.domain.statistik.RohpunktItem;

/**
 * RohpunktitemsKumulierteHaeufigkeitMapper
 */
public class RohpunktitemsKumulierteHaeufigkeitMapper implements BiFunction<Integer, List<RohpunktItem>, Integer> {

	@Override
	public Integer apply(final Integer referenzpunkte, final List<RohpunktItem> items) {

		List<RohpunktItem> itemsKleinerOderGleich = items.stream().filter(item -> referenzpunkte >= item.getPunkte())
			.collect(Collectors.toList());

		IntSummaryStatistics stats = itemsKleinerOderGleich.stream()
			.collect(Collectors.summarizingInt(RohpunktItem::getAnzahl));

		return Long.valueOf(stats.getSum()).intValue();
	}

}
