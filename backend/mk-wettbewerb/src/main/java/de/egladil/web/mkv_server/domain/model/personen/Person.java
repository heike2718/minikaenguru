// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.domain.model.personen;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Person
 */
public class Person {

	@JsonIgnore
	private final String uuid;

	@JsonProperty
	private final String fullName;

	/**
	 * @param uuid
	 * @param fullName
	 */
	public Person(final String uuid, final String fullName) {

		if (StringUtils.isBlank(uuid)) {

			throw new IllegalArgumentException("uuid darf nicht blank sein.");
		}

		if (StringUtils.isBlank(fullName)) {

			throw new IllegalArgumentException("fullName darf nicht blank sein.");
		}

		this.uuid = uuid;
		this.fullName = fullName;
	}

	public String uuid() {

		return this.uuid;
	}

	public String fullName() {

		return this.fullName;
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
		Person other = (Person) obj;
		return Objects.equals(uuid, other.uuid);
	}

	@Override
	public String toString() {

		return "Person [uuid=" + uuid + ", fullName=" + fullName + "]";
	}

}
