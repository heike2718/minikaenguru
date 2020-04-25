// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_wettbewerb.domain.model.personen.Rolle;

/**
 * PersistenterVeranstalter
 */
@Entity
@Table(name = "VERANSTALTER")
@NamedQueries(@NamedQuery(
	name = "FIND_VERANSTALTER_BY_UUID", query = "select v from PersistenterVeranstalter v where v.uuid = :uuid"))
public class PersistenterVeranstalter extends ConcurrencySafeEntity {

	public static final String FIND_BY_UUID_QUERY = "FIND_VERANSTALTER_BY_UUID";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid_generator")
	@GenericGenerator(
		name = "uuid_generator", strategy = "de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.UuidGenerator")
	@UuidString
	@NotNull
	@Size(min = 1, max = 40)
	@Column(name = "UUID")
	private String uuid;

	@Column(name = "ROLLE")
	@Enumerated(EnumType.STRING)
	private Rolle rolle;

	@NotNull
	@Size(min = 1, max = 101)
	@Column(name = "FULL_NAME")
	private String fullName;

	@Column(name = "TEILNAHMEKUERZEL")
	private String teilnahmekuerzel;

	@Version
	@Column(name = "VERSION")
	private int version;

	public String getUuid() {

		return uuid;
	}

	public void setUuid(final String uuid) {

		this.uuid = uuid;
	}

	public String getTeilnahmekuerzel() {

		return teilnahmekuerzel;
	}

	public void setTeilnahmekuerzel(final String teilnahmekuerzel) {

		this.teilnahmekuerzel = teilnahmekuerzel;
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
}
