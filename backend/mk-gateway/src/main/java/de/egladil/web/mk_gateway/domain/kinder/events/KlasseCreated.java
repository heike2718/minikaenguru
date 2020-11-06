// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.events;

import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * KlasseCreated
 */
public class KlasseCreated extends AbstractKlasseEvent {

	/**
	 *
	 */
	KlasseCreated() {

		super();

	}

	/**
	 * @param triggeringUser
	 */
	public KlasseCreated(final String triggeringUser) {

		super(triggeringUser);

	}

	@Override
	public String typeName() {

		return EventType.KLASSE_CREATED.getLabel();
	}

}
