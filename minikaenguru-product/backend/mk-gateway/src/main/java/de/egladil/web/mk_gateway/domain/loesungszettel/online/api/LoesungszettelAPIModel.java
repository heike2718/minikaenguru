// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.online.api;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * LoesungszettelAPIModel
 */
public class LoesungszettelAPIModel {

	public static final String KEINE_UUID = "neu";

	@JsonProperty
	private String uuid;

	@JsonProperty
	private int version = -1;

	@JsonProperty
	private String kindID;

	@JsonProperty
	private Klassenstufe klassenstufe;

	@JsonProperty
	private List<LoesungszettelZeileAPIModel> zeilen;

	@Override
	public String toString() {

		return "LoesungszettelAPIModel [uuid=" + uuid + ", kindID=" + kindID + ", klassenstufe=" + klassenstufe + "]";
	}

	public String uuid() {

		return uuid;
	}

	public LoesungszettelAPIModel withUuid(final String uuid) {

		this.uuid = uuid;
		return this;
	}

	public String kindID() {

		return kindID;
	}

	public LoesungszettelAPIModel withKindID(final String kindID) {

		this.kindID = kindID;
		return this;
	}

	public Klassenstufe klassenstufe() {

		return klassenstufe;
	}

	public LoesungszettelAPIModel withKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
		return this;
	}

	public List<LoesungszettelZeileAPIModel> zeilen() {

		return zeilen;
	}

	public LoesungszettelAPIModel withZeilen(final List<LoesungszettelZeileAPIModel> zeilen) {

		this.zeilen = zeilen;
		return this;
	}

	public String antwortcode() {

		Collections.sort(zeilen);

		final StringBuilder sb = new StringBuilder();
		this.zeilen.forEach(z -> sb.append(z.eingabe().toString()));
		return sb.toString();

	}

	public int version() {

		return version;
	}

	public LoesungszettelAPIModel withVersion(final int version) {

		this.version = version;
		return this;
	}

}
