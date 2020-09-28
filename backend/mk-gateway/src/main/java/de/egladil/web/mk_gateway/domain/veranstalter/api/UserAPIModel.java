// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UserAPIModel
 */
public class UserAPIModel {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String fullName;

	public String uuid() {

		return uuid;
	}

	public String fullName() {

		return fullName;
	}

}
