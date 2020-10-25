// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * KlasseAPIModel
 */
public class KlasseAPIModel implements Serializable {

	@JsonIgnore
	private static final long serialVersionUID = -5048069037139843806L;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String name;

	@JsonProperty
	private TeilnahmeIdentifier teilnahmeIdentifier;
}
