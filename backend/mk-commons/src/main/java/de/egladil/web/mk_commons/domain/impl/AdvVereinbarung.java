// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.Hausnummer;
import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.LandKuerzel;
import de.egladil.web.commons_validation.annotations.Plz;
import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_commons.domain.IMkEntity;

/**
 * AdvVereinbarung
 */
@Entity
@Table(name = "adv_vereinbarungen")
public class AdvVereinbarung implements IMkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "VERSION")
	private int version;

	@NotNull
	@Kuerzel
	@Size(min = 8, max = 8)
	@Column(name = "SCHULKUERZEL", length = 8)
	private String schulkuerzel;

	@NotNull
	@StringLatin
	@Size(min = 1, max = 100)
	@Column(name = "SCHULNAME", length = 100)
	private String schulname;

	@NotNull
	@Size(min = 1, max = 200)
	@StringLatin
	@Column(name = "STRASSE", length = 100)
	private String strasse;

	@NotNull
	@Size(min = 1, max = 10)
	@Hausnummer
	@Column(name = "HAUSNR", length = 10)
	private String hausnummer;

	@NotNull
	@StringLatin
	@Size(min = 1, max = 100)
	@Column(name = "ORT", length = 100)
	private String ort;

	@NotNull
	@Size(min = 1, max = 100)
	@Plz
	@Column(name = "PLZ", length = 10)
	private String plz;

	@NotNull
	@LandKuerzel
	@Size(min = 2, max = 2)
	@Column(name = "LAENDERCODE", length = 8)
	private String laendercode;

	@NotBlank
	@Size(min = 1, max = 19)
	@Column(name = "ZUGESTIMMT_AM")
	private String zugestimmtAm;

	@UuidString
	@NotBlank
	@Size(min = 1, max = 40)
	@Column(name = "ZUGESTIMMT_DURCH")
	private String zugestimmtDurch;

	@ManyToOne()
	@JoinColumn(name = "AVD_TEXTE_ID")
	private AdvText advText;

	public Long getId() {

		return id;
	}

	public String getSchulkuerzel() {

		return schulkuerzel;
	}

	public void setSchulkuerzel(final String schulkuerzel) {

		this.schulkuerzel = schulkuerzel;
	}

	public String getStrasse() {

		return strasse;
	}

	public void setStrasse(final String strasse) {

		this.strasse = strasse;
	}

	public String getPlz() {

		return plz;
	}

	public void setPlz(final String plz) {

		this.plz = plz;
	}

	public String getHausnummer() {

		return hausnummer;
	}

	public void setHausnummer(final String hausnummer) {

		this.hausnummer = hausnummer;
	}

	public void setZugestimmtAm(final String zugestimmtAm) {

		this.zugestimmtAm = zugestimmtAm;
	}

	public String getZugestimmtDurch() {

		return zugestimmtDurch;
	}

	public void setZugestimmtDurch(final String zugestimmtDurch) {

		this.zugestimmtDurch = zugestimmtDurch;
	}

	public AdvText getAdvText() {

		return advText;
	}

	public void setAdvText(final AdvText advText) {

		this.advText = advText;
	}

	public String getSchulname() {

		return schulname;
	}

	public void setSchulname(final String schulname) {

		this.schulname = schulname;
	}

	public String getOrt() {

		return ort;
	}

	public void setOrt(final String ort) {

		this.ort = ort;
	}

	public String getLaendercode() {

		return laendercode;
	}

	public void setLaendercode(final String laendercode) {

		this.laendercode = laendercode;
	}

	public String getZugestimmtAmDruck() {

		return this.zugestimmtAm.substring(0, 10);
	}
}
