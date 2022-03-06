// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mustertexte.events;

import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * MustertextDeletedEvent
 */
public class MustertextDeletedEvent extends AbstractMustertextEvent {

	/**
	 *
	 */
	public MustertextDeletedEvent() {

		super();

	}

	/**
	 * @param triggeringUser
	 */
	public MustertextDeletedEvent(final String triggeringUser) {

		super(triggeringUser);

	}

	@Override
	public String typeName() {

		return EventType.MUSTERTEXT_DELETED.getLabel();
	}

}
