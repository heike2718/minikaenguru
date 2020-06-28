// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Sprache;

/**
 * KindAPIModel
 */
public class KindAPIModel {

	@JsonProperty
	private String vorname;

	@JsonProperty
	private String nachname;

	@JsonProperty
	private String zusatz;

	@JsonProperty
	private KlassenstufeAPIModel klassenstufe;

	@JsonProperty
	private SpracheAPIModel sprache;

	public KindAPIModel create(final Klassenstufe klassenstufe, final Sprache sprache) {

		KindAPIModel result = new KindAPIModel();
		result.klassenstufe = KlassenstufeAPIModel.create(klassenstufe);
		result.sprache = SpracheAPIModel.create(sprache);

		return result;

	}

}
