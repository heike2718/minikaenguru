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

	private static final String KEY_DETAILS = "details";

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

	@JsonProperty
	private SchuleWettbewerbeDetails details;

	SchuleAPIModel() {

	}

	public static SchuleAPIModel withAttributes(final Map<String, Object> keyValueMap) {

		SchuleAPIModel result = new SchuleAPIModel();

		result.kuerzel = (String) keyValueMap.get(KEY_KUERZEL);

		if (keyValueMap.get(KEY_NAME) != null) {

			result.name = (String) keyValueMap.get(KEY_NAME);
		}

		if (keyValueMap.get(KEY_ORT) != null) {

			result.ort = (String) keyValueMap.get(KEY_ORT);
		}

		if (keyValueMap.get(KEY_LAND) != null) {

			result.land = (String) keyValueMap.get(KEY_LAND);
		}

		if (keyValueMap.get(KEY_ANGEMELDET) != null) {

			result.aktuellAngemeldet = (boolean) keyValueMap.get(KEY_ANGEMELDET);
		}

		if (keyValueMap.get(KEY_DETAILS) != null) {

			@SuppressWarnings("unchecked")
			Map<String, Object> detailsMap = (Map<String, Object>) keyValueMap.get(KEY_DETAILS);

			result.details = SchuleWettbewerbeDetails.withAttributes(detailsMap);
		}

		return result;
	}

	public SchuleAPIModel withAngemeldet(final boolean angemeldet) {

		this.aktuellAngemeldet = angemeldet;
		return this;
	}

	public SchuleAPIModel withDetails(final SchuleWettbewerbeDetails details) {

		this.details = details;
		return this;
	}

	public String kuerzel() {

		return kuerzel;
	}

	public boolean aktuellAngemeldet() {

		return aktuellAngemeldet;
	}

	/**
	 * @param  schuleAusKatalog
	 * @param  schuleAusWettbewerbAPI
	 * @return
	 */
	public static SchuleAPIModel merge(final SchuleAPIModel schuleAusKatalog, final SchuleAPIModel schuleAusWettbewerbAPI) {

		SchuleAPIModel result = new SchuleAPIModel();

		result.kuerzel = schuleAusKatalog.kuerzel;
		result.name = schuleAusKatalog.name;
		result.ort = schuleAusKatalog.ort;
		result.land = schuleAusKatalog.land;

		result.aktuellAngemeldet = schuleAusWettbewerbAPI.aktuellAngemeldet;
		result.details = schuleAusWettbewerbAPI.details;
		return result;
	}
}
