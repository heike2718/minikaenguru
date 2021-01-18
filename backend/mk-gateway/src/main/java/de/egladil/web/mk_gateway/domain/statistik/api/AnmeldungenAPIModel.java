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
	private AnmeldungsitemAPIModel privatanmeldungen;

	@JsonProperty
	private AnmeldungsitemAPIModel schulanmeldungen;

	@JsonProperty
	private List<AnmeldungsitemAPIModel> laender = new ArrayList<>();

	@Override
	public String toString() {

		return "AnmeldungenAPIModel [laender=" + laender + ", privatanmeldungen=" + privatanmeldungen + "]";
	}

	public List<AnmeldungsitemAPIModel> getLaender() {

		return laender;
	}

	public AnmeldungenAPIModel withLaendern(final List<AnmeldungsitemAPIModel> schulen) {

		this.laender = schulen;
		return this;
	}

	public AnmeldungsitemAPIModel getPrivatanmeldungen() {

		return privatanmeldungen;
	}

	public AnmeldungenAPIModel withPrivatanmeldungen(final AnmeldungsitemAPIModel privatanmeldungen) {

		this.privatanmeldungen = privatanmeldungen;
		return this;
	}

	public AnmeldungsitemAPIModel getSchulanmeldungen() {

		return schulanmeldungen;
	}

	public AnmeldungenAPIModel withSchulanmeldungen(final AnmeldungsitemAPIModel schulanmeldungen) {

		this.schulanmeldungen = schulanmeldungen;
		return this;
	}
}
