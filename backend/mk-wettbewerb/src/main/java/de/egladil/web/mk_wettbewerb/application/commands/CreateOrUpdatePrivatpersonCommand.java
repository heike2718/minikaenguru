// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.application.commands;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.model.personen.Rolle;

/**
 * CreateOrUpdatePrivatpersonCommand hat die gleiche Signatur wie der body eines entsprechenden mk-gateway-DomainEvents.
 */
public class CreateOrUpdatePrivatpersonCommand {

	@JsonIgnore
	private LocalDateTime occouredOn;

	@JsonProperty
	private String rolle;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String fullName;

	public static CreateOrUpdatePrivatpersonCommand create(final String uuid, final String fullName) {

		CreateOrUpdatePrivatpersonCommand result = new CreateOrUpdatePrivatpersonCommand();

		result.rolle = Rolle.PRIVAT.name();
		result.uuid = uuid;
		result.fullName = fullName;
		result.occouredOn = LocalDateTime.now();

		return result;
	}

	public String uuid() {

		return uuid;
	}

	public String fullName() {

		return fullName;
	}

}
