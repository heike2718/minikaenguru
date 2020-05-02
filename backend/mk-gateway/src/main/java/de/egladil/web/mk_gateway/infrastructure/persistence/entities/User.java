// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.egladil.web.commons_validation.annotations.UuidString;
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

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid_generator")
	@GenericGenerator(
		name = "uuid_generator", strategy = "de.egladil.web.mk_gateway.infrastructure.persistence.entities.UuidGenerator")
	@UuidString
	@NotNull
	@Size(min = 1, max = 40)
	@Column(name = "UUID")
	@JsonIgnore
	private String uuid;

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

	public String getUuid() {

		return uuid;
	}

	public void setUuid(final String uuid) {

		this.uuid = uuid;
	}
}
