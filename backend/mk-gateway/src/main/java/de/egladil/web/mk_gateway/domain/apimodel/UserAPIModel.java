// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UserAPIModel
 */
public class UserAPIModel {

	@JsonProperty
	private String authproviderSecret;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String fullName;

	public String authproviderSecret() {

		return authproviderSecret;
	}

	public String uuid() {

		return uuid;
	}

	public String fullName() {

		return fullName;
	}

}
