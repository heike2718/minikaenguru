// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import java.util.Objects;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.UuidString;

/**
 * KindRequestData
 */
public class KindRequestData {

	@JsonIgnore
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	public static final String KEINE_UUID = "neu";

	@JsonProperty
	@UuidString
	@NotNull
	private String uuid;

	@JsonProperty
	@Kuerzel
	private String kuerzelLand;

	@JsonProperty
	@NotNull
	private KindEditorModel kind;

	@Override
	public String toString() {

		return "uuid=" + uuid + ", kuerzelLand=" + kuerzelLand + ", kind=" + kind;
	}

	public KindEditorModel kind() {

		return kind;
	}

	public KindRequestData withKind(final KindEditorModel kind) {

		this.kind = kind;
		return this;
	}

	public String uuid() {

		return uuid;
	}

	public KindRequestData withUuid(final String uuid) {

		this.uuid = uuid;
		return this;
	}

	public String kuerzelLand() {

		return kuerzelLand;
	}

	public KindRequestData withKuerzelLand(final String kuerzelLand) {

		this.kuerzelLand = kuerzelLand;
		return this;
	}

	/**
	 * @return
	 */
	public String klasseUuid() {

		return this.kind.klasseUuid();
	}

	@Override
	public int hashCode() {

		return Objects.hash(kind, uuid);
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
		KindRequestData other = (KindRequestData) obj;
		return Objects.equals(kind, other.kind) && Objects.equals(uuid, other.uuid);
	}

	public String logData() {

		return this.kind.logData();
	}
}
