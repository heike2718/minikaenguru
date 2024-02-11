// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;

/**
 * Newsletter
 */
@AggregateRoot
public class Newsletter {

	@JsonIgnore
	public static final String KEINE_UUID = "neu";

	@JsonProperty
	private Identifier identifier;

	@JsonProperty
	private String betreff;

	@JsonProperty
	private String text;

	@JsonProperty
	private Set<Identifier> idsVersandinformationen;

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
		Newsletter other = (Newsletter) obj;
		return Objects.equals(identifier, other.identifier);
	}

	@Override
	public String toString() {

		return "Newsletter [identifier=" + identifier + ", betreff=" + betreff + ", idsVersandinformationen="
			+ idsVersandinformationen + "]";
	}

	public Identifier identifier() {

		return identifier;
	}

	public Newsletter withIdentifier(final Identifier identifier) {

		if (this.identifier != null && !this.identifier.equals(identifier)) {

			throw new IllegalStateException("identifier darf nicht geändert werden.");
		}
		this.identifier = identifier;
		return this;
	}

	public String betreff() {

		return betreff;
	}

	public Newsletter withBetreff(final String betreff) {

		this.betreff = betreff;
		return this;
	}

	public String text() {

		return text;
	}

	public Newsletter withText(final String text) {

		this.text = text;
		return this;
	}

	/**
	 * @return Collection eine Kopie
	 */
	public Collection<Identifier> idsVersandinformationen() {

		if (this.idsVersandinformationen == null) {

			return new ArrayList<>();
		}

		return idsVersandinformationen.stream().collect(Collectors.toList());
	}

	Newsletter addIdVersandinformation(final Identifier identifier) {

		if (this.idsVersandinformationen == null) {

			this.idsVersandinformationen = new HashSet<>();
		}

		this.idsVersandinformationen.add(identifier);
		return this;
	}

	@JsonIgnore
	public boolean isNeu() {

		return identifier == null || NewsletterAPIModel.KEINE_UUID.equals(identifier.identifier());
	}

}
