// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;

/**
 * PersistenteVersandinfo
 */
@Entity
@Table(name = "VERSANDINFOS")
@NamedQueries({
	@NamedQuery(
		name = "PersistenteVersandinfo.FIND_FOR_NEWSLETTER",
		query = "select v from PersistenteVersandinfo v where v.newsletterID = :newsletterID order by v.empfaengertyp"),
	@NamedQuery(name = "PersistenteVersandinfo.FIND_BY_UUID", query = "select v from PersistenteVersandinfo v where v.uuid = :uuid")
})
public class PersistenteVersandinfo extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 1L;

	public static final String FIND_FOR_NEWSLETTER = "PersistenteVersandinfo.FIND_FOR_NEWSLETTER";

	public static final String FIND_BY_UUID = "PersistenteVersandinfo.FIND_BY_UUID";

	@Column(name = "NEWSLETTER_UUID")
	private String newsletterID;

	@Column(name = "EMPFAENGERTYP")
	@Enumerated(EnumType.STRING)
	private Empfaengertyp empfaengertyp;

	@Column(name = "VERSAND_BEGONNEN_AM")
	private LocalDateTime versandBegonnenAm;

	@Column(name = "VERSAND_BEENDET_AM")
	private LocalDateTime versandBeendetAm;

	@Column(name = "ANZAHL_EMPFAENGER")
	private int anzahlEmpfaenger;

	@Column(name = "ANZAHL_VERSENDET")
	private int anzahlVersendet;

	@Column(name = "MIT_FEHLERN")
	private boolean versandMitFehlern;

	public String getNewsletterID() {

		return newsletterID;
	}

	public void setNewsletterID(final String newsletterID) {

		this.newsletterID = newsletterID;
	}

	public Empfaengertyp getEmpfaengertyp() {

		return empfaengertyp;
	}

	public void setEmpfaengertyp(final Empfaengertyp empfaengertyp) {

		this.empfaengertyp = empfaengertyp;
	}

	public int getAnzahlEmpfaenger() {

		return anzahlEmpfaenger;
	}

	public void setAnzahlEmpfaenger(final int anzahlEmpfaenger) {

		this.anzahlEmpfaenger = anzahlEmpfaenger;
	}

	public int getAnzahlVersendet() {

		return anzahlVersendet;
	}

	public void setAnzahlVersendet(final int anzahlVersendet) {

		this.anzahlVersendet = anzahlVersendet;
	}

	public LocalDateTime getVersandBegonnenAm() {

		return versandBegonnenAm;
	}

	public void setVersandBegonnenAm(final LocalDateTime versandBegonnenAm) {

		this.versandBegonnenAm = versandBegonnenAm;
	}

	public LocalDateTime getVersandBeendetAm() {

		return versandBeendetAm;
	}

	public void setVersandBeendetAm(final LocalDateTime versandBeendetAm) {

		this.versandBeendetAm = versandBeendetAm;
	}

	public boolean isVersandMitFehlern() {

		return versandMitFehlern;
	}

	public void setVersandMitFehlern(final boolean versandMitFehlern) {

		this.versandMitFehlern = versandMitFehlern;
	}

}
