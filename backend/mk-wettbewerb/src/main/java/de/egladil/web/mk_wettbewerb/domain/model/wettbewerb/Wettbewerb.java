// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.wettbewerb;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Objects;

/**
 * Wettbewerb
 */
public class Wettbewerb {

	private final WettbewerbID wettbewerbId;

	private LocalDate wettbewerbsbeginn;

	private final LocalDate wettbewerbsende;

	private LocalDate datumFreischaltungLehrer;

	private LocalDate datumFreischaltungPrivat;

	/**
	 * @param wettbewerbId
	 */
	public Wettbewerb(final WettbewerbID wettbewerbId) {

		if (wettbewerbId == null) {

			throw new IllegalArgumentException("wettbewerbId darf nicht null sein.");
		}

		this.wettbewerbId = wettbewerbId;

		this.wettbewerbsende = LocalDate.of(wettbewerbId.jahr(), Month.AUGUST, 1);
	}

	@Override
	public int hashCode() {

		return Objects.hash(wettbewerbId);
	}

	/**
	 * Entscheidet, ob der Wettbewerb zum gegebenen Zeitpunkt aktiv ist.
	 *
	 * @param  zeitpunkt
	 * @return
	 */
	public boolean istAktivZumZeitpunkt(final LocalDateTime zeitpunkt) {

		// true, falls jetzt
		return false;

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
		Wettbewerb other = (Wettbewerb) obj;
		return Objects.equals(wettbewerbId, other.wettbewerbId);
	}

	@Override
	public String toString() {

		return this.wettbewerbId.jahr().toString();
	}

}
