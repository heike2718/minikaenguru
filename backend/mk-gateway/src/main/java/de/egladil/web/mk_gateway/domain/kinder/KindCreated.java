// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * KindCreated
 */
public class KindCreated extends AbstractKindEvent {

	public KindCreated() {

		super();
	}

	public KindCreated(final String triggeringUser) {

		super(triggeringUser);

	}

	@Override
	public String typeName() {

		return EventType.KIND_CREATED.getLabel();
	}

}
