// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_api_gateway.domain.session;

import java.security.Principal;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mkv_api_gateway.domain.model.Rolle;

/**
 * LoggedInUser
 */
public class LoggedInUser implements Principal {

	@JsonProperty
	private String idReference;

	@JsonProperty
	private Rolle rolle;

	@JsonProperty
	private String fullName;

	@JsonIgnore
	private String uuid;

	public static LoggedInUser create(final String uuid, final Rolle rolle, final String fullName, final String idReference) {

		if (StringUtils.isBlank(uuid)) {

			throw new IllegalArgumentException("uuid darf nicht blank sein");
		}

		if (StringUtils.isBlank(fullName)) {

			throw new IllegalArgumentException("fullName darf nicht blank sein");
		}

		if (rolle == null) {

			throw new IllegalArgumentException("rolle darf nicht null sein");
		}

		LoggedInUser result = new LoggedInUser();
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

	public Rolle rolle() {

		return rolle;
	}

	public String uuid() {

		return uuid;
	}

	public String idReference() {

		return idReference;
	}

	public String fullName() {

		return fullName;
	}

	@Override
	public int hashCode() {

		return Objects.hash(uuid);
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
		LoggedInUser other = (LoggedInUser) obj;
		return Objects.equals(uuid, other.uuid);
	}

	@Override
	public String toString() {

		return "LoggedInUser [uuid=" + uuid + ", rolle=" + rolle + ", fullName=" + fullName + "]";
	}
}
