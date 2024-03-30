// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MkBiZaGruppierungsitem
 */
public class MkBiZaGruppierungsitem {

	@JsonProperty
	private String name;

	@JsonProperty
	private long anzahl;

	public String getName() {

		return name;
	}

	public MkBiZaGruppierungsitem withName(final String name) {

		this.name = name;
		return this;
	}

	public long getAnzahl() {

		return anzahl;
	}

	public MkBiZaGruppierungsitem withAnzahl(final long anzahl) {

		this.anzahl = anzahl;
		return this;
	}

}
