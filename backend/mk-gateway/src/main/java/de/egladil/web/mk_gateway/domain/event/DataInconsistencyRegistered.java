// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.semantik.DomainEvent;

/**
 * DataInconsistencyRegistered
 */
@DomainEvent
public class DataInconsistencyRegistered extends AbstractDomainEvent {

	@JsonProperty
	private String message;

	DataInconsistencyRegistered() {

		super();

	}

	public DataInconsistencyRegistered(final String message) {

		this();
		this.message = message;

	}

	@Override
	public String typeName() {

		return EventType.DATA_INCONSISTENCY_REGISTERED.getLabel();
	}

	public String message() {

		return message;
	}

}
