// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.domain.impl;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.domain.enums.Rolle;

/**
 * User
 */
@Entity
@Table(name = "USERS")
@NamedQueries({
	@NamedQuery(name = "FIND_BY_UUID", query = "select u from User u where u.uuid = :uuid")
})
public class User implements IMkEntity {

	public static final String FIND_BY_UUID = "FIND_BY_UUID";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@UuidString
	@NotNull
	@Size(min = 1, max = 40)
	@Column(name = "UUID")
	private String uuid;

	@Column(name = "ROLE")
	@Enumerated(EnumType.STRING)
	private Rolle rolle;

	public static User create(final String uuid, final Rolle rolle) {

		User user = new User();
		user.uuid = uuid;
		user.rolle = rolle;
		return user;

	}

	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	public String getUuid() {

		return uuid;
	}

	public void setUuid(final String uuid) {

		this.uuid = uuid;
	}

	public Rolle getRolle() {

		return rolle;
	}

	public void setRolle(final Rolle rolle) {

		this.rolle = rolle;
	}

	@Override
	public String toString() {

		return "User [rolle=" + rolle + ", uuid=" + uuid + "]";
	}

}
