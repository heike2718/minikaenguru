// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.Arrays;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * Aufgabenkategorie
 */
public enum Aufgabenkategorie {

	LEICHT("A"),
	MITTEL("B"),
	SCHWER("C");

	private final String nummerPrefix;

	private Aufgabenkategorie(final String nummerPrefix) {

		this.nummerPrefix = nummerPrefix;
	}

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
