// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AdminStatistikAuspraegung
 */
public class AdminStatistikAuspraegung {

	@JsonProperty
	private String wert;

	@JsonProperty
	private long anzahl;

	/**
	 *
	 */
	AdminStatistikAuspraegung() {

		super();

	}

	/**
	 * @param wert
	 * @param anzahl
	 */
	public AdminStatistikAuspraegung(final String wert, final long anzahl) {

		super();
		this.wert = wert;
		this.anzahl = anzahl;
	}

	@Override
	public String toString() {

		return "AdminStatistikAuspraegung [wert=" + wert + ", anzahl=" + anzahl + "]";
	}

	public String getWert() {

		return wert;
	}

	public long getAnzahl() {

		return anzahl;
	}

}
