// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * InMemoryTeilnahmeList
 */
public class InMemoryTeilnahmeList {

	@JsonProperty(value = "teilnahme")
	private List<InMemoryTeilnahme> teilnahmen;

	public static InMemoryTeilnahmeList create(final List<InMemoryTeilnahme> teilnahmen) {

		InMemoryTeilnahmeList result = new InMemoryTeilnahmeList();
		result.teilnahmen = teilnahmen;
		return result;

	}

	public List<InMemoryTeilnahme> getTeilnahmen() {

		return teilnahmen;
	}

}
