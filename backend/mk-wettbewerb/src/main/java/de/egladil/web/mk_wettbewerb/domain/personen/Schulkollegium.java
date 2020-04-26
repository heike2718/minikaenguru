// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.error.MkWettbewerbRuntimeException;
import de.egladil.web.mk_wettbewerb.domain.semantik.Aggregate;

/**
 * Schulkollegium
 */
@Aggregate
public class Schulkollegium {

	@JsonProperty
	private final Identifier schulkuerzel;

	@JsonProperty
	private final Person[] alleLehrer;

	/**
	 * @param schulkuerzel
	 *                     Identifier
	 * @param personen
	 *                     Person[]
	 */
	public Schulkollegium(final Identifier schulkuerzel, final Person[] personen) {

		if (schulkuerzel == null) {

			throw new IllegalArgumentException("schulkuerzel darf nicht null sein.");
		}

		if (personen == null) {

			throw new IllegalArgumentException("personen darf nicht null sein.");
		}

		this.schulkuerzel = schulkuerzel;
		this.alleLehrer = personen;
	}

	public Identifier schulkuerzel() {

		return this.schulkuerzel;
	}

	public List<Person> alleLehrerUnmodifiable() {

		return Collections.unmodifiableList(Arrays.asList(this.alleLehrer));
	}

	public String personenAlsJSON() {

		try {

			return new ObjectMapper().writeValueAsString(this.alleLehrer);

		} catch (JsonProcessingException e) {

			throw new MkWettbewerbRuntimeException("Konnte personen nicht serialisieren: " + e.getMessage(), e);
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
