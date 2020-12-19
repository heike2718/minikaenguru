// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;

/**
 * Versandinformation
 */
@AggregateRoot
public class Versandinformation {

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
	private String versandBegonnenAm;

	@JsonProperty
	private String versandBeendetAm;

	@JsonProperty
	private String fehlermeldung;

	public static Versandinformation copy(final Versandinformation source) {

		Versandinformation target = new Versandinformation();
		target.anzahlAktuellVersendet = source.anzahlAktuellVersendet;
		target.anzahlEmpaenger = source.anzahlEmpaenger;
		target.empfaengertyp = source.empfaengertyp;
		target.identifier = source.identifier;
		target.newsletterID = source.newsletterID;
		target.versandBegonnenAm = source.versandBegonnenAm;
		target.versandBeendetAm = source.versandBeendetAm;
		target.fehlermeldung = source.fehlermeldung;
		return target;
	}

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
		Versandinformation other = (Versandinformation) obj;
		return Objects.equals(identifier, other.identifier);
	}

	@Override
	public String toString() {

		return "Versandinformation [identifier=" + identifier + ", newsletterID=" + newsletterID + ", empfaengertyp="
			+ empfaengertyp + ", versandBegonnenAm=" + versandBegonnenAm + ", versandBeendetAm=" + versandBeendetAm + "]";
	}

	public Identifier identifier() {

		return identifier;
	}

	public Versandinformation withIdentifier(final Identifier identifier) {

		if (this.identifier != null && !this.identifier.equals(identifier)) {

			throw new IllegalStateException("identifier darf nicht geändert werden.");
		}
		this.identifier = identifier;
		return this;
	}

	public Identifier newsletterID() {

		if (this.newsletterID != null && !this.newsletterID.equals(newsletterID)) {

			throw new IllegalStateException("newsletterID darf nicht geändert werden.");
		}

		return newsletterID;
	}

	public Versandinformation withNewsletterID(final Identifier newsletterID) {

		this.newsletterID = newsletterID;
		return this;
	}

	public Empfaengertyp empfaengertyp() {

		return empfaengertyp;
	}

	public Versandinformation withEmpfaengertyp(final Empfaengertyp empfaengertyp) {

		if (this.empfaengertyp != null && this.empfaengertyp != empfaengertyp) {

			throw new IllegalStateException("empfaengertyp darf nicht geändert werden.");
		}

		this.empfaengertyp = empfaengertyp;
		return this;
	}

	public int anzahlAktuellVersendet() {

		return anzahlAktuellVersendet;
	}

	public Versandinformation withAnzahlAktuellVersendet(final int anzahlAktuellVersendet) {

		this.anzahlAktuellVersendet = anzahlAktuellVersendet;
		return this;
	}

	public int anzahlEmpaenger() {

		return anzahlEmpaenger;
	}

	public Versandinformation withAnzahlEmpaenger(final int anzahlEmpaenger) {

		this.anzahlEmpaenger = anzahlEmpaenger;
		return this;
	}

	public String versandBegonnenAm() {

		return versandBegonnenAm;
	}

	public Versandinformation withVersandBegonnenAm(final String versandBegonnenAm) {

		this.versandBegonnenAm = versandBegonnenAm;
		return this;
	}

	public String versandBeendetAm() {

		return versandBeendetAm;
	}

	public Versandinformation withVersandBeendetAm(final String versandBeendetAm) {

		this.versandBeendetAm = versandBeendetAm;
		return this;
	}

	public String fehlermeldung() {

		return fehlermeldung;
	}

	public Versandinformation withFehlermeldung(final String fehlermeldung) {

		this.fehlermeldung = fehlermeldung;
		return this;
	}

	@JsonIgnore
	public boolean mitFehler() {

		return StringUtils.isNotBlank(fehlermeldung);
	}

	@JsonIgnore
	public boolean bereitsVersendet() {

		return StringUtils.isNotBlank(versandBeendetAm);
	}
}
