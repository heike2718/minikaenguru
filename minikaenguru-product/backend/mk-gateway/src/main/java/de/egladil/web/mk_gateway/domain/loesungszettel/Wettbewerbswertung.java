// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import java.util.Objects;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;

/**
 * Wettbewerbswertung
 */
@ValueObject
public class Wettbewerbswertung {

	private final int punkte;

	private final int kaengurusprung;

	public Wettbewerbswertung(final int punkte, final int kaengurusprung) {

		this.punkte = punkte;
		this.kaengurusprung = kaengurusprung;
	}

	public int punkte() {

		return punkte;
	}

	public int kaengurusprung() {

		return kaengurusprung;
	}

	@Override
	public String toString() {

		return "Wettbewerbswertung [punkte=" + punkte + ", kaengurusprung=" + kaengurusprung + "]";
	}

	@Override
	public int hashCode() {

		return Objects.hash(kaengurusprung, punkte);
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
		Wettbewerbswertung other = (Wettbewerbswertung) obj;
		return kaengurusprung == other.kaengurusprung && punkte == other.punkte;
	}

}
