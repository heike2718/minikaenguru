// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import de.egladil.web.mk_wettbewerb.domain.event.WettbewerbDomainEvent;

/**
 * SchulteilnahmeCreated
 */
public class SchulteilnahmeCreated extends AbstractSchulteilnahmeEvent implements WettbewerbDomainEvent {

	public SchulteilnahmeCreated(final Integer wettbewerbsjahr, final String teilnahmenummer, final String schulname, final String createdBy) {

		super(wettbewerbsjahr, teilnahmenummer, createdBy, schulname);
	}

	@Override
	public String typeName() {

		return SchulteilnahmeCreated.class.getSimpleName();
	}

}
