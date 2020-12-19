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
import de.egladil.web.mk_gateway.domain.veranstalter.api.ChangeUserCommand;

/**
 * SynchronizeVeranstalterFailed
 */
public class SynchronizeVeranstalterFailed implements MkGatewayDomainEvent {

	@JsonIgnore
	private final LocalDateTime occuredOn;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String fullName;

	@JsonProperty
	private String email;

	SynchronizeVeranstalterFailed() {

		this.occuredOn = CommonTimeUtils.now();
	}

	public static SynchronizeVeranstalterFailed fromMessagingCommand(final ChangeUserCommand cmd) {

		SynchronizeVeranstalterFailed result = new SynchronizeVeranstalterFailed();
		result.uuid = cmd.uuid();
		result.email = cmd.email();
		result.fullName = cmd.fullName();
		return result;
	}

	@Override
	public LocalDateTime occuredOn() {

		return this.occuredOn;
	}

	@Override
	public String typeName() {

		return EventType.SYNCHRONIZE_VERANSTALTER_FAILED.getLabel();
	}

}
