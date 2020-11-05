// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassen.events;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;

/**
 * AbstractKlasseEvent
 */
public abstract class AbstractKlasseEvent implements MkGatewayDomainEvent {

	@JsonIgnore
	private final LocalDateTime occuredOn;

	@JsonProperty
	private String klasseID;

	@JsonProperty
	private String schulkuerzel;

	@JsonProperty
	private String name;

	@JsonProperty
	private String triggeringUser;

	/**
	 *
	 */
	AbstractKlasseEvent() {

		this.occuredOn = CommonTimeUtils.now();
	}

	public AbstractKlasseEvent(final String triggeringUser) {

		this();
		this.triggeringUser = triggeringUser;
	}

	@Override
	public LocalDateTime occuredOn() {

		return this.occuredOn;
	}

	public String klasseID() {

		return klasseID;
	}

	public AbstractKlasseEvent withKlasseID(final String klasseID) {

		this.klasseID = klasseID;
		return this;
	}

	public String schulkuerzel() {

		return schulkuerzel;
	}

	public AbstractKlasseEvent withSchulkuerzel(final String schulkuerzel) {

		this.schulkuerzel = schulkuerzel;
		return this;
	}

	public String triggeringUser() {

		return triggeringUser;
	}

	public String name() {

		return name;
	}

	public AbstractKlasseEvent withName(final String name) {

		this.name = name;
		return this;
	}

	public String serializeQuietly() {

		try {

			String body = new ObjectMapper().writeValueAsString(this);
			return body;
		} catch (JsonProcessingException e) {

			e.printStackTrace();
			return e.getMessage();
		}
	}

}
