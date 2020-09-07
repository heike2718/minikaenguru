// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.auswertungen;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

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
