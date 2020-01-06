// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.mk_commons.domain.ILoesungszettel;
import de.egladil.web.mk_commons.domain.enums.Auswertungsquelle;
import de.egladil.web.mk_commons.domain.enums.Klassenstufe;
import de.egladil.web.mk_commons.validation.annotations.Antwortcode;
import de.egladil.web.mk_commons.validation.annotations.Wertungscode;

/**
 * LoesungszettelRohdaten enthalten die Rohdaten für einen einzelnen Teilnehmer.
 */
@Embeddable
public class LoesungszettelRohdaten implements ILoesungszettel {

	@NotNull
	@Column(name = "KLASSENSTUFE")
	@Enumerated(EnumType.STRING)
	private Klassenstufe klassenstufe;

	@NotNull
	@Column(name = "QUELLE")
	@Enumerated(EnumType.STRING)
	private Auswertungsquelle auswertungsquelle;

	@Column(name = "TYPO")
	private boolean typo;

	@Column(name = "KAENGURUSPRUNG")
	private int kaengurusprung;

	@Column(name = "PUNKTE")
	private int punkte;

	/** Die Nutzereingabe ist ein kommaseparierter String, entweder von 'f,r,n' oder 'A,B,C,D,E,N' */
	@Size(max = 100)
	@Column(name = "ORIGINALWERTUNG")
	private String nutzereingabe;

	/** Aneinanderreihung von 12 bzw. 15 Lösungsbuchstaben oder N (Beispiel 'EDNADECCCCNNABD') */
	@Size(max = 15)
	@Column(name = "ANTWORTCODE")
	@Antwortcode
	private String antwortcode;

	/** Aneinanderreihung von 12 bzw. 15 Bewertungen f,r,n (Beispiel 'fnnfffrrffrn') */
	@Size(max = 15)
	@Column(name = "WERTUNGSCODE")
	@Wertungscode
	private String wertungscode;

	public static class Builder {
		// Pflichtattribute
		private final Auswertungsquelle auswertungsquelle;

		private final Klassenstufe klassenstufe;

		private final int laengeKaengurusprung;

		private final int punkte;

		private final String berechneterWertungscode;

		private final String nutzereingabe;

		// optionale Attribute
		private String antwortcode;

		private boolean typo = false;

		/**
		 * Erzeugt eine Instanz von Builder
		 */
		public Builder(final Auswertungsquelle auswertungsquelle, final Klassenstufe klassenstufe, final int laengeKaengurusprung, final int punkte, final String berechneterWertungscode, final String nutzereingabe) {

			if (auswertungsquelle == null) {

				throw new NullPointerException("Parameter auswertungsquelle");
			}

			if (klassenstufe == null) {

				throw new NullPointerException("Parameter klassenstufe");
			}

			if (berechneterWertungscode == null) {

				throw new NullPointerException("Parameter wertungscode");
			}

			if (nutzereingabe == null) {

				throw new NullPointerException("Parameter nutzereingabe");
			}
			this.auswertungsquelle = auswertungsquelle;
			this.klassenstufe = klassenstufe;
			this.laengeKaengurusprung = laengeKaengurusprung;
			this.punkte = punkte;
			this.berechneterWertungscode = berechneterWertungscode;
			this.nutzereingabe = nutzereingabe;
		}

		public Builder typo(final boolean typo) {

			this.typo = typo;
			return this;
		}

		public Builder antwortcode(final String antwortcode) {

			if (antwortcode == null) {

				throw new NullPointerException("Parameter antwortcode");
			}
			this.antwortcode = antwortcode;
			return this;
		}

		public LoesungszettelRohdaten build() {

			return new LoesungszettelRohdaten(this);
		}
	}

	/**
	 * Erforderlich für JPA.
	 */
	public LoesungszettelRohdaten() {

	}

	private LoesungszettelRohdaten(final Builder builder) {

		auswertungsquelle = builder.auswertungsquelle;
		klassenstufe = builder.klassenstufe;
		kaengurusprung = builder.laengeKaengurusprung;
		punkte = builder.punkte;
		nutzereingabe = builder.nutzereingabe;
		antwortcode = builder.antwortcode;
		wertungscode = builder.berechneterWertungscode;
		typo = builder.typo;
	}

	public Klassenstufe getKlassenstufe() {

		return klassenstufe;
	}

	public Auswertungsquelle getAuswertungsquelle() {

		return auswertungsquelle;
	}

	@Override
	public Integer getKaengurusprung() {

		return kaengurusprung;
	}

	@Override
	public Integer getPunkte() {

		return punkte;
	}

	public String getNutzereingabe() {

		return nutzereingabe;
	}

	public String getAntwortcode() {

		return antwortcode;
	}

	@Override
	public String getWertungscode() {

		return wertungscode;
	}

	@Override
	public String toString() {

		return "LoesungszettelRohdaten [klassenstufe=" + klassenstufe + ", auswertungsquelle=" + auswertungsquelle
			+ ", kaengurusprung=" + kaengurusprung + ", punkte=" + punkte + ", nutzereingabe=" + nutzereingabe + ", antwortcode="
			+ antwortcode + ", wertungscode=" + wertungscode + "]";
	}

	public boolean isTypo() {

		return typo;
	}
}
