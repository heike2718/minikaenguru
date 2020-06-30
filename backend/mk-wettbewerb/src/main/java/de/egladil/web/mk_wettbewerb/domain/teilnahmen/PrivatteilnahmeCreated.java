// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import de.egladil.web.mk_wettbewerb.domain.semantik.DomainEvent;

/**
 * PrivatteilnahmeCreated
 */
@DomainEvent
public class PrivatteilnahmeCreated extends AbstractTeilnahmeEvent {

	protected PrivatteilnahmeCreated(final Integer wettbewerbsjahr, final String teilnahmenummer, final String createdBy) {

		super(wettbewerbsjahr, teilnahmenummer, createdBy);
	}

	public static PrivatteilnahmeCreated create(final Privatteilnahme teilnahme, final String createdBy) {

		return new PrivatteilnahmeCreated(teilnahme.wettbewerbID().jahr(), teilnahme.teilnahmenummer().identifier(), createdBy);
	}

	@Override
	public String typeName() {

		return TYPE_PRIVATTEILNAHME_CREATED;
	}

}
