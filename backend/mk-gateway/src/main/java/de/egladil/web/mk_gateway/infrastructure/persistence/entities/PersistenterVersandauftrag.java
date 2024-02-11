// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import java.time.LocalDateTime;

import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.domain.newsletterversand.StatusAuslieferung;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * PersistenterVersandauftrag
 */
@Entity
@Table(name = "NEWSLETTER_VERSANDAUFTRAEGE")
@NamedQueries({
	@NamedQuery(
		name = "PersistenterVersandauftrag.FIND_FOR_NEWSLETTER",
		query = "select v from PersistenterVersandauftrag v where v.newsletterID = :newsletterID order by v.empfaengertyp"),
	@NamedQuery(
		name = "PersistenterVersandauftrag.FIND_BY_UUID",
		query = "select v from PersistenterVersandauftrag v where v.uuid = :uuid"),
	@NamedQuery(
		name = "PersistenterVersandauftrag.FIND_NOT_COMPLETED",
		query = "select v from PersistenterVersandauftrag v where v.status != :statusCompleted and v.status != :statusErrors order by v.erfasstAm")
})
public class PersistenterVersandauftrag extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 1L;

	public static final String FIND_FOR_NEWSLETTER = "PersistenterVersandauftrag.FIND_FOR_NEWSLETTER";

	public static final String FIND_BY_UUID = "PersistenterVersandauftrag.FIND_BY_UUID";

	public static final String FIND_NOT_COMPLETED = "PersistenterVersandauftrag.FIND_NOT_COMPLETED";

	@Column(name = "NEWSLETTER_UUID")
	private String newsletterID;

	@Column(name = "EMPFAENGERTYP")
	@Enumerated(EnumType.STRING)
	private Empfaengertyp empfaengertyp;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private StatusAuslieferung status;

	@Column(name = "ERFASST_AM")
	private LocalDateTime erfasstAm;

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

	public StatusAuslieferung getStatus() {

		return status;
	}

	public void setStatus(final StatusAuslieferung status) {

		this.status = status;
	}

	public LocalDateTime getErfasstAm() {

		return erfasstAm;
	}

	public void setErfasstAm(final LocalDateTime erfasstAm) {

		this.erfasstAm = erfasstAm;
	}

}
