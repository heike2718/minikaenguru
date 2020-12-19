// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;

/**
 * Mustertext
 */
@AggregateRoot
public class Mustertext {

	@JsonProperty
	private Identifier identifier;

	@JsonProperty
	private String name;

	@JsonProperty
	private String text;

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
		Mustertext other = (Mustertext) obj;
		return Objects.equals(identifier, other.identifier);
	}

	@Override
	public String toString() {

		return "Mustertext [identifier=" + identifier + ", name=" + name + "]";
	}

	public Identifier getIdentifier() {

		return identifier;
	}

	public void setIdentifier(final Identifier identifier) {

		this.identifier = identifier;
	}

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	public String getText() {

		return text;
	}

	public void setText(final String text) {

		this.text = text;
	}

}
