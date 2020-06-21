// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.apimodel;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;

/**
 * TeilnahmeAPIModel
 */
public class TeilnahmeAPIModel {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private Teilnahmeart teilnahmeart;

	@JsonProperty
	private List<AuswertungsgruppeAPIModel> auswertungsgruppen;

	public static TeilnahmeAPIModel create(final Privatteilnahme teilnahme) {

		TeilnahmeAPIModel result = new TeilnahmeAPIModel();
		result.jahr = teilnahme.wettbewerbID().jahr().intValue();
		result.teilnahmenummer = teilnahme.teilnahmenummer().identifier();
		result.teilnahmeart = Teilnahmeart.PRIVAT;
		result.auswertungsgruppen = new ArrayList<>();
		result.auswertungsgruppen.add(AuswertungsgruppeAPIModel.createPrivat());

		return result;
	}

	public static TeilnahmeAPIModel create(final Schulteilnahme teilnahme) {

		TeilnahmeAPIModel result = new TeilnahmeAPIModel();
		result.jahr = teilnahme.wettbewerbID().jahr().intValue();
		result.teilnahmenummer = teilnahme.teilnahmenummer().identifier();
		result.teilnahmeart = Teilnahmeart.SCHULE;

		return result;
	}

}
