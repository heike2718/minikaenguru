// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MailAPIModel
 */
public class MailAPIModel {

	@JsonProperty
	private String betreff;

	@JsonProperty
	private String empfaenger;

	@JsonProperty
	private String mailtext;

	public String getBetreff() {

		return betreff;
	}

	public String getEmpfaenger() {

		return empfaenger;
	}

	public String getMailtext() {

		return mailtext;
	}

}
