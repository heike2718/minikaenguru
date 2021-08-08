// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.admin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.AnonymisierteTeilnahmeAPIModel;

/**
 * SchuleAdminOverview
 */
public class SchuleAdminOverview {

	@JsonProperty
	private String kuerzel;

	@JsonProperty
	private SchuleKatalogData katalogData;

	@JsonProperty
	private SchuleMinikaenguruData minikaenguruData;

	@JsonProperty
	private String nameUrkunde;

	@JsonProperty
	private String angemeldetDurch;

	@JsonProperty
	private int anzahlLoesungszettel;

	@JsonProperty
	private int anzahlKinder;

	@JsonProperty
	private List<AnonymisierteTeilnahmeAPIModel> schulteilnahmen;

	public SchuleKatalogData getKatalogData() {

		return katalogData;
	}

	public SchuleAdminOverview withKatalogData(final SchuleKatalogData katalogData) {

		this.katalogData = katalogData;
		this.kuerzel = katalogData.kuerzel();
		return this;
	}

	public SchuleMinikaenguruData getMinikaenguruData() {

		return minikaenguruData;
	}

	public SchuleAdminOverview withMinikaenguruData(final SchuleMinikaenguruData minikaenguruData) {

		this.minikaenguruData = minikaenguruData;
		return this;
	}

	public List<AnonymisierteTeilnahmeAPIModel> getSchulteilnahmen() {

		return schulteilnahmen;
	}

	public SchuleAdminOverview withSchulteilnahmen(final List<AnonymisierteTeilnahmeAPIModel> schulteilnahmen) {

		this.schulteilnahmen = schulteilnahmen;
		return this;
	}

	public String getNameUrkunde() {

		return nameUrkunde;
	}

	public SchuleAdminOverview withNameUrkunde(final String nameUrkunde) {

		this.nameUrkunde = nameUrkunde;
		return this;
	}

	public String getAngemeldetDurch() {

		return angemeldetDurch;
	}

	public SchuleAdminOverview withAngemeldetDurch(final String angemeldetDurch) {

		this.angemeldetDurch = angemeldetDurch;
		return this;
	}

	public int getAnzahlLoesungszettel() {

		return anzahlLoesungszettel;
	}

	public SchuleAdminOverview withAnzahlLoesungszettel(final int anzahlLoesungszettel) {

		this.anzahlLoesungszettel = anzahlLoesungszettel;
		return this;
	}

	public int getAnzahlKinder() {

		return anzahlKinder;
	}

	public SchuleAdminOverview withAnzahlKinder(final int anzahlKinder) {

		this.anzahlKinder = anzahlKinder;
		return this;
	}
}
