// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.guimodel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.semantik.ValueObject;

/**
 * SchuleDashboardModel
 */
@ValueObject
public class SchuleDashboardModel {

	@JsonProperty
	private List<PersonGuiModel> kollegen = new ArrayList<>();

	@JsonProperty
	private PersonGuiModel angemeldetDurch;

	@JsonProperty
	private int anzahlTeilnahmen;

	public SchuleDashboardModel withKollegen(final List<Person> kollegen) {

		this.kollegen = kollegen.stream().map(p -> new PersonGuiModel(p.fullName())).collect(Collectors.toList());

		return this;
	}

	public SchuleDashboardModel withAngemeldetDurch(final Person person) {

		this.angemeldetDurch = new PersonGuiModel(person.fullName());
		return this;
	}

	public SchuleDashboardModel withAnzahlTeilnahmen(final int anzahlTeilnahmen) {

		this.anzahlTeilnahmen = anzahlTeilnahmen;
		return this;
	}

}
