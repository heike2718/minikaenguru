// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;

/**
 * PersistenterUpload
 */
@Entity
@Table(name = "UPLOADS")
public class PersistenterUpload extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "VERANSTALTER_UUID")
	private String veranstalterUuid;

	@Column(name = "TEILNAHMENUMMER")
	private String teilnahmenummer;

	@Column(name = "DATEINAME")
	private String dateiname;

	@Column(name = "UPLOAD_TYPE")
	@Enumerated(EnumType.STRING)
	private UploadType uploadTyp;

	@Column(name = "MEDIATYPE")
	private String mediatype;

	@Column(name = "CHARSET")
	private String charset;

	@Column(name = "CHECKSUMME")
	private String checksumme;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private UploadStatus status;

	@Column(name = "DATE_UPLOAD")
	private Date uploadDate;

	public String getVeranstalterUuid() {

		return veranstalterUuid;
	}

	public void setVeranstalterUuid(final String veranstalterUuid) {

		this.veranstalterUuid = veranstalterUuid;
	}

	public String getTeilnahmenummer() {

		return teilnahmenummer;
	}

	public void setTeilnahmenummer(final String teilnahmenummer) {

		this.teilnahmenummer = teilnahmenummer;
	}

	public String getDateiname() {

		return dateiname;
	}

	public void setDateiname(final String dateiname) {

		this.dateiname = dateiname;
	}

	public UploadType getUploadTyp() {

		return uploadTyp;
	}

	public void setUploadTyp(final UploadType uploadTyp) {

		this.uploadTyp = uploadTyp;
	}

	public String getMediatype() {

		return mediatype;
	}

	public void setMediatype(final String mimetype) {

		this.mediatype = mimetype;
	}

	public String getCharset() {

		return charset;
	}

	public void setCharset(final String charset) {

		this.charset = charset;
	}

	public String getChecksumme() {

		return checksumme;
	}

	public void setChecksumme(final String checksumme) {

		this.checksumme = checksumme;
	}

	public UploadStatus getStatus() {

		return status;
	}

	public void setStatus(final UploadStatus status) {

		this.status = status;
	}

	public Date getUploadDate() {

		return uploadDate;
	}

	public void setUploadDate(final Date uploadDate) {

		this.uploadDate = uploadDate;
	}

}
