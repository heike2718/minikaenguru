/**
 *
 */
package de.egladil.web.mk_gateway.domain.auswertungen.statistik;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AufgabeErgebnisItem ist das Ergebnis für eine Aufgabe gegliedert nach Anzahl richtig, falsch und nicht gelöst nebst
 * den prozentualen Anteilen.
 */
@XmlAccessorType(XmlAccessType.FIELD)
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

	/**
	 * @return the index
	 */
	public int index() {

		return index;
	}

	/**
	 * @return the anzahlRichtigGeloest
	 */
	public int anzahlRichtigGeloest() {

		return anzahlRichtigGeloest;
	}

	/**
	 * @return the anzahlNichtGeloest
	 */
	public int anzahlNichtGeloest() {

		return anzahlNichtGeloest;
	}

	/**
	 * @return the anzahlFalschGeloest
	 */
	public int anzahlFalschGeloest() {

		return anzahlFalschGeloest;
	}

	/**
	 * @return the anteilRichtigGeloest
	 */
	public double anteilRichtigGeloest() {

		return anteilRichtigGeloest;
	}

	/**
	 * Setzt sowohl den Anteil als auch den Text.
	 */
	public AufgabeErgebnisItem withAnteilRichtigGeloest(final double anteilRichtigGeloest) {

		this.anteilRichtigGeloest = anteilRichtigGeloest;
		this.anteilRichtigText = new DoubleStringMapper().apply(anteilRichtigGeloest);
		return this;
	}

	/**
	 * @return the anteilNichtGeloest
	 */
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
	 * @param zuLeicht
	 */
	public AufgabeErgebnisItem withZuLeicht(final boolean aufgabenkategoriePasstNicht) {

		this.zuLeicht = aufgabenkategoriePasstNicht;
		return this;
	}

	public final AufgabeErgebnisItem withZuSchwer(final boolean zuSchwer) {

		this.zuSchwer = zuSchwer;
		return this;
	}

	/**
	 * @return the nummer
	 */
	public String getNummer() {

		return nummer;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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

	/**
	 * Liefert die Membervariable anteilRichtigText
	 *
	 * @return die Membervariable anteilRichtigText
	 */
	public String getAnteilRichtigText() {

		return anteilRichtigText;
	}

	/**
	 * Liefert die Membervariable anteilFalschText
	 *
	 * @return die Membervariable anteilFalschText
	 */
	public String getAnteilFalschText() {

		return anteilFalschText;
	}

	/**
	 * Liefert die Membervariable anteilNichtGeloestText
	 *
	 * @return die Membervariable anteilNichtGeloestText
	 */
	public String getAnteilNichtGeloestText() {

		return anteilNichtGeloestText;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		final StringBuffer sb = new StringBuffer();
		sb.append("AufgabeErgebnisItem [nummer=");
		sb.append(nummer);
		sb.append(", richtig=");
		sb.append(anzahlRichtigGeloest);
		sb.append(", anteilRichtig=");
		sb.append(anteilRichtigText);
		sb.append(", falsch=");
		sb.append(anzahlFalschGeloest);
		sb.append(", anteilFalsch=");
		sb.append(anteilFalschText);
		sb.append(", nichtGeloest=");
		sb.append(anzahlNichtGeloest);
		sb.append(", anteilNichtGeloest=");
		sb.append(anteilNichtGeloestText);
		sb.append("]");
		return sb.toString();
	}
}
