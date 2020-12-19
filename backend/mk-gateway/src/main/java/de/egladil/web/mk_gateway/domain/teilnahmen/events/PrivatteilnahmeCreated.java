// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.events;

import de.egladil.web.mk_gateway.domain.event.EventType;
import de.egladil.web.mk_gateway.domain.semantik.DomainEvent;
import de.egladil.web.mk_gateway.domain.teilnahmen.Privatteilnahme;

/**
 * PrivatteilnahmeCreated
 */
@DomainEvent
public class PrivatteilnahmeCreated extends AbstractTeilnahmeEvent {

	protected PrivatteilnahmeCreated() {

		super();

	}

	protected PrivatteilnahmeCreated(final Integer wettbewerbsjahr, final String teilnahmenummer, final String createdBy) {

		super(wettbewerbsjahr, teilnahmenummer, createdBy);
	}

	public static PrivatteilnahmeCreated create(final Privatteilnahme teilnahme, final String createdBy) {

		return new PrivatteilnahmeCreated(teilnahme.wettbewerbID().jahr(), teilnahme.teilnahmenummer().identifier(), createdBy);
	}

	@Override
	public String typeName() {

		return EventType.PRIVATTEILNAHME_CREATED.getLabel();
	}

}
