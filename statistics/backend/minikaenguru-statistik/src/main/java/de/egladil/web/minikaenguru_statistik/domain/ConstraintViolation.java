// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain;

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
