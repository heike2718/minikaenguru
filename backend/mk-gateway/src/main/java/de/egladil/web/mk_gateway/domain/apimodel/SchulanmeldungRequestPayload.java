// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.DeutscherName;
import de.egladil.web.commons_validation.annotations.Kuerzel;

/**
 * SchulanmeldungRequestPayload
 */
public class SchulanmeldungRequestPayload {

	@JsonProperty
	@Kuerzel
	private String schulkuerzel;

	@JsonProperty
	@DeutscherName
	private String schulname;

	public String getSchulkuerzel() {

		return schulkuerzel;
	}

	public void setSchulkuerzel(final String schulkuerzel) {

		this.schulkuerzel = schulkuerzel;
	}

	public String getSchulname() {

		return schulname;
	}

	public void setSchulname(final String schulname) {

		this.schulname = schulname;
	}
}
