// =====================================================
// Project: mkv-api-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_api_gateway.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import de.egladil.web.mkv_api_gateway.domain.model.Rolle;

/**
 * User
 */
@Entity
@Table(name = "USERS")
@NamedQueries({
	@NamedQuery(name = "FIND_BY_UUID", query = "select u from User u where u.uuid = :uuid")
})
public class User extends ConcurrencySafeEntity {

	@Column(name = "ROLE")
	@Enumerated(EnumType.STRING)
	private Rolle rolle;

	public Rolle getRolle() {

		return rolle;
	}

	public void setRolle(final Rolle rolle) {

		this.rolle = rolle;
	}
}
