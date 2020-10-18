// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * KlasseAPIModel
 */
public class KlasseAPIModel {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String name;

	@JsonProperty
	private TeilnahmeIdentifier teilnahmeIdentifier;
}
