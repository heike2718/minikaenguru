// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * StringsAPIModel
 */
public class StringsAPIModel {

	@JsonProperty
	private List<String> strings = new ArrayList<>();

	public void addString(final String string) {

		if (string != null && !strings.contains(string)) {

			strings.add(string);
		}
	}

	public StringsAPIModel withStrings(final List<String> strings) {

		this.strings = strings;
		return this;
	}

	public List<String> getStrings() {

		return strings;
	}
}
