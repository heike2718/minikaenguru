// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import de.egladil.web.mk_wettbewerb.domain.event.WettbewerbDomainEvent;

/**
 * PrivatteilnahmeCreated
 */
public class PrivatteilnahmeCreated extends AbstractTeilnahmeEvent implements WettbewerbDomainEvent {

	public PrivatteilnahmeCreated(final Integer wettbewerbsjahr, final String teilnahmenummer, final String createdBy) {

		super(wettbewerbsjahr, teilnahmenummer, createdBy);
	}

	@Override
	public String typeName() {

		return PrivatteilnahmeCreated.class.getSimpleName();
	}

}
