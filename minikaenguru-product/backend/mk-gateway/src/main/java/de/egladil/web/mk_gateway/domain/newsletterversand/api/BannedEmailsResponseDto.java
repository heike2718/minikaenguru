//=====================================================
// Project: authprovider
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand.api;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
@Schema(name = "BannedEmailsResponseDto", description = "Ergebnis der Abfrage von für Mailversand gebanneten users.")
public class BannedEmailsResponseDto {

	@JsonProperty
	private List<String> bannedEmails;

	@JsonProperty
	private String nonce;



	public List<String> getBannedEmails() {
		return bannedEmails;
	}

	public void setBannedEmails(List<String> bannedEmails) {
		this.bannedEmails = bannedEmails;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
}
