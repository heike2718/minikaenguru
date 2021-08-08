// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * AuswertungCSVToAuswertungimportZeilenMapper
 */
public class AuswertungCSVToAuswertungimportZeilenMapper implements Function<List<String>, List<AuswertungimportZeile>> {

	private final AuswertungimportZeileSensor sensor = new AuswertungimportZeileSensor();

	@Override
	public List<AuswertungimportZeile> apply(final List<String> lines) {

		List<AuswertungimportZeile> result = new ArrayList<>();

		int indexUeberschrift = this.findIndexUeberschrift(lines);

		if (indexUeberschrift < 0) {

			return result;
		}

		result.add(new AuswertungimportZeile().withIndex(0).withRohdaten(lines.get(indexUeberschrift)));

		for (int i = indexUeberschrift + 1; i < lines.size(); i++) {

			String line = lines.get(i);

			if (!sensor.isLeereZeile(line)) {

				result.add(new AuswertungimportZeile().withIndex(i - indexUeberschrift).withRohdaten(line));
			}
		}

		return result;
	}

	private int findIndexUeberschrift(final List<String> lines) {

		for (int index = 0; index < lines.size(); index++) {

			if (sensor.isUeberschrift(lines.get(index))) {

				return index;
			}
		}

		return -1;
	}

}
