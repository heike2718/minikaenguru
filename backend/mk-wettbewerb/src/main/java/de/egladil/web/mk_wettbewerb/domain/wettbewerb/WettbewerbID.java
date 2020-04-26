// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.wettbewerb;

import java.util.Objects;

/**
 * WettbewerbID
 */
public class WettbewerbID {

	private final Integer jahr;

	/**
	 * @param jahr
	 */
	public WettbewerbID(final Integer jahr) {

		if (jahr == null) {

			throw new IllegalArgumentException("jahr darf nicht null sein");
		}

		if (jahr.intValue() < 2005) {

			throw new IllegalArgumentException("jahr muss größer 2004 sein");
		}

		this.jahr = jahr;
	}

	public Integer jahr() {

		return this.jahr;
	}

	@Override
	public int hashCode() {

		return Objects.hash(jahr);
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
		WettbewerbID other = (WettbewerbID) obj;
		return Objects.equals(jahr, other.jahr);
	}

	@Override
	public String toString() {

		return this.jahr.toString();
	}

}
