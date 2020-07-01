// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_wettbewerb_admin.domain.semantik.DomainEvent;

/**
 * SecurityIncidentRegistered
 */
@DomainEvent
public class SecurityIncidentRegistered implements WettbewerbDomainEvent {

	@JsonIgnore
	private final LocalDateTime occuredOn;

	@JsonProperty
	private String message;

	SecurityIncidentRegistered() {

		this.occuredOn = CommonTimeUtils.now();

	}

	public SecurityIncidentRegistered(final String message) {

		this();
		this.message = message;

	}

	@Override
	public LocalDateTime occuredOn() {

		return this.occuredOn;
	}

	@Override
	public String typeName() {

		return TYPE_SECURITY_INCIDENT_REGISTERED;
	}

	String message() {

		return message;
	}

}
