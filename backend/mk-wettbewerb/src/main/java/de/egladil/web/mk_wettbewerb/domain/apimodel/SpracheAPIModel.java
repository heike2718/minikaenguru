// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.semantik.ValueObject;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Sprache;

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
