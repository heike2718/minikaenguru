// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.enums;

import de.egladil.web.mk_commons.exception.MkRuntimeException;

/**
 * Aufgabenkategorie
 */
public enum Aufgabenkategorie {

	LEICHT("A"),
	MITTEL("B"),
	SCHWER("C");

	private final String nummerPrefix;

	/**
	 * Aufgabenkategorie
	 *
	 * @param nummerPrefix
	 */
	private Aufgabenkategorie(final String nummerPrefix) {

		this.nummerPrefix = nummerPrefix;
	}

	public static Aufgabenkategorie getByNummer(final String nummer) {

		if (nummer == null) {

			throw new IllegalArgumentException("nummer null");
		}

		for (Aufgabenkategorie kategorie : Aufgabenkategorie.values()) {

			if (nummer.startsWith(kategorie.nummerPrefix)) {

				return kategorie;
			}
		}
		throw new MkRuntimeException("fehlerhafte Nummernsyntax '" + nummer + "': nummer muss mit A, B oder C beginnen");
	}

}
