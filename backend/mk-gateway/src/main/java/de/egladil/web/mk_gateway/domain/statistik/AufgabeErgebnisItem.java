/**
 *
 */
package de.egladil.web.mk_gateway.domain.statistik;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.statistik.functions.DoubleStringMapper;

/**
 * AufgabeErgebnisItem ist das Ergebnis für eine Aufgabe gegliedert nach Anzahl richtig, falsch und nicht gelöst nebst
 * den prozentualen Anteilen.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
	propOrder = { "nummer", "anzahlRichtigGeloest", "anteilRichtigText", "anzahlFalschGeloest", "anteilFalschText",
		"anzahlNichtGeloest", "anteilNichtGeloestText" })
public class AufgabeErgebnisItem implements Comparable<AufgabeErgebnisItem> {

	@XmlElement(name = "nummer")
	@JsonProperty
	private String nummer;

	@XmlTransient
	@JsonIgnore
	private int index;

	@XmlElement(name = "anzahlRichtigGeloest")
	@JsonIgnore
	private int anzahlRichtigGeloest;

	@XmlTransient
	@JsonIgnore
	private double anteilRichtigGeloest;

	@XmlElement(name = "anzahlFalschGeloest")
	@JsonIgnore
	private int anzahlFalschGeloest;

	@XmlTransient
	@JsonIgnore
	private double anteilFalschGeloest;

	@XmlElement(name = "anzahlNichtGeloest")
	@JsonIgnore
	private int anzahlNichtGeloest;

	@XmlTransient
	@JsonIgnore
	private double anteilNichtGeloest;

	@XmlElement(name = "anteilRichtigGeloest")
	@JsonProperty("anteilRichtigGeloest")
	private String anteilRichtigText;

	@XmlElement(name = "anteilFalschGeloest")
	@JsonProperty("anteilFalschGeloest")
	private String anteilFalschText;

	@XmlElement(name = "anteilNichtGeloest")
	@JsonProperty("anteilNichtGeloest")
	private String anteilNichtGeloestText;

	@XmlTransient
	@JsonProperty
	private boolean zuLeicht;

	@XmlTransient
	@JsonProperty
	private boolean zuSchwer;

	@Override
	public int compareTo(final AufgabeErgebnisItem arg0) {

		return this.index - arg0.index();
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + index;
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
		final AufgabeErgebnisItem other = (AufgabeErgebnisItem) obj;
		if (index != other.index)
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "AufgabeErgebnisItem [nummer=" + nummer + ", richtig=" + anzahlRichtigGeloest
			+ ", anteilRichtig=" + anteilRichtigText + ", falsch=" + anzahlFalschGeloest
			+ ", anteilFalsch=" + anteilFalschText + ", nichtGeloest=" + anzahlNichtGeloest
			+ ", anteilNichtGeloest=" + anteilNichtGeloestText + "]";
	}

	public int index() {

		return index;
	}

	public int anzahlRichtigGeloest() {

		return anzahlRichtigGeloest;
	}

	public int anzahlNichtGeloest() {

		return anzahlNichtGeloest;
	}

	public int anzahlFalschGeloest() {

		return anzahlFalschGeloest;
	}

	public double anteilRichtigGeloest() {

		return anteilRichtigGeloest;
	}

	/**
	 * Setzt sowohl den Anteil als auch den Text.
	 */
	public AufgabeErgebnisItem withAnteilRichtigGeloest(final double anteilRichtigGeloest) {

		this.anteilRichtigGeloest = anteilRichtigGeloest;
		this.anteilRichtigText = new DoubleStringMapper().apply(anteilRichtigGeloest);

		Aufgabenkategorie aufgabenkategorie = Aufgabenkategorie.valueOfNummer(this.nummer);

		switch (aufgabenkategorie) {

		case LEICHT: {

			this.zuLeicht = false;

			if (this.anteilRichtigGeloest < 67.0) {

				this.zuSchwer = true;
			}
		}
			break;

		case MITTEL: {

			if (this.anteilRichtigGeloest > 66.0) {

				this.zuLeicht = true;
				this.zuSchwer = false;
			}

			if (this.anteilRichtigGeloest < 34.0) {

				this.zuLeicht = false;
				this.zuSchwer = true;
			}
			break;
		}

		case SCHWER: {

			if (this.anteilRichtigGeloest > 33.0) {

				this.zuLeicht = true;
			}
			this.zuSchwer = false;
		}

		default:
			break;
		}

		return this;
	}

	public double anteilNichtGeloest() {

		return anteilNichtGeloest;
	}

	/**
	 * Setzt sowohl den Anteil als auch den Text.
	 */
	public AufgabeErgebnisItem withAnteilNichtGeloest(final double anteilNichtGeloest) {

		this.anteilNichtGeloest = anteilNichtGeloest;
		this.anteilNichtGeloestText = new DoubleStringMapper().apply(anteilNichtGeloest);
		return this;
	}

	/**
	 * @return the anteilFalschGeloest
	 */
	public double anteilFalschGeloest() {

		return anteilFalschGeloest;
	}

	/**
	 * Setzt sowohl den Anteil als auch den Text.
	 */
	public AufgabeErgebnisItem withAnteilFalschGeloest(final double anteilFalschGeloest) {

		this.anteilFalschGeloest = anteilFalschGeloest;
		this.anteilFalschText = new DoubleStringMapper().apply(anteilFalschGeloest);
		return this;
	}

	/**
	 * @return the nummer
	 */
	public String getNummer() {

		return nummer;
	}

	public String anteilRichtigText() {

		return anteilRichtigText;
	}

	public String anteilFalschText() {

		return anteilFalschText;
	}

	public String anteilNichtGeloestText() {

		return anteilNichtGeloestText;
	}

	public AufgabeErgebnisItem withNummer(final String nummer) {

		this.nummer = nummer;
		return this;
	}

	public AufgabeErgebnisItem withIndex(final int index) {

		this.index = index;
		return this;
	}

	public AufgabeErgebnisItem withAnzahlRichtigGeloest(final int anzahlRichtigGeloest) {

		this.anzahlRichtigGeloest = anzahlRichtigGeloest;
		return this;
	}

	public AufgabeErgebnisItem withAnzahlFalschGeloest(final int anzahlFalschGeloest) {

		this.anzahlFalschGeloest = anzahlFalschGeloest;
		return this;
	}

	public AufgabeErgebnisItem withAnzahlNichtGeloest(final int anzahlNichtGeloest) {

		this.anzahlNichtGeloest = anzahlNichtGeloest;
		return this;
	}

	public boolean isZuLeicht() {

		return zuLeicht;
	}

	public boolean isZuSchwer() {

		return zuSchwer;
	}

}
