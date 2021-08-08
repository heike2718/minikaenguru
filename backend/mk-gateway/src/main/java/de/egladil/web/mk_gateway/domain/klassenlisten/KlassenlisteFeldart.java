// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

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

	public static String getMessageExpectedContents() {

		List<String> felder = Arrays.stream(KlassenlisteFeldart.values()).map(f -> f.label).collect(Collectors.toList());

		String result = "Die erste Zeile muss folgenden Inhalt in beliebiger Reihenfolge haben: " + StringUtils.join(felder, ',');
		return result;
	}
}
