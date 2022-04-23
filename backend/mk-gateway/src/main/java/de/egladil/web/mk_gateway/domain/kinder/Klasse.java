// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;

/**
 * Klasse
 */
@AggregateRoot
public class Klasse {

	@JsonProperty
	private Identifier identifier;

	@JsonProperty
	private String name;

	@JsonProperty
	private Identifier schuleID;

	@JsonIgnore
	private List<Kind> kinder = new ArrayList<>();

	public Klasse() {

	}

	public Klasse(final Identifier identifier) {

		this.identifier = identifier;
	}

	public String name() {

		return name;
	}

	public Klasse withName(final String name) {

		this.name = name;
		return this;
	}

	public Identifier schuleID() {

		return schuleID;
	}

	public Klasse withSchuleID(final Identifier schuleID) {

		this.schuleID = schuleID;
		return this;
	}

	public Identifier identifier() {

		return identifier;
	}

	public boolean addKind(final Kind kind) {

		this.kinder.add(kind);
		kind.withKlasseID(this.identifier);
		return true;
	}

	public boolean removeKind(final Kind kind) {

		boolean removed = this.kinder.remove(kind);

		if (removed) {

			kind.withKlasseID(null);
		}
		return removed;
	}

	/**
	 * Unmodifiable List!!!!
	 *
	 * @return List
	 */
	public List<Kind> kinder() {

		return Collections.unmodifiableList(kinder);
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
		Klasse other = (Klasse) obj;
		return Objects.equals(identifier, other.identifier);
	}

	@Override
	public String toString() {

		return "Klasse [identifier=" + identifier + ", name=" + name + ", schuleID=" + schuleID + "]";
	}
}
