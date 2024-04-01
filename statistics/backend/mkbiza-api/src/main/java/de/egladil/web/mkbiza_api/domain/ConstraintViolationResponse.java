// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ConstraintViolationResponse
 */
public class ConstraintViolationResponse {

	@JsonProperty
	private String title;

	@JsonProperty
	private int status;

	@JsonProperty
	private List<ConstraintViolation> violations;

}
