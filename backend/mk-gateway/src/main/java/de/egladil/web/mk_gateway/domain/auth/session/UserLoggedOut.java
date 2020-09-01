// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.session;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * UserLoggedOut
 */
public class UserLoggedOut implements MkGatewayDomainEvent {

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

		return TYPE_USER_LOGGED_OUT;
	}

	public Rolle rolle() {

		return rolle;
	}

	public String uuid() {

		return uuid;
	}

}
