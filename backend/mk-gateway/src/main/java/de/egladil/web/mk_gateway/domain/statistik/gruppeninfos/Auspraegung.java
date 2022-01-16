// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.gruppeninfos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Auspraegung
 */
public class Auspraegung {

	@JsonProperty
	private String wert;

	@JsonProperty
	private long anzahl;

	/**
	 *
	 */
	Auspraegung() {

		super();

	}

	/**
	 * @param wert
	 * @param anzahl
	 */
	public Auspraegung(final String wert, final long anzahl) {

		super();
		this.wert = wert;
		this.anzahl = anzahl;
	}

	@Override
	public String toString() {

		return "Auspraegung [wert=" + wert + ", anzahl=" + anzahl + "]";
	}

	public String getWert() {

		return wert;
	}

	public long getAnzahl() {

		return anzahl;
	}

}
