// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.events;

import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * KlasseDeleted
 */
public class KlasseDeleted extends AbstractKlasseEvent {

	/**
	 *
	 */
	KlasseDeleted() {

		super();
	}

	/**
	 * @param triggeringUser
	 */
	public KlasseDeleted(final String triggeringUser) {

		super(triggeringUser);

	}

	@Override
	public String typeName() {

		return EventType.KLASSE_DELETED.getLabel();
	}

}
