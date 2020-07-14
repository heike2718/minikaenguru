// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;

/**
 * DataInconsistencyRegistered
 */
public class DataInconsistencyRegistered implements KatalogeDomainEvent {

	@JsonIgnore
	private final LocalDateTime occuredOn;

	@JsonProperty
	private String message;

	DataInconsistencyRegistered() {

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

	public String message() {

		return message;
	}

	@Override
	public String toString() {

		return message;
	}

}
