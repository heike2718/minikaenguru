// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * User
 */
@Entity
@Table(name = "USERS")
public class User extends ConcurrencySafeEntity {

	private static final long serialVersionUID = -1456196031075014971L;

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
