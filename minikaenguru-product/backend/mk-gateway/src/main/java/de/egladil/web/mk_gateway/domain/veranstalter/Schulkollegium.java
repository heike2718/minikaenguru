// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;

/**
 * Schulkollegium
 */
@AggregateRoot
public class Schulkollegium {

	@JsonProperty
	private Identifier schulkuerzel;

	@JsonProperty
	private Kollege[] alleLehrer;

	/**
	 * Nur für Tests verwenden!!!!
	 */
	public Schulkollegium() {

	}

	/**
	 * @param schulkuerzel
	 *                     Identifier
	 * @param kollegen
	 *                     Kollege[]
	 */
	public Schulkollegium(final Identifier schulkuerzel, final Kollege[] kollegen) {

		if (schulkuerzel == null) {

			throw new IllegalArgumentException("schulkuerzel darf nicht null sein.");
		}

		if (kollegen == null) {

			throw new IllegalArgumentException("personen darf nicht null sein.");
		}

		this.schulkuerzel = schulkuerzel;
		this.alleLehrer = kollegen;
	}

	public Identifier schulkuerzel() {

		return this.schulkuerzel;
	}

	public List<Kollege> alleLehrerUnmodifiable() {

		return Collections.unmodifiableList(Arrays.asList(this.alleLehrer));
	}

	public String personenAlsJSON() {

		try {

			return new ObjectMapper().writeValueAsString(this.alleLehrer);

		} catch (JsonProcessingException e) {

			throw new MkGatewayRuntimeException("Konnte personen nicht serialisieren: " + e.getMessage(), e);
		}

	}

	@Override
	public int hashCode() {

		return Objects.hash(schulkuerzel);
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
		Schulkollegium other = (Schulkollegium) obj;
		return Objects.equals(schulkuerzel, other.schulkuerzel);
	}
}
