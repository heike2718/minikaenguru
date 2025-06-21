// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain;

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
