// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.signup;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.model.DomainEvent;
import de.egladil.web.mk_gateway.domain.model.Rolle;

/**
 * LehrerCreated
 */
public class LehrerCreated implements DomainEvent {

	@JsonIgnore
	private final LocalDateTime occouredOn;

	@JsonProperty
	private final Rolle rolle;

	@JsonProperty
	private final String uuid;

	@JsonProperty
	private final String fullName;

	@JsonProperty
	private final String schulkuerzel;

	/**
	 * @param occouredOn
	 * @param uuid
	 * @param fullName
	 * @param schulkuerzel
	 */
	public LehrerCreated(final LocalDateTime occouredOn, final String uuid, final String fullName, final String schulkuerzel) {

		if (occouredOn == null) {

			throw new IllegalArgumentException("occouredOn darf nicht null sein.");
		}

		if (StringUtils.isBlank(uuid)) {

			throw new IllegalArgumentException("uuid darf nicht blank sein.");
		}

		if (StringUtils.isBlank(fullName)) {

			throw new IllegalArgumentException("fullName darf nicht blank sein.");
		}

		if (StringUtils.isBlank(schulkuerzel)) {

			throw new IllegalArgumentException("schulkuerzel darf nicht blank sein.");
		}

		this.occouredOn = occouredOn;
		this.uuid = uuid;
		this.fullName = fullName;
		this.schulkuerzel = schulkuerzel;
		this.rolle = Rolle.LEHRER;
	}

	@Override
	public LocalDateTime occuredOn() {

		return this.occouredOn;
	}

	@Override
	public String typeName() {

		return LehrerCreated.class.getSimpleName();
	}

	public String fullName() {

		return this.fullName;
	}

	public String schulkuerzel() {

		return this.schulkuerzel;
	}

	public String uuid() {

		return this.uuid;
	}
}
