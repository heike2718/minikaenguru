// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.StringLatin;
import jakarta.validation.constraints.NotBlank;

/**
 * KlasseEditorModel
 */
public class KlasseEditorModel implements Serializable {

	@JsonIgnore
	private static final long serialVersionUID = 5522478158179301026L;

	@JsonProperty
	@NotBlank
	@StringLatin
	private String name;

	public String name() {

		return name;
	}

	public KlasseEditorModel withName(final String name) {

		this.name = name;
		return this;
	}
}
