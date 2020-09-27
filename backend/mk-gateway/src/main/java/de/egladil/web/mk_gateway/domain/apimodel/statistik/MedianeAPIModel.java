// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.statistik;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * MedianeAPIModel
 */
public class MedianeAPIModel {

	@JsonProperty
	private List<MedianAPIModel> mediane = new ArrayList<>();

	MedianeAPIModel() {

	}

	public MedianeAPIModel(final Map<Klassenstufe, String> medianMap) {

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			String median = medianMap.get(klassenstufe);

			if (median != null) {

				this.mediane.add(new MedianAPIModel(klassenstufe, median));
			}
		}
	}

}
