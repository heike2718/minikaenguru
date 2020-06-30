// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AnonymisierteTeilnahmeAPIModel
 */
public class AnonymisierteTeilnahmeAPIModel {

	private static final String KEY_JAHR = "jahr";

	private static final String KEY_TEILNAHMENUMMER = "teilnahmenummer";

	private static final String KEY_TEILNAHMEART = "teilnahmeart";

	private static final String KEY_ANZAHL_KINDER = "anzahlKinder";

	@JsonProperty
	private int jahr;

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private String teilnahmeart;

	@JsonProperty
	private int anzahlKinder;

	AnonymisierteTeilnahmeAPIModel() {

	}

	public static AnonymisierteTeilnahmeAPIModel withAttributes(final Map<String, Object> keyValueMap) {

		AnonymisierteTeilnahmeAPIModel result = new AnonymisierteTeilnahmeAPIModel();

		if (keyValueMap.get(KEY_JAHR) != null) {

			result.jahr = (Integer) keyValueMap.get(KEY_JAHR);

		}

		if (keyValueMap.get(KEY_TEILNAHMENUMMER) != null) {

			result.teilnahmenummer = (String) keyValueMap.get(KEY_TEILNAHMENUMMER);

		}

		if (keyValueMap.get(KEY_TEILNAHMEART) != null) {

			result.teilnahmeart = (String) keyValueMap.get(KEY_TEILNAHMEART);

		}

		if (keyValueMap.get(KEY_ANZAHL_KINDER) != null) {

			result.anzahlKinder = (Integer) keyValueMap.get(KEY_ANZAHL_KINDER);

		}

		return result;
	}

	public int jahr() {

		return jahr;
	}

	public String teilnahmenummer() {

		return teilnahmenummer;
	}

	public String teilnahmeart() {

		return teilnahmeart;
	}

	public int anzahlKinder() {

		return anzahlKinder;
	}
}
