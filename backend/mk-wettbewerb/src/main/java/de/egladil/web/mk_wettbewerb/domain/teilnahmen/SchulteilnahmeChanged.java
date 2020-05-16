// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import de.egladil.web.mk_wettbewerb.domain.event.WettbewerbDomainEvent;

/**
 * SchulteilnahmeChanged
 */
public class SchulteilnahmeChanged extends AbstractSchulteilnahmeEvent implements WettbewerbDomainEvent {

	public SchulteilnahmeChanged(final Integer wettbewerbsjahr, final String teilnahmenummer, final String schulname, final String changedBy) {

		super(wettbewerbsjahr, teilnahmenummer, changedBy, schulname);
	}

	@Override
	public String typeName() {

		return SchulteilnahmeChanged.class.getSimpleName();
	}

}
