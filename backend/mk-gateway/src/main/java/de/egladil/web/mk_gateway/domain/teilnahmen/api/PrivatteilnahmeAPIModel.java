// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.api;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.KindAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Privatteilnahme;

/**
 * PrivatteilnahmeAPIModel
 */
public class PrivatteilnahmeAPIModel {

	@JsonProperty
	private TeilnahmeIdentifier identifier;

	@JsonProperty
	private boolean kinderGeladen;

	@JsonProperty
	private int anzahlKinder;

	@JsonProperty
	private List<KindAPIModel> kinder = new ArrayList<>();

	public static PrivatteilnahmeAPIModel createFromPrivatteilnahme(final Privatteilnahme privatteilnahme) {

		PrivatteilnahmeAPIModel result = new PrivatteilnahmeAPIModel();
		result.identifier = TeilnahmeIdentifier.createFromTeilnahme(privatteilnahme);
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
