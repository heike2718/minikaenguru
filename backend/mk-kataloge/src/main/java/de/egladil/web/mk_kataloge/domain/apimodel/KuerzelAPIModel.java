// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * KuerzelAPIModel
 */
public class KuerzelAPIModel {

	@JsonProperty
	private String kuerzelSchule;

	@JsonProperty
	private String kuerzelOrt;

	public static KuerzelAPIModel create(final String schule, final String ort) {

		KuerzelAPIModel result = new KuerzelAPIModel();
		result.kuerzelOrt = ort;
		result.kuerzelSchule = schule;
		return result;
	}

	KuerzelAPIModel() {

		super();

	}

	String kuerzelSchule() {

		return kuerzelSchule;
	}

	String kuerzelOrt() {

		return kuerzelOrt;
	}

}