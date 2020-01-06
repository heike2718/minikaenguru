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
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.domain.enums.Teilnahmeart;
import de.egladil.web.mk_commons.domain.enums.UploadMimeType;
import de.egladil.web.mk_commons.domain.enums.UploadStatus;

/**
 * Upload enthält die Informationen zu einer hochgeladenen Punkttabelle.
 */
@Entity
@Table(name = "uploads")
public class Upload implements IMkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	@JsonIgnore
	private Long id;

	@Version
	@Column(name = "VERSION")
	@JsonIgnore
	private int version;

	@NotNull
	@Kuerzel
	@Size(min = 8, max = 8)
	@Column(name = "TEILNAHMEKUERZEL")
	@JsonProperty
	private String kuerzel;

	@NotNull
	@Column(name = "TEILNAHMEART")
	@Enumerated(EnumType.STRING)
	@JsonProperty
	private Teilnahmeart teilnahmeart;

	@NotNull
	@Column(name = "MIMETYPE")
	@Enumerated(EnumType.STRING)
	@JsonProperty
	private UploadMimeType mimetype;

	@NotNull
	@Size(max = 150)
	@Column(name = "DATEINAME")
	@JsonProperty
	private String dateiname;

	@NotNull
	@Size(min = 1, max = 128)
	@Column(name = "CHECKSUMME")
	@JsonProperty
	private String checksumme;

	@Column(name = "OHNE_DOWNLOADS")
	@JsonIgnore
	private boolean uploadOhneDownloads;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	@JsonProperty
	private UploadStatus uploadStatus;

	@NotNull
	@Size(min = 4, max = 4)
	@Column(name = "JAHR")
	@JsonProperty
	private String jahr;

	/**
	 * Erzeiugt einen Upload mit status NEU und den gegebenen Attributen.
	 *
	 * @param  mimeType
	 * @param  checksumme
	 * @param  dateiname
	 * @return
	 */
	public static Upload createStatusNeuByDateiInfos(final UploadMimeType mimeType, final String checksumme, final String dateiname) {

		Upload upload = new Upload();
		upload.mimetype = mimeType;
		upload.checksumme = checksumme;
		upload.dateiname = dateiname;
		upload.uploadStatus = UploadStatus.NEU;
		return upload;
	}

	public Upload() {

	}

	public Upload initWith(final TeilnahmeIdentifier teilnahmeIdentifier) {

		this.jahr = teilnahmeIdentifier.getJahr();
		this.teilnahmeart = teilnahmeIdentifier.getTeilnahmeart();
		this.kuerzel = teilnahmeIdentifier.getKuerzel();
		return this;
	}

	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public void setKuerzel(final String schulkuerzel) {

		this.kuerzel = schulkuerzel;
	}

	public Teilnahmeart getTeilnahmeart() {

		return teilnahmeart;
	}

	public void setTeilnahmeart(final Teilnahmeart teilnahmeart) {

		this.teilnahmeart = teilnahmeart;
	}

	public UploadMimeType getMimetype() {

		return mimetype;
	}

	public void setMimetype(final UploadMimeType mimetype) {

		this.mimetype = mimetype;
	}

	public String getDateiname() {

		return dateiname;
	}

	public void setDateiname(final String dateiname) {

		this.dateiname = dateiname;
	}

	public String getChecksumme() {

		return checksumme;
	}

	public void setChecksumme(final String checksumme) {

		this.checksumme = checksumme;
	}

	public boolean isUploadOhneDownloads() {

		return uploadOhneDownloads;
	}

	public void setUploadOhneDownloads(final boolean uploadOhneDownloads) {

		this.uploadOhneDownloads = uploadOhneDownloads;
	}

	public String getJahr() {

		return jahr;
	}

	public void setJahr(final String jahr) {

		this.jahr = jahr;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((checksumme == null) ? 0 : checksumme.hashCode());
		result = prime * result + ((jahr == null) ? 0 : jahr.hashCode());
		result = prime * result + ((kuerzel == null) ? 0 : kuerzel.hashCode());
		result = prime * result + ((teilnahmeart == null) ? 0 : teilnahmeart.hashCode());
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
		final Upload other = (Upload) obj;

		if (checksumme == null) {

			if (other.checksumme != null)
				return false;
		} else if (!checksumme.equals(other.checksumme))
			return false;

		if (jahr == null) {

			if (other.jahr != null)
				return false;
		} else if (!jahr.equals(other.jahr))
			return false;

		if (kuerzel == null) {

			if (other.kuerzel != null)
				return false;
		} else if (!kuerzel.equals(other.kuerzel))
			return false;
		if (teilnahmeart != other.teilnahmeart)
			return false;
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "Upload [kuerzel=" + kuerzel + ", teilnahmeart=" + teilnahmeart + ", mimetype=" + mimetype + ", dateiname="
			+ dateiname + ", uploadOhneDownloads=" + uploadOhneDownloads + ", uploadStatus=" + uploadStatus + ", jahr=" + jahr
			+ "]";
	}

	public UploadStatus getUploadStatus() {

		return uploadStatus;
	}

	public void setUploadStatus(final UploadStatus uploadStatus) {

		this.uploadStatus = uploadStatus;
	}
}
