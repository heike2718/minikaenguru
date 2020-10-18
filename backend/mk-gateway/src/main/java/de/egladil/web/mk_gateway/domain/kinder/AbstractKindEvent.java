// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

	public AbstractKindEvent withTriggeringUser(final String triggeringUser) {

		this.triggeringUser = triggeringUser;
		return this;
	}

	public AbstractKindEvent withKlasseID(final String klasseID) {

		this.klasseID = klasseID;
		return this;
	}
}
