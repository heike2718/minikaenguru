// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.auswertungen;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DuplikatWarnungModel
 */
public class DuplikatWarnungModel {

	@JsonProperty
	private Duplikatkontext kontext;

	@JsonProperty
	private String warnungstext;

	public DuplikatWarnungModel(final Duplikatkontext kontext, final String warnungstext) {

		this.kontext = kontext;
		this.warnungstext = warnungstext;
	}

	DuplikatWarnungModel() {

		super();

	}

}
