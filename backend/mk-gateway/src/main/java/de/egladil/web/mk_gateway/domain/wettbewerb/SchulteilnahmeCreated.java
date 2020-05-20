// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;
import de.egladil.web.mk_gateway.domain.semantik.DomainEvent;

/**
 * SchulteilnahmeCreated
 */
@DomainEvent
public class SchulteilnahmeCreated extends AbstractSchulteilnahmeEvent implements MkGatewayDomainEvent {

	public SchulteilnahmeCreated(final Integer wettbewerbsjahr, final String teilnahmenummer, final String schulname, final String createdBy) {

		super(wettbewerbsjahr, teilnahmenummer, createdBy, schulname);
	}

	@Override
	public String typeName() {

		return SchulteilnahmeCreated.class.getSimpleName();
	}

}
