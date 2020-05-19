// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.guimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * PersonGuiModel
 */
public class PersonGuiModel {

	@JsonProperty
	private final String fullName;

	public PersonGuiModel(final String fullName) {

		this.fullName = fullName;
	}

}
