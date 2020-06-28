// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.testdaten.entities;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.personen.Rolle;
import de.egladil.web.mk_wettbewerb.domain.personen.ZugangUnterlagen;

/**
 * InMemoryVeranstalter
 */
public class InMemoryVeranstalter {

	@JsonProperty
	private Rolle rolle;

	@JsonProperty
	private Person person;

	@JsonProperty
	private ZugangUnterlagen zugangUnterlagen;

	@JsonProperty
	private List<Identifier> teilnahmenummern;

	public static InMemoryVeranstalter createForTest(final String uuid) {

		InMemoryVeranstalter result = new InMemoryVeranstalter();
		result.person = new Person(uuid, "Hugo Haller");
		result.rolle = Rolle.LEHRER;
		result.zugangUnterlagen = ZugangUnterlagen.DEFAULT;
		result.teilnahmenummern = Arrays.asList(new Identifier[] { new Identifier("ABCDEFG"), new Identifier("RSTUVWR") });
		return result;
	}

	public Rolle getRolle() {

		return rolle;
	}

	public Person getPerson() {

		return person;
	}

	public List<Identifier> getTeilnahmenummern() {

		return teilnahmenummern;
	}

	public ZugangUnterlagen getZugangUnterlagen() {

		return zugangUnterlagen;
	}

}
