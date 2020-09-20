// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.statistik;

/**
 * Wertung der Lösung (falsch, richtig, nicht da)
 */
public enum Wertung {
	f,
	n,
	r;

	public static Wertung valueOfStringIgnoringCase(final String str) {

		if (str == null) {

			throw new IllegalArgumentException("str darf nicht null sein");
		}
		return Wertung.valueOf(str.toLowerCase());
	}

}
