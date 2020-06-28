// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import de.egladil.web.mk_wettbewerb.domain.semantik.DomainEvent;

/**
 * SchulteilnahmeChanged
 */
@DomainEvent
public class SchulteilnahmeChanged extends AbstractSchulteilnahmeEvent {

	public SchulteilnahmeChanged(final Integer wettbewerbsjahr, final String teilnahmenummer, final String schulname, final String changedBy) {

		super(wettbewerbsjahr, teilnahmenummer, changedBy, schulname);
	}

	@Override
	public String typeName() {

		return TYPE_SCHULTEILNAHME_CHANGED;
	}

}
