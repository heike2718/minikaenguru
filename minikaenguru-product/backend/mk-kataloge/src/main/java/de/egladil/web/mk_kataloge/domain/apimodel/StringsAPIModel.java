// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.apimodel;

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

	public List<String> getStrings() {

		return strings;
	}
}
