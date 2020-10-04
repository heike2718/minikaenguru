// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * MedianAPIModel
 */
public class MedianAPIModel {

	@JsonProperty
	private String klassenstufe;

	@JsonProperty
	private String median;

	MedianAPIModel() {

	}

	public MedianAPIModel(final Klassenstufe klassenstufe, final String median) {

		this.klassenstufe = klassenstufe.getLabel();
		this.median = median;
	}

}
