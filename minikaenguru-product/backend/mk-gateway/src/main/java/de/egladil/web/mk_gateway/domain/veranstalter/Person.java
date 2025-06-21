// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;

/**
 * Person
 */
@ValueObject
public class Person {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String fullName;

	@JsonProperty
	private String email;

	/**
	 * Konstruktor für JSON deserialization
	 */
	@SuppressWarnings("unused")
	private Person() {

	}

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

		return "Person [uuid=" + uuid + ", fullName=" + fullName + ", email=" + email + "]";
	}

	public String email() {

		return email;
	}

	public Person withEmail(final String email) {

		this.email = email;
		return this;
	}

}
