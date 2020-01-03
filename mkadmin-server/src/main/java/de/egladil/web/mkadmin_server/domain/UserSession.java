// =====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkadmin_server.domain;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * UserSession
 */
public class UserSession {

	private String sessionId;

	@JsonIgnore
	private String uuid;

	private String idReference;

	private long expiresAt;

	public static UserSession create(final String sessionId, final String idReference, final String uuid) {

		UserSession result = new UserSession();

		result.sessionId = sessionId;
		result.idReference = idReference;
		result.uuid = uuid;
		return result;
	}

	public String getSessionId() {

		return sessionId;
	}

	public String getUuid() {

		return uuid;
	}

	public String getIdReference() {

		return idReference;
	}

	public long getExpiresAt() {

		return expiresAt;
	}

	public void setExpiresAt(final long expiresAt) {

		this.expiresAt = expiresAt;
	}

	public void clearSessionId() {

		this.sessionId = null;
	}

	@Override
	public String toString() {

		return "UserSession [uuid=" + StringUtils.abbreviate(uuid, 11) + ", expiresAt=" + expiresAt + "]";
	}

}
