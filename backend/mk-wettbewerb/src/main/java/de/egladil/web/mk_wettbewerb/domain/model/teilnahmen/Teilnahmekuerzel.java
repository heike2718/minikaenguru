// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.teilnahmen;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Teilnahmekuerzel
 */
public abstract class Teilnahmekuerzel {

	@JsonProperty
	private final String kuerzel;

	/**
	 * @param kuerzel
	 */
	public Teilnahmekuerzel(final String kuerzel) {

		if (StringUtils.isBlank(kuerzel)) {

			throw new IllegalArgumentException("kuerzel darf nicht blank sein");
		}

		this.kuerzel = kuerzel;
	}

	public String kuerzel() {

		return this.kuerzel;
	}

	/**
	 * @return String eindeutigen Identifizierer zum Überschreiben von equals / hashCode
	 */
	protected abstract String classIdentifier();

	@Override
	public int hashCode() {

		return Objects.hash(classIdentifier(), kuerzel);
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
		Teilnahmekuerzel other = (Teilnahmekuerzel) obj;
		return Objects.equals(classIdentifier(), other.classIdentifier()) && Objects.equals(kuerzel, other.kuerzel);
	}
}
