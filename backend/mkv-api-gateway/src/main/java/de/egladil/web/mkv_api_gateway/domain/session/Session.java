// =====================================================
// Project: mkv-api-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_api_gateway.domain.session;

import java.util.Objects;

/**
 * Session
 */
public class Session {

	private String sessionId;

	private long expiresAt;

	private LoggedInUser user;

	public Session createAnonymous(final String sessionId) {

		Session session = new Session();
		session.user = user;
		return session;
	}

	public static Session create(final String sessionId, final LoggedInUser user) {

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

	public String sessionId() {

		return sessionId;
	}

	public LoggedInUser user() {

		return user;
	}

	/**
	 * In Prod, wo Cookies funktionieren, muss die sessionId im Response-Payload entfernt werden können, da sie über ein Cookie
	 * übertragen wird.
	 */
	public void clearSessionId() {

		this.sessionId = null;
	}

	@Override
	public int hashCode() {

		return Objects.hash(sessionId);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		Session other = (Session) obj;
		return Objects.equals(sessionId, other.sessionId);
	}

}
