// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.domain.newsletterversand.StatusAuslieferung;
import de.egladil.web.mk_gateway.domain.newsletterversand.Versandauftrag;

/**
 * VersandauftragDTO
 */
public class VersandauftragDTO {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String newsletterID;

	@JsonProperty
	private String newsletterBetreff;

	@JsonProperty
	private Empfaengertyp empfaengertyp;

	@JsonProperty
	private StatusAuslieferung status;

	@JsonProperty
	private int anzahlAktuellVersendet;

	@JsonProperty
	private int anzahlEmpaenger;

	@JsonProperty
	private String versandBegonnenAm;

	@JsonProperty
	private String versandBeendetAm;

	@JsonProperty
	private boolean versandMitFehler;

	public static VersandauftragDTO createFromVersandauftrag(final Versandauftrag versandauftrag) {

		VersandauftragDTO result = new VersandauftragDTO();
		result.uuid = versandauftrag.identifier().identifier();
		result.newsletterID = versandauftrag.newsletterID().identifier();
		result.empfaengertyp = versandauftrag.empfaengertyp();
		result.anzahlAktuellVersendet = versandauftrag.anzahlAktuellVersendet();
		result.anzahlEmpaenger = versandauftrag.anzahlEmpaenger();
		result.versandBegonnenAm = versandauftrag.versandBegonnenAm();
		result.versandBeendetAm = versandauftrag.versandBeendetAm();
		result.versandMitFehler = versandauftrag.mitFehler();
		result.status = versandauftrag.getStatus();
		return result;
	}

	public String uuid() {

		return uuid;
	}

	public String newsletterID() {

		return newsletterID;
	}

	public Empfaengertyp empfaengertyp() {

		return empfaengertyp;
	}

	public int anzahlAktuellVersendet() {

		return anzahlAktuellVersendet;
	}

	public int anzahlEmpaenger() {

		return anzahlEmpaenger;
	}

	public String versandBegonnenAm() {

		return versandBegonnenAm;
	}

	public String versandBeendetAm() {

		return versandBeendetAm;
	}

	public boolean versandMitFehler() {

		return versandMitFehler;
	}

	public StatusAuslieferung status() {

		return status;
	}

	public String getNewsletterBetreff() {

		return newsletterBetreff;
	}

	public void setNewsletterBetreff(final String newsletterBetreff) {

		this.newsletterBetreff = newsletterBetreff;
	}

}
