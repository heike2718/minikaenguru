// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import java.util.Objects;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * Teilnahme
 */
@AggregateRoot
public abstract class Teilnahme {

	private final WettbewerbID wettbewerbID;

	private final Identifier teilnahmenummer;

	/**
	 * @param teilnahmenummer
	 * @param jahr
	 */
	public Teilnahme(final WettbewerbID wettbewerbID, final Identifier teilnahmekuerzel) {

		if (wettbewerbID == null) {

			throw new IllegalArgumentException("wettbewerbID darf nicht null sein");
		}

		if (teilnahmekuerzel == null) {

			throw new IllegalArgumentException("teilnahmenummer darf nicht null sein");
		}

		this.wettbewerbID = wettbewerbID;
		this.teilnahmenummer = teilnahmekuerzel;
	}

	/**
	 * @return Teilnahmeart
	 */
	public abstract Teilnahmeart teilnahmeart();

	public WettbewerbID wettbewerbID() {

		return this.wettbewerbID;
	}

	public Identifier teilnahmenummer() {

		return this.teilnahmenummer;
	}

	@Override
	public int hashCode() {

		return Objects.hash(teilnahmenummer, wettbewerbID);
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
		return Objects.equals(teilnahmenummer, other.teilnahmenummer) && Objects.equals(wettbewerbID, other.wettbewerbID);
	}
}
