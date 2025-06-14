// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Land;
import jakarta.validation.constraints.NotBlank;

/**
 * LandPayload zum Umbenennen von Land, Ort oder Schule. Alle Umbenennungen ändern ein bis viele Einträge in der
 * denormalsierten SCHULEN-Tabelle.
 */
public class LandPayload {

	@JsonProperty
	@Kuerzel
	@NotBlank
	private String kuerzel;

	@JsonProperty
	@StringLatin
	@NotBlank
	private String name;

	public static LandPayload create(final Land land) {

		LandPayload result = new LandPayload();
		result.kuerzel = land.getKuerzel();
		result.name = land.getName();

		return result;

	}

	public static LandPayload create(final String kuerzel, final String name) {

		LandPayload result = new LandPayload();
		result.kuerzel = kuerzel;
		result.name = name;
		return result;

	}

	public LandPayload() {

		super();

	}

	public String name() {

		return name;
	}

	public String kuerzel() {

		return kuerzel;
	}

}
