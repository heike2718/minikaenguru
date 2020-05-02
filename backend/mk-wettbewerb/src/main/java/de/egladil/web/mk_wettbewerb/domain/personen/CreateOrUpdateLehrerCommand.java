// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.semantik.DomainCommand;

/**
 * CreateOrUpdateLehrerCommand hat die gleiche Signatur wie der body eines entsprechenden mk-gateway-DomainEvents und entsteht durch
 * Deserialisierung eines REST-Payloads..
 */
@DomainCommand
public class CreateOrUpdateLehrerCommand {

	@JsonIgnore
	private LocalDateTime occouredOn;

	@JsonProperty
	private String rolle;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String fullName;

	@JsonProperty
	private String schulkuerzel;

	public static CreateOrUpdateLehrerCommand createForTest(final String uuid, final String fullName, final String schulkuerzel) {

		CreateOrUpdateLehrerCommand result = new CreateOrUpdateLehrerCommand();
		result.rolle = Rolle.LEHRER.name();
		result.fullName = fullName;
		result.schulkuerzel = schulkuerzel;
		result.occouredOn = LocalDateTime.now();
		result.uuid = uuid;

		return result;

	}

	public String uuid() {

		return uuid;
	}

	public String fullName() {

		return fullName;
	}

	public String schulkuerzel() {

		return schulkuerzel;
	}

}
