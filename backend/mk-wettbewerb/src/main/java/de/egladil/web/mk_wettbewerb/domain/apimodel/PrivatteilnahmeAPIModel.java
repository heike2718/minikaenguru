// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.apimodel;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;

/**
 * PrivatteilnahmeAPIModel
 */
public class PrivatteilnahmeAPIModel {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private Teilnahmeart teilnahmeart;

	@JsonProperty
	private boolean kinderGeladen;

	@JsonProperty
	private int anzahlKinder;

	@JsonProperty
	private List<KindAPIModel> kinder = new ArrayList<>();

	public static PrivatteilnahmeAPIModel createFromPrivatteilnahme(final Privatteilnahme privatteilnahme) {

		PrivatteilnahmeAPIModel result = new PrivatteilnahmeAPIModel();
		result.jahr = privatteilnahme.wettbewerbID().jahr();
		result.teilnahmeart = privatteilnahme.teilnahmeart();
		result.teilnahmenummer = privatteilnahme.teilnahmenummer().identifier();
		result.kinderGeladen = false;

		return result;

	}

	PrivatteilnahmeAPIModel() {

	}

	public PrivatteilnahmeAPIModel withKindern(final List<KindAPIModel> kinder) {

		this.kinder = kinder;
		return this;
	}

}
