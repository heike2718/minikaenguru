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
 * DataInconsistencyRegistered
 */
@DomainEvent
public class DataInconsistencyRegistered implements WettbewerbDomainEvent {

	@JsonIgnore
	private final LocalDateTime occuredOn;

	@JsonProperty
	private String message;

	DataInconsistencyRegistered() {

		super();
		this.occuredOn = CommonTimeUtils.now();

	}

	public DataInconsistencyRegistered(final String message) {

		this();
		this.message = message;

	}

	@Override
	public LocalDateTime occuredOn() {

		return occuredOn;
	}

	@Override
	public String typeName() {

		return TYPE_DATA_INCONSISTENCY_REGISTERED;
	}

	String message() {

		return message;
	}

}
