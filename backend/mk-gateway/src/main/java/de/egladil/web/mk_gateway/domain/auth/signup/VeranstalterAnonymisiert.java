// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.signup;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;

/**
 * VeranstalterAnonymisiert
 */
public class VeranstalterAnonymisiert implements MkGatewayDomainEvent {

	@JsonIgnore
	private final LocalDateTime occouredOn;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String rollen;

	public VeranstalterAnonymisiert() {

		this.occouredOn = LocalDateTime.now();

	}

	@Override
	public LocalDateTime occuredOn() {

		return null;
	}

	@Override
	public String typeName() {

		return MkGatewayDomainEvent.TYPE_VERANSTALTER_ANONYMISIERT;
	}

	public String uuid() {

		return uuid;
	}

	public String rollen() {

		return rollen;
	}

}
