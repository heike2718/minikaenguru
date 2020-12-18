// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.events;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.event.EventType;
import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * UserLoggedIn
 */
public class UserLoggedIn implements MkGatewayDomainEvent {

	@JsonIgnore
	private final LocalDateTime occuredOn;

	@JsonProperty
	private Rolle rolle;

	@JsonProperty
	private String uuid;

	UserLoggedIn() {

		this.occuredOn = CommonTimeUtils.now();

	}

	public UserLoggedIn(final String uuid, final Rolle rolle) {

		this();
		this.uuid = uuid;
		this.rolle = rolle;
	}

	@Override
	public LocalDateTime occuredOn() {

		return this.occuredOn;
	}

	@Override
	public String typeName() {

		return EventType.USER_LOGGED_IN.getLabel();
	}

	public Rolle rolle() {

		return rolle;
	}

	public String uuid() {

		return uuid;
	}

}
