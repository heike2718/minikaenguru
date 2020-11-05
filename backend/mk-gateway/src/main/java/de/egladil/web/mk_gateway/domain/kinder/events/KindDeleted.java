// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.events;

import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * KindDeleted
 */
public class KindDeleted extends AbstractKindEvent {

	public KindDeleted() {

		super();

	}

	public KindDeleted(final String triggeringUser) {

		super(triggeringUser);

	}

	@Override
	public String typeName() {

		return EventType.KIND_DELETED.getLabel();
	}

}
