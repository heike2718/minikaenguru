// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.signup;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * PrivatmenschCreated
 */
public class PrivatmenschCreated implements MkGatewayDomainEvent {

	private final LocalDateTime occouredOn;

	@JsonProperty
	private final Rolle rolle;

	@JsonProperty
	private final String uuid;

	@JsonProperty
	private final String fullName;

	/**
	 * @param occouredOn
	 * @param uuid
	 * @param fullName
	 */
	public PrivatmenschCreated(final LocalDateTime occouredOn, final String uuid, final String fullName) {

		if (occouredOn == null) {

			throw new IllegalArgumentException("occouredOn darf nicht null sein.");
		}

		if (StringUtils.isBlank(uuid)) {

			throw new IllegalArgumentException("uuid darf nicht blank sein.");
		}

		if (StringUtils.isBlank(fullName)) {

			throw new IllegalArgumentException("fullName darf nicht blank sein.");
		}

		this.occouredOn = occouredOn;
		this.fullName = fullName;
		this.uuid = uuid;
		this.rolle = Rolle.PRIVAT;
	}

	@Override
	public LocalDateTime occuredOn() {

		return this.occouredOn;
	}

	@Override
	public String typeName() {

		return PrivatmenschCreated.class.getSimpleName();
	}

	public String fullName() {

		return this.fullName;
	}

	public String uuid() {

		return this.uuid;
	}

	Rolle rolle() {

		return this.rolle;
	}

}
