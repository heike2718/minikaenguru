// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.semantik.ValueObject;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Klassenstufe;

/**
 * KlassenstufeAPIModel
 */
@ValueObject
public class KlassenstufeAPIModel {

	@JsonProperty
	private Klassenstufe klassenstufe;

	@JsonProperty
	private String label;

	public static KlassenstufeAPIModel create(final Klassenstufe klassenstufe) {

		KlassenstufeAPIModel result = new KlassenstufeAPIModel();

		result.klassenstufe = klassenstufe;
		result.label = klassenstufe.getLabel();

		return result;

	}

	public Klassenstufe klassenstufe() {

		return klassenstufe;
	}

	public String label() {

		return label;
	}

}
