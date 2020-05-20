// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SchuleAPIModel
 */

public class SchuleAPIModel {

	private static final String KEY_KUERZEL = "kuerzel";

	private static final String KEY_NAME = "name";

	private static final String KEY_ORT = "ort";

	private static final String KEY_LAND = "land";

	private static final String KEY_ANGEMELDET = "aktuellAngemeldet";

	@JsonProperty
	private String kuerzel;

	@JsonProperty
	private String name;

	@JsonProperty
	private String ort;

	@JsonProperty
	private String land;

	@JsonProperty
	private boolean aktuellAngemeldet;

	public SchuleAPIModel() {

	}

	public SchuleAPIModel withAttributes(final Map<String, Object> keyValueMap) {

		this.kuerzel = (String) keyValueMap.get(KEY_KUERZEL);

		if (keyValueMap.get(KEY_NAME) != null) {

			this.name = (String) keyValueMap.get(KEY_NAME);
		}

		if (keyValueMap.get(KEY_ORT) != null) {

			this.ort = (String) keyValueMap.get(KEY_ORT);
		}

		if (keyValueMap.get(KEY_LAND) != null) {

			this.land = (String) keyValueMap.get(KEY_LAND);
		}

		if (keyValueMap.get(KEY_ANGEMELDET) != null) {

			this.aktuellAngemeldet = (boolean) keyValueMap.get(KEY_ANGEMELDET);
		}
		return this;
	}

	public SchuleAPIModel withAngemeldet(final boolean angemeldet) {

		this.aktuellAngemeldet = angemeldet;
		return this;
	}

	public String kuerzel() {

		return kuerzel;
	}

	public boolean aktuellAngemeldet() {

		return aktuellAngemeldet;
	}
}
