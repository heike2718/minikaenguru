// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Downloaddaten
 */
@Embeddable
public class Downloaddaten {

	@Column(name = "WAS")
	@NotNull
	@Size(max = 40)
	private String dateiname;

	@NotBlank
	@Column(name = "JAHR", length = 4)
	private String jahr;

	@Column(name = "ANZAHL")
	private int anzahl;

	/**
	 *
	 */
	public void anzahlErhoehen() {

		anzahl++;
	}

	@Override
	public String toString() {

		return "Downloaddaten [jahr=" + jahr + ", dateiname=" + dateiname + ", anzahl=" + anzahl + "]";
	}

	public String getDateiname() {

		return dateiname;
	}

	public void setDateiname(final String dateiart) {

		this.dateiname = dateiart;
	}

	public String getJahr() {

		return jahr;
	}

	public void setJahr(final String jahr) {

		this.jahr = jahr;
	}

	public int getAnzahl() {

		return anzahl;
	}

	public void setAnzahl(final int anzahl) {

		this.anzahl = anzahl;
	}
}
