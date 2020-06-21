// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.apimodel;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	private List<KindAPIModel> kinder = new ArrayList<>();

}
