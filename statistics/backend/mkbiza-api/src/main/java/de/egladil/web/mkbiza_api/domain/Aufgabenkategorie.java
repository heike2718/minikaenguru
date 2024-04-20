// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain;

/**
 * Aufgabenkategorie
 */
public enum Aufgabenkategorie {

	A {

		@Override
		public int getPunkte() {

			return 3;
		}

		@Override
		public double getLowerBoundOfCorrectAnswers() {

			return 80.00;
		}

		@Override
		public double getUpperOfCorrectAnswers() {

			return 100.00;
		}

	},
	B {

		@Override
		public int getPunkte() {

			return 4;
		}

		@Override
		public double getLowerBoundOfCorrectAnswers() {

			return 30.00;
		}

		@Override
		public double getUpperOfCorrectAnswers() {

			return 70.00;
		}

	},
	C {

		@Override
		public int getPunkte() {

			return 5;
		}

		@Override
		public double getLowerBoundOfCorrectAnswers() {

			return 0;
		}

		@Override
		public double getUpperOfCorrectAnswers() {

			return 20.00;
		}

	};

	public abstract int getPunkte();

	public abstract double getLowerBoundOfCorrectAnswers();

	public abstract double getUpperOfCorrectAnswers();

	public static Aufgabenkategorie valueOfNummer(final String nummer) {

		String kategorie = nummer.substring(0, 1);

		switch (kategorie) {

		case "A":
			return A;

		case "B":
			return B;

		case "C":
			return C;

		default:
			throw new IllegalArgumentException("keine Aufgabenkategorie zu nummer " + nummer + " bekannt.");
		}
	}

}
