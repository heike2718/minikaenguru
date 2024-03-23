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

	@Override
	public String toString() {

		return "KuerzelAPIModel [kuerzelOrt=" + kuerzelOrt + ", kuerzelSchule=" + kuerzelSchule + "]";
	}

	public String kuerzelSchule() {

		return kuerzelSchule;
	}

	public String kuerzelOrt() {

		return kuerzelOrt;
	}

}
