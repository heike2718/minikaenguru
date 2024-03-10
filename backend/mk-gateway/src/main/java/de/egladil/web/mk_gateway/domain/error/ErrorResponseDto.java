// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.error;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ErrorResponseDto hat die gleiche Signatur wie MessagePayload - kann also wie dieses im Frontend verwendet werden. Ziel ist das
 * Zurückdrängen der commons-Libs.
 */
public class ErrorResponseDto {

	@JsonProperty
	private String level;

	@JsonProperty
	private String message;

	public static ErrorResponseDto error(final String fehlermeldung) {

		ErrorResponseDto result = new ErrorResponseDto();
		result.level = Schweregrad.ERROR.toString();
		result.message = fehlermeldung;
		return result;
	}

	public static ErrorResponseDto warning(final String fehlermeldung) {

		ErrorResponseDto result = new ErrorResponseDto();
		result.level = Schweregrad.WARNING.toString();
		result.message = fehlermeldung;
		return result;
	}

	public String getMessage() {

		return message;
	}

	public String getLevel() {

		return level;
	}

}
