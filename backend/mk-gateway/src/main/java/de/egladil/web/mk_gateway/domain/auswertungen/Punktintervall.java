// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.auswertungen;

import java.util.Objects;

/**
 * Ein Punktintervall ist ein abgeschlossene Intervall [minVal, maxVal] von Punkten. Es hat einen Startpunkt minVal und
 * in der Regel die Länge 475. Nur im oberen Bereich ist die Länge kürzer. Alle möglichen erreichbaren Punkte in einer
 * Klassenstufe können genau einem Intervall zugeordnet werden. Es entspricht einer Zeile in der Punktverteilung.
 * Punktintervalle werden <b>absteigend</b> sortiert. Gerechnet wird ganzzahlig, also mit dem 100-Fachen der
 * tatsächlichen Punkte.
 */
public class Punktintervall implements Comparable<Punktintervall> {

	public static final int DEFAULT_LAENGE = 475;

	public static final int LAENGE_INKLUSION = 450;

	private final int minVal;

	private final int maxVal;

	public static class Builder {
		// Pflichtattribute
		private final int minVal;

		private int maxVal;

		/**
		 * Erzeugt eine Instanz von Builder
		 */
		public Builder(final int minVal) {

			this.minVal = minVal;
		}

		public Builder maxVal(final int maxVal) {

			this.maxVal = maxVal;
			return this;
		}

		public Builder laenge(final int laenge) {

			if (laenge <= 0) {

				throw new IllegalArgumentException("laenge muss gröesser 0 sein: laenge=" + laenge);
			}
			this.maxVal = this.minVal + laenge;
			return this;
		}

		public Punktintervall build() {

			final Punktintervall result = new Punktintervall(this.minVal, this.maxVal);
			return result;
		}
	}

	/**
	 * Erzeugt eine Instanz von Punktintervall
	 */
	private Punktintervall(final int minVal, final int maxVal) {

		this.minVal = minVal;
		this.maxVal = maxVal;
	}

	/**
	 * @param  punkte
	 * @return        boolean true, wenn punkte dazugehören, false sonst.
	 */
	public boolean contains(final int punkte) {

		return minVal <= punkte && punkte <= maxVal;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Punktintervall o) {

		return minVal - o.getMinVal();
	}

	public int getMinVal() {

		return minVal;
	}

	public int getMaxVal() {

		return maxVal;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder();
		builder.append("[");
		builder.append(minVal);
		builder.append(",");
		builder.append(maxVal);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {

		return Objects.hash(minVal);
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
		Punktintervall other = (Punktintervall) obj;
		return minVal == other.minVal;
	}
}
