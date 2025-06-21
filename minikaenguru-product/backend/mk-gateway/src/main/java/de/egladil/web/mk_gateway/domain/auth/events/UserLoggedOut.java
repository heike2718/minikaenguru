// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.events;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.event.AbstractDomainEvent;
import de.egladil.web.mk_gateway.domain.event.EventType;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * UserLoggedOut
 */
public class UserLoggedOut extends AbstractDomainEvent {

	@JsonIgnore
	private final LocalDateTime occuredOn;

	@JsonProperty
	private Rolle rolle;

	@JsonProperty
	private String uuid;

	UserLoggedOut() {

		this.occuredOn = CommonTimeUtils.now();

	}

	public UserLoggedOut(final String uuid, final Rolle rolle) {

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

		return EventType.USER_LOGGED_OUT.getLabel();
	}

	public Rolle rolle() {

		return rolle;
	}

	public String uuid() {

		return uuid;
	}

}
