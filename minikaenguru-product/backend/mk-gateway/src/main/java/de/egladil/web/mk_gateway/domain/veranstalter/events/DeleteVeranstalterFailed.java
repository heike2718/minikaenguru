// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.events;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.event.EventType;
import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;

/**
 * DeleteVeranstalterFailed
 */
public class DeleteVeranstalterFailed implements MkGatewayDomainEvent {

	@JsonIgnore
	private final LocalDateTime occuredOn;

	@JsonProperty
	private String uuid;

	DeleteVeranstalterFailed() {

		this.occuredOn = CommonTimeUtils.now();
	}

	public DeleteVeranstalterFailed(final String uuid) {

		this();
		this.uuid = uuid;
	}

	@Override
	public LocalDateTime occuredOn() {

		return this.occuredOn;
	}

	@Override
	public String typeName() {

		return EventType.DELETE_VERANSTALTER_FAILED.getLabel();
	}

	public String uuid() {

		return uuid;
	}

}
