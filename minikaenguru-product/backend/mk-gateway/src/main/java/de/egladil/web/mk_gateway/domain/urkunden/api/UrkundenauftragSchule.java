// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.mk_gateway.domain.urkunden.Farbschema;

/**
 * UrkundenauftragSchule
 */
public class UrkundenauftragSchule {

	@JsonProperty
	@NotBlank
	@Kuerzel
	private String schulkuerzel;

	/** Tagesdatum im Format dd.MM.yyyy, das auf die Urkunde gedruckt werden soll. */
	@JsonProperty
	private String dateString;

	@JsonProperty
	@NotNull
	private Farbschema farbschema;

	public String schulkuerzel() {

		return schulkuerzel;
	}

	public UrkundenauftragSchule withSchulkuerzel(final String schulkuerzel) {

		this.schulkuerzel = schulkuerzel;
		return this;
	}

	@JsonProperty
	private boolean forceCreateUrkundenKaengurusprung;

	public String dateString() {

		return dateString;
	}

	public UrkundenauftragSchule withDateString(final String dateString) {

		this.dateString = dateString;
		return this;
	}

	public Farbschema farbschema() {

		return farbschema;
	}

	public UrkundenauftragSchule withFarbschema(final Farbschema farbschema) {

		this.farbschema = farbschema;
		return this;
	}

}
