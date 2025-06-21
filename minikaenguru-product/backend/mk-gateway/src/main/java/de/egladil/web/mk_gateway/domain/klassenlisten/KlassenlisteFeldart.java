// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

/**
 * KlassenlisteFeldart
 */
public enum KlassenlisteFeldart {

	KLASSE("Klasse"),
	KLASSENSTUFE("Klassenstufe"),
	NACHNAME("Nachname"),
	VORNAME("Vorname");

	private final String label;

	private KlassenlisteFeldart(final String label) {

		this.label = label;
	}

	/**
	 * Resilientere Implementierung als valueOf(str), da das Excel nicht druckbare Zeichen enthalten kann.
	 *
	 * @param  str
	 * @return     KlassenlisteFeldart
	 */
	public static KlassenlisteFeldart detectFromString(final String str) throws IllegalArgumentException {

		if (str.contains(KLASSENSTUFE.toString())) {

			return KLASSENSTUFE;
		} else {

			for (KlassenlisteFeldart feldart : KlassenlisteFeldart.values()) {

				if (str.contains(feldart.toString())) {

					return feldart;
				}
			}
		}

		throw new IllegalArgumentException(str);
	}

	public String getLabel() {

		return label;
	}
}
