// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;

/**
 * KuerzelPayload
 */
@Deprecated
public class KuerzelPayload {

	@JsonProperty
	@Kuerzel
	@NotBlank
	private String kuerzel;

	public String kuerzel() {

		return kuerzel;
	}

	public KuerzelPayload withKuerzel(final String kuerzel) {

		this.kuerzel = kuerzel;
		return this;
	}

}
