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

	private String fullName;

	// Die Mailadresse wird nur auf dem Server benötigt und nur für wenige Aktionen
	@JsonIgnore
	private String email;

	@JsonIgnore
	private String uuid;

	public static SessionUser create(final String uuid, final String rolle, final String fullName, final String idReference) {

		SessionUser result = new SessionUser();
		result.uuid = uuid;
		result.rolle = rolle;
		result.fullName = fullName;
		result.idReference = idReference;
		return result;
	}

	@JsonIgnore
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

	public String getFullName() {

		return fullName;
	}

	public void setFullName(final String fullName) {

		this.fullName = fullName;
	}

	public String getEmail() {

		return email;
	}

	public void setEmail(final String email) {

		this.email = email;
	}

}
