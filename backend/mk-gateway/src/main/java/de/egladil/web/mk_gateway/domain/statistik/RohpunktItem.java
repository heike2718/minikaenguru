// =====================================================
// Projekt: de.egladil.mkv.auswertungen
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.domain.statistik;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.statistik.functions.DoubleAsStringWithKommaMapper;
import de.egladil.web.mk_gateway.domain.statistik.functions.PunkteStringMapper;

/**
 * RohpunktItem ist ein Eintrag in der Prozentrangtabelle.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RohpunktItem {

	@XmlElement(name = "punkte")
	@JsonProperty("punkte")
	private String punkteText;

	@XmlTransient
	@JsonIgnore
	private int punkte;

	@XmlTransient
	@JsonIgnore
	private int anzahl;

	@XmlElement(name = "anzahl")
	@JsonProperty("anzahl")
	private String anzahlText;

	@XmlTransient
	@JsonIgnore
	private double prozentrang;

	@XmlElement(name = "prozentrang")
	@JsonProperty("prozentrang")
	private String prozentrangText;

	/**
	 * Erzeugt eine Instanz von RohpunktItem
	 */
	public RohpunktItem() {

	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + punkte;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RohpunktItem other = (RohpunktItem) obj;
		if (punkte != other.punkte)
			return false;
		return true;
	}

	public String getPunkteText() {

		return punkteText;
	}

	public int getPunkte() {

		return punkte;
	}

	public RohpunktItem withPunkten(final int punkte) {

		this.punkte = punkte;
		this.punkteText = new PunkteStringMapper().apply(punkte);
		return this;
	}

	public int getAnzahl() {

		return anzahl;
	}

	public RohpunktItem withAnzahl(final int anzahl) {

		this.anzahl = anzahl;
		this.anzahlText = Integer.toString(anzahl);
		return this;
	}

	public double getProzentrang() {

		return prozentrang;
	}

	public RohpunktItem withProzentrang(final double prozentrang) {

		this.prozentrang = prozentrang;
		this.prozentrangText = new DoubleAsStringWithKommaMapper().apply(this.prozentrang);
		return this;
	}

	public String getProzentrangText() {

		return prozentrangText;
	}

	/**
	 * Liefert die Membervariable anzahlText
	 *
	 * @return die Membervariable anzahlText
	 */
	public String getAnzahlText() {

		return anzahlText;
	}

}
