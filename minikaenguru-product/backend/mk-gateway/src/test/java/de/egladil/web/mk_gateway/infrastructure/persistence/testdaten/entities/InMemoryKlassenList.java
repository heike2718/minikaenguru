// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.entities;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.kinder.Klasse;

/**
 * InMemoryKlassenList
 */
public class InMemoryKlassenList {

	@JsonProperty
	private Klasse[] klassen;

	public List<Klasse> getKlassen() {

		return Arrays.asList(klassen);
	}

}
