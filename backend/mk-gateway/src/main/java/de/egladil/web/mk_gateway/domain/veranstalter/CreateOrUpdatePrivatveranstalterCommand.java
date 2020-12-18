// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.auth.events.PrivatveranstalterCreated;
import de.egladil.web.mk_gateway.domain.semantik.DomainCommand;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * CreateOrUpdatePrivatveranstalterCommand hat die gleiche Signatur wie der body eines entsprechenden mk-gateway-DomainEvents.
 */
@DomainCommand
public class CreateOrUpdatePrivatveranstalterCommand {

	@JsonIgnore
	private LocalDateTime occouredOn;

	@JsonProperty
	private String rolle;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String fullName;

	@JsonProperty
	private String email;

	@JsonProperty
	private boolean newsletterEmpfaenger;

	public static CreateOrUpdatePrivatveranstalterCommand create(final PrivatveranstalterCreated event) {

		CreateOrUpdatePrivatveranstalterCommand result = new CreateOrUpdatePrivatveranstalterCommand();
		result.fullName = event.fullName();
		result.email = event.email();
		result.newsletterEmpfaenger = event.isNewsletterEmpfaenger();
		result.rolle = Rolle.PRIVAT.toString();
		result.uuid = event.uuid();
		return result;
	}

	public static CreateOrUpdatePrivatveranstalterCommand create(final String uuid, final String fullName) {

		CreateOrUpdatePrivatveranstalterCommand result = new CreateOrUpdatePrivatveranstalterCommand();

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

	public CreateOrUpdatePrivatveranstalterCommand withNewsletterEmpfaenger(final boolean newsletterEmpfaenger) {

		this.newsletterEmpfaenger = newsletterEmpfaenger;
		return this;
	}

	public String email() {

		return email;
	}

}
