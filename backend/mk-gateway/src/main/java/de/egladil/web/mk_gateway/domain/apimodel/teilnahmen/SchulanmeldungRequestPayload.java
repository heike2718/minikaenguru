// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.teilnahmen;

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

	public static SchulanmeldungRequestPayload create(final String schulkuerzel, final String schulname) {

		SchulanmeldungRequestPayload result = new SchulanmeldungRequestPayload();
		result.schulkuerzel = schulkuerzel;
		result.schulname = schulname;
		return result;

	}

	SchulanmeldungRequestPayload() {

	}

	public String schulkuerzel() {

		return schulkuerzel;
	}

	public String schulname() {

		return schulname;
	}

	@Override
	public String toString() {

		return "SchulanmeldungRequestPayload [schulkuerzel=" + schulkuerzel + ", schulname=" + schulname + "]";
	}
}
