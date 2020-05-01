// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import java.util.Objects;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;

/**
 * Teilnahme
 */
public abstract class Teilnahme {

	private final WettbewerbID wettbewerbID;

	private final Identifier teilnahmekuerzel;

	/**
	 * @param teilnahmekuerzel
	 * @param jahr
	 */
	public Teilnahme(final WettbewerbID wettbewerbID, final Identifier teilnahmekuerzel) {

		if (wettbewerbID == null) {

			throw new IllegalArgumentException("wettbewerbID darf nicht null sein");
		}

		if (teilnahmekuerzel == null) {

			throw new IllegalArgumentException("teilnahmekuerzel darf nicht null sein");
		}

		this.wettbewerbID = wettbewerbID;
		this.teilnahmekuerzel = teilnahmekuerzel;
	}

	public WettbewerbID wettbewerbID() {

		return this.wettbewerbID;
	}

	public Identifier teilnahmekuerzel() {

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
