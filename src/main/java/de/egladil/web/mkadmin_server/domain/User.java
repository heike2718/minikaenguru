//=====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mkadmin_server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons.payload.HateoasPayload;

/**
 * User
 */
@Entity
@Table(name = "USERS")
public class User implements MkadminEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@NotNull
	@Size(min = 1, max = 36)
	@Column(name = "UUID")
	private String uuid;

	@Version
	@Column(name = "VERSION")
	private int version;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setHateoasPayload(final HateoasPayload hateoasPayload) {
	}

	@Override
	public HateoasPayload getHateoasPayload() {
		return null;
	}

}
