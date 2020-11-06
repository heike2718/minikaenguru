// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.semantik.DomainEvent;

/**
 * SecurityIncidentRegistered
 */
@DomainEvent
public class SecurityIncidentRegistered extends AbstractDomainEvent {

	@JsonProperty
	private String message;

	SecurityIncidentRegistered() {

		super();

	}

	public SecurityIncidentRegistered(final String message) {

		this();
		this.message = message;

	}

	@Override
	public String typeName() {

		return EventType.SECURITY_INCIDENT_REGISTERED.getLabel();
	}

	public String message() {

		return message;
	}

}
