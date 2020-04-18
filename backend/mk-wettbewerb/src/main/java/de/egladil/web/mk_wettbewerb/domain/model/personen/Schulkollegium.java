// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.personen;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.egladil.web.mk_wettbewerb.domain.model.teilnahmen.Schulkuerzel;

/**
 * Schulkollegium
 */
public class Schulkollegium {

	private final Schulkuerzel schulkuerzel;

	private final List<Person> alleLehrerDerSchule;

	/**
	 * @param schulkuerzel
	 * @param alleLehrerDerSchule
	 */
	public Schulkollegium(final Schulkuerzel schulkuerzel, final List<Person> alleLehrerDerSchule) {

		if (schulkuerzel == null) {

			throw new IllegalArgumentException("schulkuerzel darf nicht null sein.");
		}

		if (alleLehrerDerSchule == null) {

			throw new IllegalArgumentException("alleLehrerDerSchule darf nicht null sein.");
		}

		this.schulkuerzel = schulkuerzel;
		this.alleLehrerDerSchule = alleLehrerDerSchule;
	}

	public Schulkuerzel schulkuerzel() {

		return this.schulkuerzel;
	}

	public List<Person> alleLehrerDerSchule() {

		return Collections.unmodifiableList(this.alleLehrerDerSchule);
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
