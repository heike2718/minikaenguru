// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.statistik.functions.DoubleAsStringWithKommaMapper;
import de.egladil.web.mk_gateway.domain.statistik.functions.PunktintervallStringMapper;

/**
 * GesamtpunktverteilungItem
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GesamtpunktverteilungItem {

	@XmlTransient
	@JsonIgnore
	private Punktintervall punktintervall;

	@XmlTransient
	@JsonIgnore
	private int anzahl;

	@XmlTransient
	@JsonIgnore
	private double prozentrang;

	@XmlElement(name = "intervall")
	@JsonProperty("intervall")
	private String punktintervallText;

	@XmlElement(name = "anzahlImIntervall")
	@JsonProperty("anzahlImIntervall")
	private String anzahlText;

	@XmlElement(name = "intervallPR")
	@JsonProperty("intervallPR")
	private String prozentrangText;

	public Punktintervall getPunktintervall() {

		return punktintervall;
	}

	public GesamtpunktverteilungItem withPunktintervall(final Punktintervall punktintervall) {

		this.punktintervall = punktintervall;
		this.punktintervallText = new PunktintervallStringMapper().apply(punktintervall);
		return this;
	}

	public int getAnzahl() {

		return anzahl;
	}

	public GesamtpunktverteilungItem withAnzahl(final int anzahl) {

		this.anzahl = anzahl;
		this.anzahlText = "" + anzahl;
		return this;
	}

	public double getProzentrang() {

		return prozentrang;
	}

	public GesamtpunktverteilungItem withProzentrang(final double prozentrang) {

		this.prozentrang = prozentrang;
		this.prozentrangText = new DoubleAsStringWithKommaMapper().apply(this.prozentrang);
		return this;
	}

	public String getPunktintervallText() {

		return punktintervallText;
	}

	public String getAnzahlText() {

		return anzahlText;
	}

	public String getProzentrangText() {

		return prozentrangText;
	}

	@Override
	public int hashCode() {

		return Objects.hash(punktintervall);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		GesamtpunktverteilungItem other = (GesamtpunktverteilungItem) obj;
		return Objects.equals(punktintervall, other.punktintervall);
	}

}
