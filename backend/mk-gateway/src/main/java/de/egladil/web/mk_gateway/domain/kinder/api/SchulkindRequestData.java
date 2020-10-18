// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SchulkindRequestData
 */
public class SchulkindRequestData {

	@JsonProperty
	private KindAPIModel kind;

	@JsonProperty
	private KlasseAPIModel klasse;
}
