// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.apimodel;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;

/**
 * SchulteilnahmeAPIModel
 */
public class SchulteilnahmeAPIModel {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private Teilnahmeart teilnahmeart;

	@JsonProperty
	private List<AuswertungsgruppeAPIModel> auswertungsgruppen;

	public static SchulteilnahmeAPIModel create(final Schulteilnahme teilnahme) {

		SchulteilnahmeAPIModel result = new SchulteilnahmeAPIModel();
		result.jahr = teilnahme.wettbewerbID().jahr().intValue();
		result.teilnahmenummer = teilnahme.teilnahmenummer().identifier();
		result.teilnahmeart = Teilnahmeart.SCHULE;

		return result;
	}

}
