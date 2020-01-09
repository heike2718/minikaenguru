// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.session;

import java.security.Principal;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * SessionUser. Die idReference dient dazu, Fehler, die auf dem Client auftreten, mit dem Serverlog zu verbinden.
 */
public class SessionUser implements Principal {

	private String idReference;

	private String rolle;

	@JsonIgnore
	private String uuid;

	public static SessionUser create(final String uuid, final String rolle, final String idReference) {

		SessionUser result = new SessionUser();
		result.uuid = uuid;
		result.rolle = rolle;
		return result;
	}

	@Override
	public String getName() {

		return uuid;
	}

	public String getRolle() {

		return rolle;
	}

	public String getUuid() {

		return uuid;
	}

	public String getIdReference() {

		return idReference;
	}

}
