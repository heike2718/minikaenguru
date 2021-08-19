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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;

/**
 * PersistenterUpload
 */
@Entity
@Table(name = "UPLOADS")
@NamedQueries({
	@NamedQuery(
		name = "PersistenterUpload.FIND_BY_TEILNAHMENUMMER",
		query = "select u from PersistenterUpload u where u.teilnahmenummer = :teilnahmenummer"),
	@NamedQuery(
		name = "PersistenterUpload.FIND_BY_IDENTIFIER",
		query = "select u from PersistenterUpload u where u.teilnahmenummer = :teilnahmenummer and u.checksumme = :checksumme")
})
public class PersistenterUpload extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 1L;

	public static final String FIND_BY_TEILNAHMENUMMER = "PersistenterUpload.FIND_BY_TEILNAHMENUMMER";

	public static final String FIND_BY_IDENTIFIER = "PersistenterUpload.FIND_BY_IDENTIFIER";

	@Column(name = "BENUTZER_UUID")
	private String benutzerUuid;

	@Column(name = "TEILNAHMENUMMER")
	private String teilnahmenummer;

	@Column(name = "DATEINAME")
	private String dateiname;

	@Column(name = "UPLOAD_TYPE")
	@Enumerated(EnumType.STRING)
	private UploadType uploadTyp;

	@Column(name = "MEDIATYPE")
	private String mediatype;

	@Column(name = "CHECKSUMME")
	private Long checksumme;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private UploadStatus status;

	@Column(name = "DATE_UPLOAD")
	private Date uploadDate;

	public String getBenutzerUuid() {

		return benutzerUuid;
	}

	public void setBenutzerUuid(final String veranstalterUuid) {

		this.benutzerUuid = veranstalterUuid;
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

	public Long getChecksumme() {

		return checksumme;
	}

	public void setChecksumme(final Long checksumme) {

		this.checksumme = checksumme;
	}

}