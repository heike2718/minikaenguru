//=====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mkadmin_server.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import de.egladil.web.commons.payload.HateoasPayload;

/**
 * User
 */
@Entity
@Table(name = "USERS")
public class User implements MkadminEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHateoasPayload(final HateoasPayload hateoasPayload) {
	}

	@Override
	public HateoasPayload getHateoasPayload() {
		return null;
	}

}
