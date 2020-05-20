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
 * SchuleDashboardModel
 */
@ValueObject
public class SchuleDashboardModel {

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

	SchuleDashboardModel() {

	}

	public SchuleDashboardModel(final String kuerzel) {

		this.kuerzel = kuerzel;
	}

	public SchuleDashboardModel withNameUrkunde(final String nameUrkunde) {

		this.nameUrkunde = nameUrkunde;
		return this;
	}

	public SchuleDashboardModel withKollegen(final List<Person> kollegen) {

		this.kollegen = StringUtils.join(kollegen.stream().map(p -> p.fullName()).collect(Collectors.toList()),
			",");

		return this;
	}

	public SchuleDashboardModel withAngemeldetDurch(final Person person) {

		this.angemeldetDurch = person.fullName();
		return this;
	}

	public SchuleDashboardModel withAnzahlTeilnahmen(final int anzahlTeilnahmen) {

		this.anzahlTeilnahmen = anzahlTeilnahmen;
		return this;
	}

}
