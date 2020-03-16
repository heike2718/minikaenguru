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

import de.egladil.web.mk_commons.domain.enums.UploadMimeType;

/**
 * Dateidaten
 */
@Embeddable
public class Dateidaten {

	@NotNull
	@Column(name = "MIMETYPE")
	@Enumerated(EnumType.STRING)
	private UploadMimeType mimetype;

	@NotNull
	@Size(max = 50)
	@Column(name = "DATEINAME")
	private String dateiname;

	@NotNull
	@Size(min = 1, max = 40)
	@Column(name = "CHECKSUMME")
	private String checksumme;

	@Column(name = "OHNE_DOWNLOADS")
	private boolean uploadOhneDownloads;

	@Override
	public String toString() {

		return "Dateidaten [dateiname=" + dateiname + ", mimetype=" + mimetype + ", checksumme=" + checksumme + "]";
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

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((checksumme == null) ? 0 : checksumme.hashCode());
		result = prime * result + ((dateiname == null) ? 0 : dateiname.hashCode());
		result = prime * result + ((mimetype == null) ? 0 : mimetype.hashCode());
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
		final Dateidaten other = (Dateidaten) obj;

		if (checksumme == null) {

			if (other.checksumme != null)
				return false;
		} else if (!checksumme.equals(other.checksumme))
			return false;

		if (dateiname == null) {

			if (other.dateiname != null)
				return false;
		} else if (!dateiname.equals(other.dateiname))
			return false;
		if (mimetype != other.mimetype)
			return false;
		return true;
	}

	public boolean isUploadOhneDownloads() {

		return uploadOhneDownloads;
	}

	public void setUploadOhneDownloads(final boolean uploadOhneDownloads) {

		this.uploadOhneDownloads = uploadOhneDownloads;
	}

}
