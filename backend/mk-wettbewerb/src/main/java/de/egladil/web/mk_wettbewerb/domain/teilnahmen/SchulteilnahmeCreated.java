// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import de.egladil.web.mk_wettbewerb.domain.semantik.DomainEvent;

/**
 * SchulteilnahmeCreated
 */
@DomainEvent
public class SchulteilnahmeCreated extends AbstractSchulteilnahmeEvent {

	public SchulteilnahmeCreated(final Integer wettbewerbsjahr, final String teilnahmenummer, final String schulname, final String createdBy) {

		super(wettbewerbsjahr, teilnahmenummer, createdBy, schulname);
	}

	@Override
	public String typeName() {

		return TYPE_SCHULTEILNAHME_CREATED;
	}

}
