// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mustertexte.events;

import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * MustertextSavedEvent
 */
public class MustertextSavedEvent extends AbstractMustertextEvent {

	/**
	 *
	 */
	public MustertextSavedEvent() {

		super();

	}

	/**
	 * @param triggeringUser
	 */
	public MustertextSavedEvent(final String triggeringUser) {

		super(triggeringUser);

	}

	@Override
	public String typeName() {

		return EventType.MUSTERTEXT_SAVED.getLabel();
	}

}
