// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.event.AbstractDomainEvent;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * AbstractKindEvent
 */
public abstract class AbstractKindEvent extends AbstractDomainEvent {

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

	@JsonProperty
	private String loesungszettelID;

	AbstractKindEvent() {

		super();
	}

	public AbstractKindEvent(final String triggeringUser) {

		super();
		this.triggeringUser = triggeringUser;
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

	public AbstractKindEvent withLoesungszettelID(final String loesungszettelID) {

		this.loesungszettelID = loesungszettelID;
		return this;
	}
}
