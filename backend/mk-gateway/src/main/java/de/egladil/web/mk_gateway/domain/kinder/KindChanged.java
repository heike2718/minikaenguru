// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * KindChanged
 */
public class KindChanged extends AbstractKindEvent {

	public KindChanged() {

		super();
	}

	public KindChanged(final String triggeringUser) {

		super(triggeringUser);
	}

	@Override
	public String typeName() {

		return EventType.KIND_CHANGED.getLabel();
	}

}
