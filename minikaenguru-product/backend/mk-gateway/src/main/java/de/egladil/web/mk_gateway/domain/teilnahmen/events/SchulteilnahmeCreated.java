// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.events;

import de.egladil.web.mk_gateway.domain.event.EventType;
import de.egladil.web.mk_gateway.domain.semantik.DomainEvent;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;

/**
 * SchulteilnahmeCreated
 */
@DomainEvent
public class SchulteilnahmeCreated extends AbstractSchulteilnahmeEvent {

	protected SchulteilnahmeCreated() {

		super();

	}

	public SchulteilnahmeCreated(final Integer wettbewerbsjahr, final String teilnahmenummer, final String schulname, final String createdBy) {

		super(wettbewerbsjahr, teilnahmenummer, createdBy, schulname);
	}

	public static SchulteilnahmeCreated create(final Schulteilnahme teilnahme) {

		return new SchulteilnahmeCreated(teilnahme.wettbewerbID().jahr(), teilnahme.teilnahmenummer().identifier(),
			teilnahme.nameSchule(), teilnahme.angemeldetDurchVeranstalterId().identifier());

	}

	@Override
	public String typeName() {

		return EventType.SCHULTEILNAHME_CREATED.getLabel();
	}

}
