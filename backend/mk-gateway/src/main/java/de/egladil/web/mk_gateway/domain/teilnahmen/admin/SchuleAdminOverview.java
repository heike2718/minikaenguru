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
	private SchuleKatalogData katalogData;

	@JsonProperty
	private SchuleMinikaenguruData minikaenguruData;

	@JsonProperty
	private AktuelleSchulteilnahmeData aktuelleTeilnahme;

	@JsonProperty
	private List<AnonymisierteTeilnahmeAPIModel> schulteilnahmen;

	public SchuleKatalogData getKatalogData() {

		return katalogData;
	}

	public SchuleAdminOverview withKatalogData(final SchuleKatalogData katalogData) {

		this.katalogData = katalogData;
		return this;
	}

	public SchuleMinikaenguruData getMinikaenguruData() {

		return minikaenguruData;
	}

	public SchuleAdminOverview withMinikaenguruData(final SchuleMinikaenguruData minikaenguruData) {

		this.minikaenguruData = minikaenguruData;
		return this;
	}

	public AktuelleSchulteilnahmeData getAktuelleTeilnahme() {

		return aktuelleTeilnahme;
	}

	public SchuleAdminOverview withAktuelleTeilnahme(final AktuelleSchulteilnahmeData aktuelleTeilnahme) {

		this.aktuelleTeilnahme = aktuelleTeilnahme;
		return this;
	}

	public List<AnonymisierteTeilnahmeAPIModel> getSchulteilnahmen() {

		return schulteilnahmen;
	}

	public SchuleAdminOverview withSchulteilnahmen(final List<AnonymisierteTeilnahmeAPIModel> schulteilnahmen) {

		this.schulteilnahmen = schulteilnahmen;
		return this;
	}

}
