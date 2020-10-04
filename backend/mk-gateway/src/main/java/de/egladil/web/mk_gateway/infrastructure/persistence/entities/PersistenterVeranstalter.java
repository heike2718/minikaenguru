// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;

/**
 * PersistenterVeranstalter
 */
@Entity
@Table(name = "VERANSTALTER")
@NamedQueries({
	@NamedQuery(
		name = "PersistenterVeranstalter.FIND_BY_UUID", query = "select v from PersistenterVeranstalter v where v.uuid = :uuid"),
	@NamedQuery(
		name = "PersistenterVeranstalter.FIND_BY_PARTIAL_UUID",
		query = "select v from PersistenterVeranstalter v where v.uuid like :suchstring"),
	@NamedQuery(
		name = "PersistenterVeranstalter.FIND_BY_PARTIAL_NAME",
		query = "select v from PersistenterVeranstalter v where lower(v.fullName) like :suchstring"),
	@NamedQuery(
		name = "PersistenterVeranstalter.FIND_BY_PARTIAL_EMAIL",
		query = "select v from PersistenterVeranstalter v where lower(v.email) like :suchstring"),
	@NamedQuery(
		name = "PersistenterVeranstalter.FIND_BY_TEILNAHMENUMMER",
		query = "select v from PersistenterVeranstalter v where lower(v.teilnahmenummern) like :suchstring"),
})
public class PersistenterVeranstalter extends ConcurrencySafeEntity {

	public static final String FIND_BY_UUID_QUERY = "PersistenterVeranstalter.FIND_BY_UUID";

	public static final String FIND_BY_PARTIAL_UUID_QUERY = "PersistenterVeranstalter.FIND_BY_PARTIAL_UUID";

	public static final String FIND_BY_PARTIAL_NAME_QUERY = "PersistenterVeranstalter.FIND_BY_PARTIAL_NAME";

	public static final String FIND_BY_PARTIAL_EMAIL_QUERY = "PersistenterVeranstalter.FIND_BY_PARTIAL_EMAIL";

	public static final String FIND_BY_TEILNAHMENUMMER_QUERY = "PersistenterVeranstalter.FIND_BY_TEILNAHMENUMMER";

	@Column(name = "ROLLE")
	@Enumerated(EnumType.STRING)
	private Rolle rolle;

	@NotNull
	@Size(min = 1, max = 101)
	@Column(name = "FULL_NAME")
	private String fullName;

	@Column(name = "TEILNAHMENUMMERN")
	private String teilnahmenummern;

	@Column(name = "ZUGANG_UNTERLAGEN")
	@Enumerated(EnumType.STRING)
	private ZugangUnterlagen zugangUnterlagen;

	@Column(name = "NEWSLETTER")
	private boolean newsletterEmpfaenger;

	@Column(name = "EMAIL")
	private String email;

	public String getTeilnahmenummern() {

		return teilnahmenummern;
	}

	public void setTeilnahmenummern(final String teilnahmekuerzel) {

		this.teilnahmenummern = teilnahmekuerzel;
	}

	public String getFullName() {

		return fullName;
	}

	public void setFullName(final String fullName) {

		this.fullName = fullName;
	}

	public Rolle getRolle() {

		return rolle;
	}

	public void setRolle(final Rolle rolle) {

		this.rolle = rolle;
	}

	public ZugangUnterlagen getZugangsberechtigungUnterlagen() {

		return zugangUnterlagen;
	}

	public void setZugangsberechtigungUnterlagen(final ZugangUnterlagen zugangUnterlagen) {

		this.zugangUnterlagen = zugangUnterlagen;
	}

	public boolean isNewsletterEmpfaenger() {

		return newsletterEmpfaenger;
	}

	public void setNewsletterEmpfaenger(final boolean newsletter) {

		this.newsletterEmpfaenger = newsletter;
	}

	public String getEmail() {

		return email;
	}

	public void setEmail(final String email) {

		this.email = email;
	}
}
