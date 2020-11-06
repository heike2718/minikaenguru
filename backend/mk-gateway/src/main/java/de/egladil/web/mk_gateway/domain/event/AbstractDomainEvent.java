// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons_net.time.CommonTimeUtils;

/**
 * AbstractDomainEvent
 */
public abstract class AbstractDomainEvent implements MkGatewayDomainEvent {

	@JsonIgnore
	private final LocalDateTime occuredOn;

	protected AbstractDomainEvent() {

		this.occuredOn = CommonTimeUtils.now();
	}

	@Override
	public LocalDateTime occuredOn() {

		return null;
	}

	public String serializeQuietly() {

		try {

			String body = new ObjectMapper().writeValueAsString(this);
			return typeName() + ": " + body;
		} catch (JsonProcessingException e) {

			e.printStackTrace();
			return e.getMessage();
		}
	}

}
