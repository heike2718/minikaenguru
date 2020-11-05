// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.entities;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.kinder.Kind;

/**
 * InMemoryKinderList
 */
public class InMemoryKinderList {

	@JsonProperty
	private Kind[] kinder;

	public List<Kind> getKinder() {

		return Arrays.asList(kinder);
	}

}
