// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.events;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * PrivatveranstalterCreated
 */
public class PrivatveranstalterCreated implements MkGatewayDomainEvent {

	private final LocalDateTime occouredOn;

	@JsonProperty
	private final Rolle rolle;

	@JsonProperty
	private final String uuid;

	@JsonProperty
	private final String fullName;

	@JsonProperty
	private final String email;

	@JsonProperty
	private final boolean newsletterEmpfaenger;

	/**
	 * @param occouredOn
	 * @param uuid
	 * @param fullName
	 */
	public PrivatveranstalterCreated(final LocalDateTime occouredOn, final String uuid, final String fullName, final String email, final boolean newsletterEmpfaenger) {

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
		this.email = StringUtils.isBlank(email) ? null : email;
		this.uuid = uuid;
		this.rolle = Rolle.PRIVAT;
		this.newsletterEmpfaenger = newsletterEmpfaenger;
	}

	@Override
	public LocalDateTime occuredOn() {

		return this.occouredOn;
	}

	@Override
	public String typeName() {

		return PrivatveranstalterCreated.class.getSimpleName();
	}

	public String fullName() {

		return this.fullName;
	}

	public String uuid() {

		return this.uuid;
	}

	public Rolle rolle() {

		return this.rolle;
	}

	public boolean isNewsletterEmpfaenger() {

		return newsletterEmpfaenger;
	}

	public String email() {

		return email;
	}

}
