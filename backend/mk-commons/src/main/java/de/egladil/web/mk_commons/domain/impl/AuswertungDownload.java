// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.domain.ITeilnahmeIdentifierProvider;
import de.egladil.web.mk_commons.domain.enums.Teilnahmeart;

/**
 * AuswertungDownload
 */
@Entity
@Table(name = "auswertungdownloads")
public class AuswertungDownload implements IMkEntity, ITeilnahmeIdentifierProvider {

	public static final String UNIQUE_ATTRIBUTE_NAME = "downloadCode";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "VERSION")
	private int version;

	@NotNull
	@Column(name = "TEILNAHMEART")
	@Enumerated(EnumType.STRING)
	private Teilnahmeart teilnahmeart;

	@NotNull
	@Kuerzel
	@Size(min = 8, max = 8)
	@Column(name = "TEILNAHMEKUERZEL", length = 8)
	private String teilnahmekuerzel;

	@NotBlank
	@Column(name = "JAHR", length = 4)
	private String jahr;

	@NotNull
	@Kuerzel
	@Size(min = 22, max = 22)
	@Column(name = "DOWNLOADCODE")
	private String downloadCode;

	@UuidString
	@NotNull
	@Size(min = 1, max = 40)
	@Column(name = "MODIFIED_BY")
	private String geaendertDurch;

	@Column(name = "DATEN")
	@Lob
	private byte[] daten;

	@Column
	private int anzahl;

	public static AuswertungDownload forTeilnahmeIdentifier(final TeilnahmeIdentifier teilnahmeIdentifier) {

		final AuswertungDownload result = new AuswertungDownload();

		result.jahr = teilnahmeIdentifier.getJahr();
		result.teilnahmeart = teilnahmeIdentifier.getTeilnahmeart();
		result.teilnahmekuerzel = teilnahmeIdentifier.getKuerzel();

		return result;
	}

	@Override
	public TeilnahmeIdentifier provideTeilnahmeIdentifier() {

		return TeilnahmeIdentifier.create(teilnahmeart, teilnahmekuerzel, jahr);
	}

	public Long getId() {

		return this.id;
	}

	public byte[] getDaten() {

		return daten;
	}

	public void setDaten(final byte[] daten) {

		this.daten = daten;
	}

	public String getDownloadCode() {

		return downloadCode;
	}

	public void setDownloadCode(final String kuerzel) {

		this.downloadCode = kuerzel;
	}

	public String getGeaendertDurch() {

		return geaendertDurch;
	}

	public void setGeaendertDurch(final String geaendertDurch) {

		this.geaendertDurch = geaendertDurch;
	}

	public int getAnzahl() {

		return anzahl;
	}

	public void setAnzahl(final int anzahl) {

		this.anzahl = anzahl;
	}

	public Teilnahmeart getTeilnahmeart() {

		return teilnahmeart;
	}

	public String getTeilnahmekuerzel() {

		return teilnahmekuerzel;
	}

	public String getJahr() {

		return jahr;
	}
}
