// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.domain.mail.Versandinformation;

/**
 * VersandinfoAPIModel
 */
public class VersandinfoAPIModel {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String newsletterID;

	@JsonProperty
	private Empfaengertyp empfaengertyp;

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

	public static VersandinfoAPIModel createFromVersandinfo(final Versandinformation versandinfo) {

		VersandinfoAPIModel result = new VersandinfoAPIModel();
		result.uuid = versandinfo.identifier().identifier();
		result.newsletterID = versandinfo.newsletterID().identifier();
		result.empfaengertyp = versandinfo.empfaengertyp();
		result.anzahlAktuellVersendet = versandinfo.anzahlAktuellVersendet();
		result.anzahlEmpaenger = versandinfo.anzahlEmpaenger();
		result.versandBegonnenAm = versandinfo.versandBegonnenAm();
		result.versandBeendetAm = versandinfo.versandBeendetAm();
		result.versandMitFehler = versandinfo.mitFehler();
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

}
