// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.meldungen;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Meldung
 */
public class Meldung {

	@JsonProperty
	private String text;

	Meldung() {

		super();

	}

	public Meldung(final String text) {

		this.text = text;
	}

	public String getText() {

		return text;
	}

	@Override
	public String toString() {

		return text;
	}

}
