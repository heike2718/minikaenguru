// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;
import de.egladil.web.mk_gateway.domain.veranstalter.Kollege;

/**
 * SchuleDetails
 */
@ValueObject
public class SchuleDetails {

	@JsonProperty
	private String kuerzel;

	@JsonProperty
	private String kollegen;

	@JsonProperty
	private String angemeldetDurch;

	@JsonProperty
	private String nameUrkunde;

	@JsonProperty
	private int anzahlTeilnahmen;

	@JsonProperty
	private boolean hatAdv;

	SchuleDetails() {

	}

	public SchuleDetails(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public SchuleDetails withKollegen(final List<Kollege> kollegen) {

		this.kollegen = StringUtils.join(kollegen.stream().map(p -> p.fullName()).collect(Collectors.toList()),
			", ");

		return this;
	}

	public SchuleDetails withAngemeldetDurch(final Kollege person) {

		this.angemeldetDurch = person.fullName();
		return this;
	}

	public SchuleDetails withAnzahlTeilnahmen(final int anzahl) {

		this.anzahlTeilnahmen = anzahl;
		return this;
	}

	public String kuerzel() {

		return kuerzel;
	}

	public String kollegen() {

		return kollegen;
	}

	public String angemeldetDurch() {

		return angemeldetDurch;
	}

	public int anzahlTeilnahmen() {

		return anzahlTeilnahmen;
	}

	public boolean hatAdv() {

		return hatAdv;
	}

	public SchuleDetails withHatAdv(final boolean hatAdv) {

		this.hatAdv = hatAdv;
		return this;
	}

	public String nameUrkunde() {

		return nameUrkunde;
	}

	public SchuleDetails withNameUrkunde(final String nameUrkunde) {

		this.nameUrkunde = nameUrkunde;
		return this;
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
		SchuleDetails other = (SchuleDetails) obj;
		return Objects.equals(kuerzel, other.kuerzel);
	}

}
