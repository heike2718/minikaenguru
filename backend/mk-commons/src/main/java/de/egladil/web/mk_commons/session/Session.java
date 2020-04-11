// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.session;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Session
 */
@Deprecated(forRemoval = true)
public class Session {

	private String sessionId;

	@JsonIgnore
	private long expiresAt;

	private SessionUser user;

	public Session createAnonymous(final String sessionId) {

		Session session = new Session();
		session.user = user;
		return session;
	}

	public static Session create(final String sessionId, final SessionUser user) {

		Session session = new Session();
		session.sessionId = sessionId;
		session.user = user;
		return session;
	}

	public long getExpiresAt() {

		return expiresAt;
	}

	public void setExpiresAt(final long expiresAt) {

		this.expiresAt = expiresAt;
	}

	public String getSessionId() {

		return sessionId;
	}

	public SessionUser getUser() {

		return user;
	}

	/**
	 * In Prod, wo Cookies funktionieren, muss die sessionId im Response-Payload entfernt werden können, da sie über ein Cookie
	 * übertragen wird.
	 */
	public void clearSessionId() {

		this.sessionId = null;
	}

}
