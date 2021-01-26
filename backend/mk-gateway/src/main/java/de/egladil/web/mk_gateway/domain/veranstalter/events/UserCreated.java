// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.event.AbstractDomainEvent;
import de.egladil.web.mk_gateway.domain.event.EventType;
import de.egladil.web.mk_gateway.domain.veranstalter.api.CreateUserCommand;

/**
 * UserCreated
 */
public class UserCreated extends AbstractDomainEvent {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String fullName;

	@JsonProperty
	private String email;

	@JsonProperty
	private String nonce;

	public static UserCreated mapFromCommand(final CreateUserCommand command) {

		UserCreated result = new UserCreated();
		result.email = command.getEmail();
		result.fullName = command.getFullName();
		result.nonce = command.getNonce();
		result.uuid = command.getUuid();
		return result;
	}

	@Override
	public String typeName() {

		return EventType.USER_CREATED.getLabel();
	}

}
