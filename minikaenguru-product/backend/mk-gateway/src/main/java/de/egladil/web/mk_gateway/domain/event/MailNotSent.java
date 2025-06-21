// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;

/**
 * MailNotSent
 */
public class MailNotSent extends AbstractDomainEvent {

	@JsonIgnore
	private final LocalDateTime occuredOn;

	@JsonProperty
	private String message;

	MailNotSent() {

		this.occuredOn = CommonTimeUtils.now();

	}

	public MailNotSent(final String message) {

		this();
		this.message = message;
	}

	@Override
	public LocalDateTime occuredOn() {

		return this.occuredOn;
	}

	@Override
	public String typeName() {

		return EventType.MAIL_NOT_SENT.getLabel();
	}

	@Override
	public String toString() {

		return message;
	}

}
