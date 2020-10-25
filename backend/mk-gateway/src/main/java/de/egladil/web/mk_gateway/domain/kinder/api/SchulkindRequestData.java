// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SchulkindRequestData
 */
public class SchulkindRequestData implements Serializable {

	@JsonIgnore
	private static final long serialVersionUID = 8379589127142138630L;

	@JsonProperty
	private KindAPIModel kind;

	@JsonProperty
	private KlasseAPIModel klasse;
}
