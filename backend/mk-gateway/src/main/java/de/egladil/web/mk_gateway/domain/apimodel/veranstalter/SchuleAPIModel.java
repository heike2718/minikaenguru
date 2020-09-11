// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.veranstalter;

import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;

/**
 * SchuleAPIModel
 */
@ValueObject
public class SchuleAPIModel {

	private static final String KEY_KUERZEL = "kuerzel";

	private static final String KEY_NAME = "name";

	private static final String KEY_ORT = "ort";

	private static final String KEY_LAND = "land";

	private static final String KEY_KUERZEL_LAND = "kuerzelLand";

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
	private String kuerzelLand;

	@JsonProperty
	private boolean aktuellAngemeldet;

	@JsonProperty
	private SchuleDetails details;

	SchuleAPIModel() {

	}

	public static SchuleAPIModel withKuerzelLand(final String kuerzelLand) {

		SchuleAPIModel result = new SchuleAPIModel();
		result.kuerzelLand = kuerzelLand;
		return result;
	}

	public static SchuleAPIModel withKuerzel(final String kuerzel) {

		SchuleAPIModel result = new SchuleAPIModel();
		result.kuerzel = kuerzel;
		return result;
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

		if (keyValueMap.get(KEY_KUERZEL_LAND) != null) {

			result.kuerzelLand = (String) keyValueMap.get(KEY_KUERZEL_LAND);
		}

		if (keyValueMap.get(KEY_ANGEMELDET) != null) {

			result.aktuellAngemeldet = (boolean) keyValueMap.get(KEY_ANGEMELDET);
		}

		return result;
	}

	public SchuleAPIModel withAngemeldet(final boolean angemeldet) {

		this.aktuellAngemeldet = angemeldet;
		return this;
	}

	public SchuleAPIModel withDetails(final SchuleDetails details) {

		this.details = details;
		return this;
	}

	/**
	 * @param  schuleAusKatalog
	 * @param  schuleAusWettbewerbAPI
	 * @return
	 */
	public static SchuleAPIModel merge(final SchuleAPIModel schuleAusKatalog, final SchuleDetails schuleAusWettbewerbAPI) {

		SchuleAPIModel result = new SchuleAPIModel();

		result.kuerzel = schuleAusKatalog.kuerzel;
		result.name = schuleAusKatalog.name;
		result.ort = schuleAusKatalog.ort;
		result.land = schuleAusKatalog.land;

		result.aktuellAngemeldet = schuleAusWettbewerbAPI.aktuelleTeilnahme() != null;
		result.details = schuleAusWettbewerbAPI;
		return result;
	}

	public SchuleAPIModel markKatalogeintragUnknown() {

		name = "unbekannter Schulname";
		ort = "unbekannter Ort";
		land = "unbekanntes Land / Bundesland";
		return this;
	}

	public String kuerzel() {

		return kuerzel;
	}

	public boolean aktuellAngemeldet() {

		return aktuellAngemeldet;
	}

	public String name() {

		return name;
	}

	public String ort() {

		return ort;
	}

	public String land() {

		return land;
	}

	public String kuerzelLand() {

		return kuerzelLand;
	}

	public SchuleDetails getDetails() {

		return details;
	}

	@Override
	public int hashCode() {

		return Objects.hash(kuerzel);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		SchuleAPIModel other = (SchuleAPIModel) obj;
		return Objects.equals(kuerzel, other.kuerzel);
	}

	@Override
	public String toString() {

		return "SchuleAPIModel [kuerzel=" + kuerzel + "]";
	}
}
