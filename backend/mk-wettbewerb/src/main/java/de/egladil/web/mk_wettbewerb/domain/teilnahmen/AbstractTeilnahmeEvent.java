// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_wettbewerb.domain.event.WettbewerbDomainEvent;
import de.egladil.web.mk_wettbewerb.domain.semantik.DomainEvent;

/**
 * AbstractTeilnahmeEvent
 */
@DomainEvent
public abstract class AbstractTeilnahmeEvent implements WettbewerbDomainEvent {

	@JsonIgnore
	private final LocalDateTime occouredOn;

	@JsonProperty
	private final Integer wettbewerbsjahr;

	@JsonProperty
	private final String teilnahmenummer;

	@JsonProperty
	private final String triggeringUser;

	protected AbstractTeilnahmeEvent(final Integer wettbewerbsjahr, final String teilnahmenummer, final String triggeringUser) {

		if (wettbewerbsjahr == null) {

			throw new IllegalArgumentException("wettbewerbsjahr darf nicht null sein.");
		}

		if (StringUtils.isBlank(teilnahmenummer)) {

			throw new IllegalArgumentException("teilnahmenummer darf nicht blank sein.");
		}

		if (StringUtils.isBlank(triggeringUser)) {

			throw new IllegalArgumentException("triggeringUser darf nicht blank sein.");
		}

		this.occouredOn = CommonTimeUtils.now();
		this.wettbewerbsjahr = wettbewerbsjahr;
		this.teilnahmenummer = teilnahmenummer;
		this.triggeringUser = triggeringUser;
	}

	@Override
	public LocalDateTime occuredOn() {

		return occouredOn;
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
