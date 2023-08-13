// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge.api;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.StringLatin;

/**
 * RenameKatalogItemPayload zum Umbenennen von Land, Ort oder Schule. Alle Umbenennungen ändern ein bis viele Einträge in der
 * denormalsierten SCHULEN-Tabelle.
 */
public class RenameKatalogItemPayload {

	@JsonProperty
	@StringLatin
	@NotBlank
	private String name;

	public static RenameKatalogItemPayload create(final String name) {

		RenameKatalogItemPayload result = new RenameKatalogItemPayload();
		result.name = name;
		return result;

	}

	public RenameKatalogItemPayload() {

		super();

	}

	public String name() {

		return name;
	}

}
