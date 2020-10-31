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
 * PrivatkindRequestData
 */
public class PrivatkindRequestData implements Serializable {

	@JsonIgnore
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	public static final String KEINE_UUID = "neu";

	@JsonProperty
	@UuidString
	@NotNull
	private String uuid;

	@JsonProperty
	@NotNull
	private KindEditorModel kind;

	public KindEditorModel kind() {

		return kind;
	}

	public PrivatkindRequestData withKind(final KindEditorModel kind) {

		this.kind = kind;
		return this;
	}

	public String uuid() {

		return uuid;
	}

	public PrivatkindRequestData withUuid(final String uuid) {

		this.uuid = uuid;
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
		PrivatkindRequestData other = (PrivatkindRequestData) obj;
		return Objects.equals(kind, other.kind) && Objects.equals(uuid, other.uuid);
	}
}
