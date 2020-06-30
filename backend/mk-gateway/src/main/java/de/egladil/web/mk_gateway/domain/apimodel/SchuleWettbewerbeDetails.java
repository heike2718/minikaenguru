// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;

/**
 * SchuleWettbewerbeDetails
 */
@ValueObject
public class SchuleWettbewerbeDetails {

	private static final String KEY_KUERZEL = "kuerzel";

	private static final String KEY_NAME_URKUNDE = "nameUrkunde";

	private static final String KEY_KOLLEGEN = "kollegen";

	private static final String KEY_ANGEMELDET_DURCH = "angemeldetDurch";

	private static final String KEY_ANZAHL_VERGANGENE_TEILNAHMEN = "anzahlVergangeneTeilnahmen";

	@JsonProperty
	private String kuerzel;

	@JsonProperty
	private String nameUrkunde;

	@JsonProperty
	private String kollegen;

	@JsonProperty
	private String angemeldetDurch;

	@JsonProperty
	private int anzahlVergangeneTeilnahmen = 0;

	@JsonProperty
	private boolean vergangeneTeilnahmenGeladen;

	@JsonProperty
	private List<AnonymisierteTeilnahmeAPIModel> vergangeneTeilnahmen;

	SchuleWettbewerbeDetails() {

	}

	public static SchuleWettbewerbeDetails withAttributes(final Map<String, Object> keyValueMap) {

		SchuleWettbewerbeDetails result = new SchuleWettbewerbeDetails();

		result.kuerzel = (String) keyValueMap.get(KEY_KUERZEL);

		if (keyValueMap.get(KEY_NAME_URKUNDE) != null) {

			result.nameUrkunde = (String) keyValueMap.get(KEY_NAME_URKUNDE);
		}

		if (keyValueMap.get(KEY_KOLLEGEN) != null) {

			result.kollegen = (String) keyValueMap.get(KEY_KOLLEGEN);
		}

		if (keyValueMap.get(KEY_ANGEMELDET_DURCH) != null) {

			result.angemeldetDurch = (String) keyValueMap.get(KEY_ANGEMELDET_DURCH);
		}

		if (keyValueMap.get(KEY_ANZAHL_VERGANGENE_TEILNAHMEN) != null) {

			result.anzahlVergangeneTeilnahmen = (Integer) keyValueMap.get(KEY_ANZAHL_VERGANGENE_TEILNAHMEN);
		}

		return result;

	}

	public String kuerzel() {

		return kuerzel;
	}

	public String nameUrkunde() {

		return nameUrkunde;
	}

	public String kollegen() {

		return kollegen;
	}

	public String angemeldetDurch() {

		return angemeldetDurch;
	}

	public boolean vergangeneTeilnahmenGeladen() {

		return vergangeneTeilnahmenGeladen;
	}

	public List<AnonymisierteTeilnahmeAPIModel> vergangeneTeilnahmen() {

		return vergangeneTeilnahmen;
	}

	public int anzahlVergangeneTeilnahmen() {

		return anzahlVergangeneTeilnahmen;
	}

}
