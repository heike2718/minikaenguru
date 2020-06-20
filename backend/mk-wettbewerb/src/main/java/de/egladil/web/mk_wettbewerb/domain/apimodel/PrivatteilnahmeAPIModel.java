// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Privatteilnahme;

/**
 * PrivatteilnahmeAPIModel
 */
public class PrivatteilnahmeAPIModel {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private String teilnahmenummer;

	public static PrivatteilnahmeAPIModel create(final Privatteilnahme teilnahme) {

		PrivatteilnahmeAPIModel result = new PrivatteilnahmeAPIModel();
		result.jahr = teilnahme.wettbewerbID().jahr().intValue();
		result.teilnahmenummer = teilnahme.teilnahmenummer().identifier();

		return result;
	}

}
