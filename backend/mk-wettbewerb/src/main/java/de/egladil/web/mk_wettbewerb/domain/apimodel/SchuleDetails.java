// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.apimodel;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.semantik.ValueObject;

/**
 * SchuleDetails
 */
@ValueObject
public class SchuleDetails {

	@JsonProperty
	private String kuerzel;

	@JsonProperty
	private String nameUrkunde;

	@JsonProperty
	private String kollegen;

	@JsonProperty
	private String angemeldetDurch;

	@JsonProperty
	private int anzahlTeilnahmen;

	SchuleDetails() {

	}

	public SchuleDetails(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public SchuleDetails withNameUrkunde(final String nameUrkunde) {

		this.nameUrkunde = nameUrkunde;
		return this;
	}

	public SchuleDetails withKollegen(final List<Person> kollegen) {

		this.kollegen = StringUtils.join(kollegen.stream().map(p -> p.fullName()).collect(Collectors.toList()),
			", ");

		return this;
	}

	public SchuleDetails withAngemeldetDurch(final Person person) {

		this.angemeldetDurch = person.fullName();
		return this;
	}

	public SchuleDetails withAnzahlTeilnahmen(final int anzahlTeilnahmen) {

		this.anzahlTeilnahmen = anzahlTeilnahmen;
		return this;
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

	public int anzahlTeilnahmen() {

		return anzahlTeilnahmen;
	}

}
