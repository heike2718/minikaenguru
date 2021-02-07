// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.Arrays;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * Aufgabenkategorie
 */
public enum Aufgabenkategorie {

	LEICHT("A") {

		@Override
		public int getPenalty(final Klassenstufe klassenstufe) {

			return klassenstufe == Klassenstufe.IKID ? 150 : 75; // 3 Antwortmöglichkeiten als Hälfte der Punkte Abzug
		}

		@Override
		public int getPunkte() {

			return 300;
		}

	},
	MITTEL("B") {

		@Override
		public int getPenalty(final Klassenstufe klassenstufe) {

			return klassenstufe == Klassenstufe.IKID ? 200 : 100; // 3 Antwortmöglichkeiten als Hälfte der Punkte Abzug
		}

		@Override
		public int getPunkte() {

			return 400;
		}

	},
	SCHWER("C") {

		@Override
		public int getPenalty(final Klassenstufe klassenstufe) {

			return klassenstufe == Klassenstufe.IKID ? 250 : 125; // 3 Antwortmöglichkeiten als Hälfte der Punkte Abzug
		}

		@Override
		public int getPunkte() {

			return 500;
		}

	};

	private final String nummerPrefix;

	private Aufgabenkategorie(final String nummerPrefix) {

		this.nummerPrefix = nummerPrefix;
	}

	public abstract int getPenalty(Klassenstufe klassenstufe);

	public abstract int getPunkte();

	public static Aufgabenkategorie valueOfNummer(final String nummer) {

		if (nummer == null) {

			throw new IllegalArgumentException("nummer null");
		}

		Optional<Aufgabenkategorie> optKategorie = Arrays.stream(Aufgabenkategorie.values())
			.filter(k -> nummer.toUpperCase().startsWith(k.nummerPrefix)).findFirst();

		if (optKategorie.isPresent()) {

			return optKategorie.get();
		}

		throw new MkGatewayRuntimeException("fehlerhafte Nummernsyntax '" + nummer + "': nummer muss mit A, B oder C beginnen");
	}

}
