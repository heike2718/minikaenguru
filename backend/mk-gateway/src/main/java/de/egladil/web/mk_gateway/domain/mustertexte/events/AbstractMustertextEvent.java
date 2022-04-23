// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mustertexte.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.event.AbstractDomainEvent;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertextkategorie;

/**
 * AbstractMustertextEvent
 */
public abstract class AbstractMustertextEvent extends AbstractDomainEvent {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private Mustertextkategorie kategorie;

	@JsonProperty
	private String name;

	@JsonProperty
	private String triggeringUser;

	/**
	 *
	 */
	AbstractMustertextEvent() {

		super();

	}

	/**
	 * @param triggeringUser
	 */
	public AbstractMustertextEvent(final String triggeringUser) {

		this.triggeringUser = triggeringUser;
	}

	public Mustertextkategorie getKategorie() {

		return kategorie;
	}

	public AbstractMustertextEvent withKategorie(final Mustertextkategorie kategorie) {

		this.kategorie = kategorie;
		return this;
	}

	public String getName() {

		return name;
	}

	public AbstractMustertextEvent withName(final String name) {

		this.name = name;
		return this;
	}

	public String getTriggeringUser() {

		return triggeringUser;
	}

	public AbstractMustertextEvent withTriggeringUser(final String triggeringUser) {

		this.triggeringUser = triggeringUser;
		return this;
	}

}
