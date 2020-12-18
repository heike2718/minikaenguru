// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.event.AbstractDomainEvent;
import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * NewsletterversandProgress
 */
public class NewsletterversandProgress extends AbstractDomainEvent {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String versandBegonnenAm;

	@JsonProperty
	private int anzahlEmpfaenger;

	@JsonProperty
	private int aktuellVersendet;

	@Override
	public String typeName() {

		return EventType.NEWSLETTERVERSAND_PROGRESS.getLabel();
	}

	public String uuid() {

		return uuid;
	}

	public NewsletterversandProgress withUuid(final String uuid) {

		this.uuid = uuid;
		return this;
	}

	public String versandBegonnenAm() {

		return versandBegonnenAm;
	}

	public NewsletterversandProgress withVersandBegonnenAm(final String versandBegonnenAm) {

		this.versandBegonnenAm = versandBegonnenAm;
		return this;
	}

	public int anzahlEmpfaenger() {

		return anzahlEmpfaenger;
	}

	public NewsletterversandProgress withAnzahlEmpfaenger(final int anzahlEmpfaenger) {

		this.anzahlEmpfaenger = anzahlEmpfaenger;
		return this;
	}

	public int aktuellVersendet() {

		return aktuellVersendet;
	}

	public NewsletterversandProgress withAktuellVersendet(final int aktuellVersendet) {

		this.aktuellVersendet = aktuellVersendet;
		return this;
	}

}
