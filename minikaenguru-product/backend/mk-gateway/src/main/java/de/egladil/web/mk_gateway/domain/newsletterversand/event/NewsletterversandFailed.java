// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand.event;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.event.AbstractDomainEvent;
import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * NewsletterversandFailed
 */
public class NewsletterversandFailed extends AbstractDomainEvent {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String message;

	@JsonProperty(value = "invalidEmails")
	private List<String> invalidMailaddresses;

	@JsonProperty(value = "validSentEmails")
	private List<String> validSentAddresses;

	@JsonProperty(value = "validUnsentEmails")
	private List<String> validUnsentAddresses;

	@Override
	public String typeName() {

		return EventType.NEWSLETTERVERSAND_FAILED.getLabel();
	}

	public String uuid() {

		return uuid;
	}

	public NewsletterversandFailed withUuid(final String uuid) {

		this.uuid = uuid;
		return this;
	}

	public String message() {

		return message;
	}

	public NewsletterversandFailed withMessage(final String message) {

		this.message = message;
		return this;
	}

	public List<String> invalidMailaddresses() {

		return invalidMailaddresses;
	}

	public NewsletterversandFailed withInvalidMailaddresses(final List<String> invalidMailaddresses) {

		this.invalidMailaddresses = invalidMailaddresses;
		return this;
	}

	public NewsletterversandFailed withValidSentAddresses(final List<String> validSentAddresses) {

		this.validSentAddresses = validSentAddresses;
		return this;
	}

	public NewsletterversandFailed withValidUnsentAddresses(final List<String> validUnsentAddresses) {

		this.validUnsentAddresses = validUnsentAddresses;
		return this;
	}

}
