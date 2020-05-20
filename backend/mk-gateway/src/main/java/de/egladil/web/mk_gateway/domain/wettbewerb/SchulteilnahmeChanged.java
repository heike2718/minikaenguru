// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;
import de.egladil.web.mk_gateway.domain.semantik.DomainEvent;

/**
 * SchulteilnahmeChanged
 */
@DomainEvent
public class SchulteilnahmeChanged extends AbstractSchulteilnahmeEvent implements MkGatewayDomainEvent {

	public SchulteilnahmeChanged(final Integer wettbewerbsjahr, final String teilnahmenummer, final String schulname, final String changedBy) {

		super(wettbewerbsjahr, teilnahmenummer, changedBy, schulname);
	}

	@Override
	public String typeName() {

		return SchulteilnahmeChanged.class.getSimpleName();
	}

}
