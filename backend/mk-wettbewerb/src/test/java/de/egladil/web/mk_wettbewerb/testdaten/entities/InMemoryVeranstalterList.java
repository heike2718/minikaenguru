// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.testdaten.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * InMemoryVeranstalterList
 */
public class InMemoryVeranstalterList {

	@JsonProperty
	private List<InMemoryVeranstalter> veranstalter;

	public InMemoryVeranstalterList() {

	}

	public InMemoryVeranstalterList(final List<InMemoryVeranstalter> veranstalter) {

		this.veranstalter = veranstalter;
	}

	public List<InMemoryVeranstalter> getVeranstalter() {

		return veranstalter;
	}

}
