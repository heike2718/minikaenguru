// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.semantik.DomainEvent;

/**
 * AbstractSchulteilnahmeEvent
 */
@DomainEvent
public abstract class AbstractSchulteilnahmeEvent extends AbstractTeilnahmeEvent {

	@JsonProperty
	private final String schulname;

	public AbstractSchulteilnahmeEvent(final Integer wettbewerbsjahr, final String teilnahmenummer, final String triggerinUser, final String schulname) {

		super(wettbewerbsjahr, teilnahmenummer, triggerinUser);

		if (StringUtils.isBlank(schulname)) {

			throw new IllegalArgumentException("schulname darf nicht blank sein.");
		}
		this.schulname = schulname;
	}

	public String schulname() {

		return schulname;
	}

}
