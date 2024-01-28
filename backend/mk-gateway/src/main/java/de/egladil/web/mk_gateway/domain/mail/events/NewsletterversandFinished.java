// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.event.AbstractDomainEvent;
import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * NewsletterversandFinished
 */
@Deprecated
public class NewsletterversandFinished extends AbstractDomainEvent {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String message;

	@JsonProperty
	private String versandBeendetAm;

	@Override
	public String typeName() {

		return EventType.NEWSLETTERVERSAND_FINISHED.getLabel();
	}

	public String uuid() {

		return uuid;
	}

	public NewsletterversandFinished withUuid(final String uuid) {

		this.uuid = uuid;
		return this;
	}

	public String message() {

		return message;
	}

	public NewsletterversandFinished withMessage(final String message) {

		this.message = message;
		return this;
	}

	public String versandBeendetAm() {

		return versandBeendetAm;
	}

	public NewsletterversandFinished withVersandBeendetAm(final String versandBeendetAm) {

		this.versandBeendetAm = versandBeendetAm;
		return this;
	}

}
