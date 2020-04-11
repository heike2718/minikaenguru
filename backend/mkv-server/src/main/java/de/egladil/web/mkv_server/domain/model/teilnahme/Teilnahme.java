// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.domain.model.teilnahme;

import java.util.Objects;

import de.egladil.web.mkv_server.domain.model.wettbewerb.WettbewerbID;

/**
 * Teilnahme
 */
public abstract class Teilnahme {

	private final WettbewerbID wettbewerbID;

	private final Teilnahmekuerzel teilnahmekuerzel;

	/**
	 * @param teilnahmekuerzel
	 * @param jahr
	 */
	public Teilnahme(final WettbewerbID wettbewerbID, final Teilnahmekuerzel teilnahmekuerzel) {

		if (wettbewerbID == null) {

			throw new IllegalArgumentException("wettbewerbID darf nicht null sein");
		}

		if (teilnahmekuerzel == null) {

			throw new IllegalArgumentException("teilnahmekuerzel darf nicht null sein");
		}

		if (!teilnahmekuerzelErlaubt(teilnahmekuerzel)) {

			throw new IllegalArgumentException(teilnahmekuerzel.getClass().getSimpleName() + " ist nicht erlaubt.");
		}

		this.wettbewerbID = wettbewerbID;
		this.teilnahmekuerzel = teilnahmekuerzel;
	}

	/**
	 * @param  teilnahmekuerzel
	 *                          Teilnahmekuerzel
	 * @return                  boolean
	 */
	protected abstract boolean teilnahmekuerzelErlaubt(Teilnahmekuerzel teilnahmekuerzel);

	public WettbewerbID wettbewerbID() {

		return this.wettbewerbID;
	}

	public Teilnahmekuerzel teilnahmekuerzel() {

		return this.teilnahmekuerzel;
	}

	@Override
	public int hashCode() {

		return Objects.hash(teilnahmekuerzel, wettbewerbID);
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
		Teilnahme other = (Teilnahme) obj;
		return Objects.equals(teilnahmekuerzel, other.teilnahmekuerzel) && Objects.equals(wettbewerbID, other.wettbewerbID);
	}

}
