// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;

/**
 * SchuleKatalogData
 */
public class SchuleKatalogData {

	@JsonProperty
	private String kuerzel;

	@JsonProperty
	private String name;

	@JsonProperty
	private String ort;

	@JsonProperty
	private String land;

	@JsonProperty
	private String kuerzelLand;

	public static SchuleKatalogData fromSchuleAPIModel(final SchuleAPIModel schuleAPIModel) {

		SchuleKatalogData result = new SchuleKatalogData();
		result.kuerzel = schuleAPIModel.kuerzel();
		result.kuerzelLand = schuleAPIModel.kuerzelLand();
		result.name = schuleAPIModel.name();
		result.ort = schuleAPIModel.ort();
		result.land = schuleAPIModel.land();

		return result;

	}

	public String kuerzel() {

		return kuerzel;
	}

	public SchuleKatalogData withKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
		return this;
	}

	public String name() {

		return name;
	}

	public SchuleKatalogData withName(final String name) {

		this.name = name;
		return this;
	}

	public String ort() {

		return ort;
	}

	public SchuleKatalogData withOrt(final String ort) {

		this.ort = ort;
		return this;
	}

	public String land() {

		return land;
	}

	public SchuleKatalogData withLand(final String land) {

		this.land = land;
		return this;
	}

	public String kuerzelLand() {

		return kuerzelLand;
	}

	public SchuleKatalogData withKuerzelLand(final String kuerzelLand) {

		this.kuerzelLand = kuerzelLand;
		return this;
	}

}
