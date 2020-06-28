// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.semantik.ValueObject;

/**
 * Identifier ist ein Value-Objekt, das einen Identifier repräsentiert.
 */
@ValueObject
public class Identifier {

	@JsonProperty
	private String identifier;

	Identifier() {

	}

	/**
	 * @param identifier
	 */
	public Identifier(final String identifier) {

		if (StringUtils.isBlank(identifier)) {

			throw new IllegalArgumentException("identifier darf nicht blank sein.");
		}

		this.identifier = identifier;
	}

	public String identifier() {

		return identifier;
	}

	@Override
	public int hashCode() {

		return Objects.hash(identifier);
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
		Identifier other = (Identifier) obj;
		return Objects.equals(identifier, other.identifier);
	}

	@Override
	public String toString() {

		return this.identifier;
	}

}
