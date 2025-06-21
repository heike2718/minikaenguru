// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.events;

import de.egladil.web.mk_gateway.domain.event.EventType;
import de.egladil.web.mk_gateway.domain.semantik.DomainEvent;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;

/**
 * SchulteilnahmeChanged
 */
@DomainEvent
public class SchulteilnahmeChanged extends AbstractSchulteilnahmeEvent {

	protected SchulteilnahmeChanged() {

		super();
	}

	protected SchulteilnahmeChanged(final Integer wettbewerbsjahr, final String teilnahmenummer, final String schulname, final String changedBy) {

		super(wettbewerbsjahr, teilnahmenummer, changedBy, schulname);
	}

	public static SchulteilnahmeChanged create(final Schulteilnahme teilnahme, final String changedBy) {

		return new SchulteilnahmeChanged(teilnahme.wettbewerbID().jahr(), teilnahme.teilnahmenummer().identifier(),
			teilnahme.nameSchule(), changedBy);

	}

	@Override
	public String typeName() {

		return EventType.SCHULTEILNAHME_CHANGED.getLabel();
	}

}
