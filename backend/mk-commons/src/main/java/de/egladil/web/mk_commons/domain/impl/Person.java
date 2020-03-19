// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.egladil.web.commons_validation.annotations.StringLatin;

/**
 * Kontakt
 */
@Embeddable
public class Person {

	@StringLatin
	@NotBlank
	@Size(min = 1, max = 100)
	@Column(name = "VORNAME")
	private String vorname;

	@StringLatin
	@NotBlank
	@Size(min = 1, max = 100)
	@Column(name = "NACHNAME")
	private String nachname;

	@Column(name = "LAST_LOGIN")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date lastLogin;

	@Column(name = "LAST_LOGOUT")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	private Date lastLogout;

	@Column(name = "ANONYM")
	@JsonIgnore
	private boolean anonym;

	/**
	 * Erzeugt eine Instanz von Kontaktdaten
	 */
	public Person() {

	}

	/**
	 * Erzeugt eine Instanz von Kontaktdaten
	 */
	public Person(final String vorname, final String nachname) {

		super();
		this.vorname = vorname;
		this.nachname = nachname;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return vorname + " " + nachname;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((nachname == null) ? 0 : nachname.toLowerCase().hashCode());
		result = prime * result + ((vorname == null) ? 0 : vorname.toLowerCase().hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Person other = (Person) obj;

		if (nachname == null) {

			if (other.nachname != null)
				return false;
		} else if (!nachname.equalsIgnoreCase(other.nachname))
			return false;

		if (vorname == null) {

			if (other.vorname != null)
				return false;
		} else if (!vorname.equalsIgnoreCase(other.vorname))
			return false;
		return true;
	}

	/**
	 * Setzt die Membervariable
	 *
	 * @param vorname
	 *                neuer Wert der Membervariablen vorname
	 */
	public void setVorname(final String vorname) {

		this.vorname = vorname;
	}

	/**
	 * Setzt die Membervariable
	 *
	 * @param nachname
	 *                 neuer Wert der Membervariablen nachname
	 */
	public void setNachname(final String nachname) {

		this.nachname = nachname;
	}

	/**
	 * Liefert die Membervariable lastVisit
	 *
	 * @return die Membervariable lastVisit
	 */
	public Date getLastLogout() {

		return lastLogout;
	}

	/**
	 * Setzt die Membervariable
	 *
	 * @param lastVisit
	 *                  neuer Wert der Membervariablen lastVisit
	 */
	public void setLastLogout(final Date lastVisit) {

		this.lastLogout = lastVisit;
	}

	/**
	 * Liefert die Membervariable anonym
	 *
	 * @return die Membervariable anonym
	 */
	public boolean isAnonym() {

		return anonym;
	}

	/**
	 * Setzt die Membervariable
	 *
	 * @param anonym
	 *               neuer Wert der Membervariablen anonym
	 */
	public void setAnonym(final boolean anonym) {

		this.anonym = anonym;
	}

	/**
	 * Liefert die Membervariable lastLogin
	 *
	 * @return die Membervariable lastLogin
	 */
	public Date getLastLogin() {

		return lastLogin;
	}

	/**
	 * Setzt die Membervariable
	 *
	 * @param lastLogin
	 *                  neuer Wert der Membervariablen lastLogin
	 */
	public void setLastLogin(final Date lastLogin) {

		this.lastLogin = lastLogin;
	}

	/**
	 * Gibt die Zeit des letzten Logins in die Anwendung zurück.
	 *
	 * @return Long oder 0.
	 */
	public Long getLastAccessTime() {

		final Long lastAccess = lastLogin == null ? null : lastLogin.getTime();
		return lastAccess;
	}

	public String getFullName() {

		return vorname + " " + nachname;
	}

	public String getVorname() {

		return vorname;
	}

	public String getNachname() {

		return nachname;
	}
}
