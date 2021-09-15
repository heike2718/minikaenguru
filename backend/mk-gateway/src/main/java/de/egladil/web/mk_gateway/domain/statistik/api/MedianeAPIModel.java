// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * MedianeAPIModel
 */
public class MedianeAPIModel {

	@JsonProperty
	private List<MedianAPIModel> mediane = new ArrayList<>();

	public void addMedian(final MedianAPIModel median) {

		this.mediane.add(median);
	}

	public int size() {

		return mediane.size();
	}

	public Optional<MedianAPIModel> findMedian(final Klassenstufe klassenstufe) {

		return mediane.stream().filter(m -> klassenstufe == m.getKlassenstufe()).findFirst();
	}

	@JsonIgnore
	public List<MedianAPIModel> getMedianeSortedByKlassenstufe() {

		List<MedianAPIModel> result = new ArrayList<>();

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			Optional<MedianAPIModel> opt = findMedian(klassenstufe);

			if (opt.isPresent()) {

				result.add(opt.get());
			}
		}

		return result;

	}

}
