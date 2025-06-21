// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungItem;
import de.egladil.web.mk_gateway.domain.statistik.Punktintervall;

/**
 * PunktintervallKumulierteHaufigkeitMapper
 */
public class PunktintervallKumulierteHaufigkeitMapper implements BiFunction<Punktintervall, List<GesamtpunktverteilungItem>, Integer> {

	@Override
	public Integer apply(final Punktintervall referenzintervall, final List<GesamtpunktverteilungItem> items) {

		List<GesamtpunktverteilungItem> itemsKleinerOderGleich = items.stream()
			.filter(item -> item.getPunktintervall().compareTo(referenzintervall) >= 0).collect(Collectors.toList());

		IntSummaryStatistics stats = itemsKleinerOderGleich.stream()
			.collect(Collectors.summarizingInt(GesamtpunktverteilungItem::getAnzahl));

		return Long.valueOf(stats.getSum()).intValue();
	}

}
