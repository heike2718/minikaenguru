// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.enums;

/**
 * Antwortbuchstabe ist das, was mittels Formular für ein Kind erfasst wird. N bedeutet, Aufgabe weggelassen.
 */
public enum Antwortbuchstabe {

	N(0),
	A(1),
	B(2),
	C(3),
	D(4),
	E(5);

	private final int nummer;

	/**
	 * Erzeugt eine Instanz von Antwortbuchstabe
	 */
	private Antwortbuchstabe(final int nummer) {

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

	public static Antwortbuchstabe valueOfSingleLetter(final String letter) {

		if (letter == null) {

			throw new IllegalArgumentException("letter null");
		}

		if ("-".equals(letter)) {

			return Antwortbuchstabe.N;
		}
		return Antwortbuchstabe.valueOf(letter.toUpperCase());
	}

	/**
	 * Mapping zwischen Nummern und den Buchstaben.
	 *
	 * @param  nummer
	 * @return        Antwortbuchstabe
	 */
	public static Antwortbuchstabe valueOfNummer(final int nummer) {

		switch (nummer) {

		case 0:
			return N;

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
			throw new IllegalArgumentException("Nur Nummern 0-5 erlaubt!");
		}
	}
}
