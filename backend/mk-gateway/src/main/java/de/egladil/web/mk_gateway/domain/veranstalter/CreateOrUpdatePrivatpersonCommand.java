// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.semantik.DomainCommand;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * CreateOrUpdatePrivatpersonCommand hat die gleiche Signatur wie der body eines entsprechenden mk-gateway-DomainEvents.
 */
@DomainCommand
public class CreateOrUpdatePrivatpersonCommand {

	@JsonIgnore
	private LocalDateTime occouredOn;

	@JsonProperty
	private String rolle;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String fullName;

	@JsonProperty
	private boolean newsletterEmpfaenger;

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

	public boolean newsletterEmpfaenger() {

		return newsletterEmpfaenger;
	}

	public CreateOrUpdatePrivatpersonCommand withNewsletterEmpfaenger(final boolean newsletterEmpfaenger) {

		this.newsletterEmpfaenger = newsletterEmpfaenger;
		return this;
	}

}
