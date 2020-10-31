// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.auswertungen;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * AuswertungsgruppeAPIModel
 */
public class AuswertungsgruppeAPIModel {

	@JsonProperty
	private String name;

	@JsonProperty
	private KlassenstufeAPIModel klassenstufe;

	@JsonProperty
	private List<KindAPIModel> kinder = new ArrayList<>();

	public static AuswertungsgruppeAPIModel createPrivat() {

		AuswertungsgruppeAPIModel result = new AuswertungsgruppeAPIModel();

		result.name = "";

		return result;

	}

	public static AuswertungsgruppeAPIModel createSchule(final String name, final Klassenstufe klassenstufe) {

		AuswertungsgruppeAPIModel result = new AuswertungsgruppeAPIModel();

		result.name = name;
		result.klassenstufe = KlassenstufeAPIModel.create(klassenstufe);

		return result;

	}

}
