// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;

/**
 * PersistenterVeranstalter
 */
@Entity
@Table(name = "VERANSTALTER")
@NamedQueries({
	@NamedQuery(
		name = "PersistenterVeranstalter.FIND_BY_UUIDS",
		query = "select v from PersistenterVeranstalter v where v.uuid in :uuids"),
	@NamedQuery(
		name = "PersistenterVeranstalter.FIND_BY_PARTIAL_UUID",
		query = "select v from PersistenterVeranstalter v where v.uuid like :suchstring order by v.uuid"),
	@NamedQuery(
		name = "PersistenterVeranstalter.FIND_BY_PARTIAL_NAME",
		query = "select v from PersistenterVeranstalter v where lower(v.fullName) like :suchstring order by v.uuid"),
	@NamedQuery(
		name = "PersistenterVeranstalter.FIND_BY_PARTIAL_EMAIL",
		query = "select v from PersistenterVeranstalter v where lower(v.email) like :suchstring order by v.uuid"),
	@NamedQuery(
		name = "PersistenterVeranstalter.FIND_BY_TEILNAHMENUMMER",
		query = "select v from PersistenterVeranstalter v where lower(v.teilnahmenummern) like :suchstring order by v.uuid"),
	@NamedQuery(
		name = "PersistenterVeranstalter.FIND_BY_ZUGANGSSTATUS",
		query = "select v from PersistenterVeranstalter v where zugangUnterlagen = :suchstring order by v.uuid"),
})
public class PersistenterVeranstalter extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 6012887050926370489L;

	public static final String FIND_BY_PARTIAL_UUID_QUERY = "PersistenterVeranstalter.FIND_BY_PARTIAL_UUID";

	public static final String FIND_BY_PARTIAL_NAME_QUERY = "PersistenterVeranstalter.FIND_BY_PARTIAL_NAME";

	public static final String FIND_BY_PARTIAL_EMAIL_QUERY = "PersistenterVeranstalter.FIND_BY_PARTIAL_EMAIL";

	public static final String FIND_BY_TEILNAHMENUMMER_QUERY = "PersistenterVeranstalter.FIND_BY_TEILNAHMENUMMER";

	public static final String FIND_BY_ZUGANGSSTATUS_QUERY = "PersistenterVeranstalter.FIND_BY_ZUGANGSSTATUS";

	public static final String FIND_BY_UUIDS_QUERY = "PersistenterVeranstalter.FIND_BY_UUIDS";

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
