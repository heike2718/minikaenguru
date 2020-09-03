// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.semantik.DomainEvent;
import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;

/**
 * AbstractTeilnahmeEvent
 */
@DomainEvent
public abstract class AbstractTeilnahmeEvent implements MkGatewayDomainEvent {

	@JsonIgnore
	private final LocalDateTime occouredOn;

	@JsonProperty
	private Integer wettbewerbsjahr;

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private String triggeringUser;

	AbstractTeilnahmeEvent() {

		this.occouredOn = CommonTimeUtils.now();
	}

	public AbstractTeilnahmeEvent(final Integer wettbewerbsjahr, final String teilnahmenummer, final String triggeringUser) {

		this();

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