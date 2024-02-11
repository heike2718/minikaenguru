// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;

/**
 * Versandauftrag ist ein Auftrag zur Versendung eines Newsletters. Er wird von sogenannten NewsletterAuslieferungen referenziert.
 */
@AggregateRoot
public class Versandauftrag {

	@JsonProperty
	private Identifier identifier;

	@JsonProperty
	private Identifier newsletterID;

	@JsonProperty
	private Empfaengertyp empfaengertyp;

	@JsonProperty
	private int anzahlAktuellVersendet;

	@JsonProperty
	private int anzahlEmpaenger;

	@JsonProperty
	private StatusAuslieferung status;

	@JsonProperty
	private String erfasstAm;

	@JsonProperty
	private String versandBegonnenAm;

	@JsonProperty
	private String versandBeendetAm;

	@JsonProperty
	private String fehlermeldung;

	@Override
	public int hashCode() {

		return Objects.hash(identifier);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		Versandauftrag other = (Versandauftrag) obj;
		return Objects.equals(identifier, other.identifier);
	}

	@Override
	public String toString() {

		return "Versandauftrag [identifier=" + identifier + ", newsletterID=" + newsletterID + ", empfaengertyp="
			+ empfaengertyp + ", versandBegonnenAm=" + versandBegonnenAm + ", versandBeendetAm=" + versandBeendetAm + "]";
	}

	public Identifier identifier() {

		return identifier;
	}

	public Versandauftrag withIdentifier(final Identifier identifier) {

		this.identifier = identifier;
		return this;
	}

	public Identifier newsletterID() {

		return newsletterID;
	}

	public Versandauftrag withNewsletterID(final Identifier newsletterID) {

		if (this.newsletterID != null && !this.newsletterID.equals(newsletterID)) {

			throw new IllegalStateException("newsletterID darf nicht geändert werden.");
		}

		this.newsletterID = newsletterID;
		return this;
	}

	public Empfaengertyp empfaengertyp() {

		return empfaengertyp;
	}

	public Versandauftrag withEmpfaengertyp(final Empfaengertyp empfaengertyp) {

		if (this.empfaengertyp != null && this.empfaengertyp != empfaengertyp) {

			throw new IllegalStateException("empfaengertyp darf nicht geändert werden.");
		}

		this.empfaengertyp = empfaengertyp;
		return this;
	}

	public int anzahlAktuellVersendet() {

		return anzahlAktuellVersendet;
	}

	public Versandauftrag withAnzahlAktuellVersendet(final int anzahlAktuellVersendet) {

		this.anzahlAktuellVersendet = anzahlAktuellVersendet;
		return this;
	}

	public void setAnzahlAktuellVersendet(final int anzahlAktuellVersendet) {

		this.anzahlAktuellVersendet = anzahlAktuellVersendet;
	}

	public int anzahlEmpaenger() {

		return anzahlEmpaenger;
	}

	public Versandauftrag withAnzahlEmpaenger(final int anzahlEmpaenger) {

		this.anzahlEmpaenger = anzahlEmpaenger;
		return this;
	}

	public void setAnzahlEmpaenger(final int anzahlEmpaenger) {

		this.anzahlEmpaenger = anzahlEmpaenger;
	}

	public String versandBegonnenAm() {

		return versandBegonnenAm;
	}

	public Versandauftrag withVersandBegonnenAm(final String versandBegonnenAm) {

		this.versandBegonnenAm = versandBegonnenAm;
		return this;
	}

	public void setVersandBegonnenAm(final String versandBegonnenAm) {

		this.versandBegonnenAm = versandBegonnenAm;
	}

	public String versandBeendetAm() {

		return versandBeendetAm;
	}

	public Versandauftrag withVersandBeendetAm(final String versandBeendetAm) {

		this.versandBeendetAm = versandBeendetAm;
		return this;
	}

	public String fehlermeldung() {

		return fehlermeldung;
	}

	public Versandauftrag withFehlermeldung(final String fehlermeldung) {

		this.fehlermeldung = fehlermeldung;
		return this;
	}

	public StatusAuslieferung getStatus() {

		return status;
	}

	public Versandauftrag withStatus(final StatusAuslieferung status) {

		this.status = status;
		return this;
	}

	public void setStatus(final StatusAuslieferung status) {

		this.status = status;
	}

	public String getErfasstAm() {

		return erfasstAm;
	}

	public Versandauftrag withErfasstAm(final String erfasstAm) {

		this.erfasstAm = erfasstAm;
		return this;
	}

	@JsonIgnore
	public boolean mitFehler() {

		return StringUtils.isNotBlank(fehlermeldung);
	}

	public void setVersandBeendetAm(final String versandBeendetAm) {

		this.versandBeendetAm = versandBeendetAm;
	}

}
