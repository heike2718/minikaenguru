// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.event.AbstractDomainEvent;

/**
 * AbstractKlasseEvent
 */
public abstract class AbstractKlasseEvent extends AbstractDomainEvent {

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

		super();
	}

	public AbstractKlasseEvent(final String triggeringUser) {

		super();
		this.triggeringUser = triggeringUser;
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
}
