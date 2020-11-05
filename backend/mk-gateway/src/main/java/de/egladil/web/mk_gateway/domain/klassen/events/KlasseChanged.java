// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassen.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * KlasseChanged
 */
public class KlasseChanged extends AbstractKlasseEvent {

	@JsonProperty
	private String nameAlt;

	/**
	 *
	 */
	KlasseChanged() {

		super();
	}

	/**
	 * @param triggeringUser
	 */
	public KlasseChanged(final String triggeringUser) {

		super(triggeringUser);

	}

	@Override
	public String typeName() {

		return EventType.KLASSE_CHANGED.getLabel();
	}

	public String nameAlt() {

		return nameAlt;
	}

	public KlasseChanged withNameAlt(final String nameAlt) {

		this.nameAlt = nameAlt;
		return this;
	}

}
