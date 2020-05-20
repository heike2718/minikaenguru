// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;
import de.egladil.web.mk_gateway.domain.semantik.DomainEvent;

/**
 * PrivatteilnahmeCreated
 */
@DomainEvent
public class PrivatteilnahmeCreated extends AbstractTeilnahmeEvent implements MkGatewayDomainEvent {

	public PrivatteilnahmeCreated(final Integer wettbewerbsjahr, final String teilnahmenummer, final String createdBy) {

		super(wettbewerbsjahr, teilnahmenummer, createdBy);
	}

	@Override
	public String typeName() {

		return PrivatteilnahmeCreated.class.getSimpleName();
	}

}
