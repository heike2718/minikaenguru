// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ConstraintViolation
 */
public class ConstraintViolation {

	@JsonProperty
	private String field;

	@JsonProperty
	private String message;
}
