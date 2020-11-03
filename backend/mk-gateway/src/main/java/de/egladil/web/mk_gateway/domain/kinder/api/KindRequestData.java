// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.UuidString;

/**
 * KindRequestData
 */
public class KindRequestData implements Serializable {

	@JsonIgnore
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	public static final String KEINE_UUID = "neu";

	@JsonProperty
	@UuidString
	@NotNull
	private String uuid;

	@JsonProperty
	@UuidString
	private String klasseUuid;

	@JsonProperty
	@NotNull
	private KindEditorModel kind;

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

	public String klasseUuid() {

		return klasseUuid;
	}

	public KindRequestData withKlasseUuid(final String klasseUuid) {

		this.klasseUuid = klasseUuid;
		return this;
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

}
