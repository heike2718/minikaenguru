// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.enums;

/**
 * Antwortbuchstabe
 */
public enum Loesungsbuchstabe {

	A(1),
	B(2),
	C(3),
	D(4),
	E(5);

	private final int nummer;

	/**
	 * Erzeugt eine Instanz von Antwortbuchstabe
	 */
	private Loesungsbuchstabe(final int nummer) {

		this.nummer = nummer;
	}

	/**
	 * Liefert die Membervariable nummer
	 *
	 * @return die Membervariable nummer
	 */
	public int getNummer() {

		return nummer;
	}

	/**
	 * Mapping zwischen Nummern und den Buchstaben.
	 *
	 * @param  nummer
	 * @return        Antwortbuchstabe
	 */
	public static Loesungsbuchstabe valueOfNummer(final int nummer) {

		switch (nummer) {

		case 1:
			return A;

		case 2:
			return B;

		case 3:
			return C;

		case 4:
			return D;

		case 5:
			return E;

		default:
			throw new IllegalArgumentException("Nur Nummern 1-5 erlaubt!");
		}
	}
}
