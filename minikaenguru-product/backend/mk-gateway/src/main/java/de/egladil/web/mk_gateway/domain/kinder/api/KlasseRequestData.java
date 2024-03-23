// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.UuidString;

/**
 * KlasseRequestData
 */
public class KlasseRequestData implements Serializable {

	@JsonIgnore
	private static final long serialVersionUID = -2155795017777400135L;

	@JsonIgnore
	public static final String KEINE_UUID = "neu";

	@JsonProperty
	@UuidString
	@NotNull
	private String uuid;

	@JsonProperty
	@Kuerzel
	@NotBlank
	private String schulkuerzel;

	@JsonProperty
	@NotNull
	private KlasseEditorModel klasse;

	public String getUuid() {

		return uuid;
	}

	public KlasseRequestData withUuid(final String uuid) {

		this.uuid = uuid;
		return this;
	}

	public String schulkuerzel() {

		return schulkuerzel;
	}

	public KlasseRequestData withSchulkuerzel(final String schulkuerzel) {

		this.schulkuerzel = schulkuerzel;
		return this;
	}

	public KlasseEditorModel klasse() {

		return klasse;
	}

	public KlasseRequestData withKlasse(final KlasseEditorModel klasse) {

		this.klasse = klasse;
		return this;
	}

}
