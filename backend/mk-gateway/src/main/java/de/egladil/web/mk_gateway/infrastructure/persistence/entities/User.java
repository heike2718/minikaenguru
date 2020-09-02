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
import javax.persistence.Version;

import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * User
 */
@Entity
@Table(name = "USERS")
@NamedQueries({
	@NamedQuery(name = "FIND_BY_UUID", query = "select u from User u where u.uuid = :uuid")
})
public class User extends ConcurrencySafeEntity {

	public static final String FIND_USER_BY_UUID_QUERY = "FIND_BY_UUID";

	@Column(name = "ROLE")
	@Enumerated(EnumType.STRING)
	private Rolle rolle;

	@Version
	@Column(name = "VERSION")
	private int version;

	public Rolle getRolle() {

		return rolle;
	}

	public void setRolle(final Rolle rolle) {

		this.rolle = rolle;
	}

	@Override
	public String toString() {

		return "User [uuid=" + getUuid() + ", rolle=" + rolle + "]";
	}
}
