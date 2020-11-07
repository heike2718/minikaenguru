// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.event.AbstractDomainEvent;
import de.egladil.web.mk_gateway.domain.semantik.DomainEvent;

/**
 * AbstractTeilnahmeEvent
 */
@DomainEvent
public abstract class AbstractTeilnahmeEvent extends AbstractDomainEvent {

	@JsonProperty
	private Integer wettbewerbsjahr;

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private String triggeringUser;

	AbstractTeilnahmeEvent() {

		super();
	}

	public AbstractTeilnahmeEvent(final Integer wettbewerbsjahr, final String teilnahmenummer, final String triggeringUser) {

		super();

		if (wettbewerbsjahr == null) {

			throw new IllegalArgumentException("wettbewerbsjahr darf nicht null sein.");
		}

		if (StringUtils.isBlank(teilnahmenummer)) {

			throw new IllegalArgumentException("teilnahmenummer darf nicht blank sein.");
		}

		if (StringUtils.isBlank(triggeringUser)) {

			throw new IllegalArgumentException("triggeringUser darf nicht blank sein.");
		}

		this.wettbewerbsjahr = wettbewerbsjahr;
		this.teilnahmenummer = teilnahmenummer;
		this.triggeringUser = triggeringUser;
	}

	public Integer wettbewerbsjahr() {

		return wettbewerbsjahr;
	}

	public String teilnahmenummer() {

		return teilnahmenummer;
	}

	public String triggeringUser() {

		return triggeringUser;
	}

}
