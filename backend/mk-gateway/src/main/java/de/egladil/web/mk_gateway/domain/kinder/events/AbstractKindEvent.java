// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.events;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * AbstractKindEvent
 */
public abstract class AbstractKindEvent implements MkGatewayDomainEvent {

	@JsonIgnore
	private final LocalDateTime occuredOn;

	@JsonProperty
	private String kindID;

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private Klassenstufe klassenstufe;

	@JsonProperty
	private Sprache sprache;

	@JsonProperty
	private String triggeringUser;

	@JsonProperty
	private String klasseID;

	AbstractKindEvent() {

		this.occuredOn = CommonTimeUtils.now();
	}

	public AbstractKindEvent(final String triggeringUser) {

		this();
		this.triggeringUser = triggeringUser;
	}

	@Override
	public LocalDateTime occuredOn() {

		return this.occuredOn;
	}

	public AbstractKindEvent withKindID(final String kindID) {

		this.kindID = kindID;
		return this;
	}

	public AbstractKindEvent withTeilnahmenummer(final String teilnahmenummer) {

		this.teilnahmenummer = teilnahmenummer;
		return this;
	}

	public AbstractKindEvent withKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
		return this;
	}

	public AbstractKindEvent withSprache(final Sprache sprache) {

		this.sprache = sprache;
		return this;
	}

	public AbstractKindEvent withKlasseID(final String klasseID) {

		this.klasseID = klasseID;
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
