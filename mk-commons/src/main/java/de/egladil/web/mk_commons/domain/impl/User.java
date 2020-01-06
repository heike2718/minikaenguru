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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.domain.enums.MKVRolle;

/**
 * User
 */
@Entity
@Table(name = "USERS")
public class User implements IMkEntity {

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
	private MKVRolle rolle;

	public static User create(final String uuid, final MKVRolle rolle) {

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

	public MKVRolle getRolle() {

		return rolle;
	}

	public void setRolle(final MKVRolle rolle) {

		this.rolle = rolle;
	}

	@Override
	public String toString() {

		return "User [rolle=" + rolle + ", uuid=" + uuid + "]";
	}

}
