// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.veranstalter;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.SchulteilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.semantik.ValueObject;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;

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
	private SchulteilnahmeAPIModel aktuelleTeilnahme;

	@JsonProperty
	private int anzahlTeilnahmen;

	@JsonProperty
	private boolean hatAdv;

	SchuleDetails() {

	}

	public SchuleDetails(final String kuerzel) {

		this.kuerzel = kuerzel;
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

	public SchuleDetails withTeilnahme(final SchulteilnahmeAPIModel teilnahme) {

		this.aktuelleTeilnahme = teilnahme;
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

	public SchulteilnahmeAPIModel aktuelleTeilnahme() {

		return aktuelleTeilnahme;
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

}
