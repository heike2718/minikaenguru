// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.auswertungen;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * SpracheAPIModel
 */
@ValueObject
public class SpracheAPIModel {

	@JsonProperty
	private Sprache sprache;

	@JsonProperty
	private String label;

	public static SpracheAPIModel create(final Sprache sprache) {

		SpracheAPIModel result = new SpracheAPIModel();
		result.sprache = sprache;
		result.label = sprache.getLabel();
		return result;
	}

	public Sprache sprache() {

		return sprache;
	}

	public String label() {

		return label;
	}

}
