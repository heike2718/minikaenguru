// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.api;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AnmeldungenAPIModel
 */
public class AnmeldungenAPIModel {

	@JsonProperty
	private List<AnmeldungsitemAPIModel> schulen = new ArrayList<>();

	@JsonProperty
	private AnmeldungsitemAPIModel privatanmeldungen;

	public static AnmeldungenAPIModel createEmptyObject() {

		return new AnmeldungenAPIModel()
			.withPrivatanmeldungen(AnmeldungsitemAPIModel.createEmptyPrivatanmeldungenAPIModel())
			.withSchulen(new ArrayList<>());
	}

	@Override
	public String toString() {

		return "AnmeldungenAPIModel [schulen=" + schulen + ", privatanmeldungen=" + privatanmeldungen + "]";
	}

	public List<AnmeldungsitemAPIModel> getSchulen() {

		return schulen;
	}

	public AnmeldungenAPIModel withSchulen(final List<AnmeldungsitemAPIModel> schulen) {

		this.schulen = schulen;
		return this;
	}

	public AnmeldungsitemAPIModel getPrivatanmeldungen() {

		return privatanmeldungen;
	}

	public AnmeldungenAPIModel withPrivatanmeldungen(final AnmeldungsitemAPIModel privatanmeldungen) {

		this.privatanmeldungen = privatanmeldungen;
		return this;
	}

}
