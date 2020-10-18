// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * PrivatkindRequestData
 */
public class PrivatkindRequestData {

	@JsonProperty
	private KindAPIModel kind;

	@JsonProperty
	private TeilnahmeIdentifier teilnahmeIdentifier;

	public KindAPIModel kind() {

		return kind;
	}

	public PrivatkindRequestData withKind(final KindAPIModel kind) {

		this.kind = kind;
		return this;
	}

	public TeilnahmeIdentifier teilnahmeIdentifier() {

		return teilnahmeIdentifier;
	}

	public PrivatkindRequestData withTeilnahmeIdentifier(final TeilnahmeIdentifier teilnahmeIdentifier) {

		this.teilnahmeIdentifier = teilnahmeIdentifier;
		return this;
	}

	@Override
	public int hashCode() {

		return Objects.hash(kind, teilnahmeIdentifier);
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
		PrivatkindRequestData other = (PrivatkindRequestData) obj;
		return Objects.equals(kind, other.kind) && Objects.equals(teilnahmeIdentifier, other.teilnahmeIdentifier);
	}

}
