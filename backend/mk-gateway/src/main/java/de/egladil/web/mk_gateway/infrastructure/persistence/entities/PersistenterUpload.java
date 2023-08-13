// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;

/**
 * PersistenterUpload
 */
@Entity
@Table(name = "UPLOADS")
@NamedQueries({
	@NamedQuery(
		name = "PersistenterUpload.FIND_BY_UPLOAD_TYPE_AND_TEILNAHMENUMMER",
		query = "select u from PersistenterUpload u where u.teilnahmenummer = :teilnahmenummer and u.uploadType = :uploadType order by u.uploadDate"),
	@NamedQuery(
		name = "PersistenterUpload.FIND_BY_IDENTIFIER",
		query = "select u from PersistenterUpload u where u.teilnahmenummer = :teilnahmenummer and u.checksumme = :checksumme")
})
public class PersistenterUpload extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 1L;

	public static final String FIND_BY_IDENTIFIER = "PersistenterUpload.FIND_BY_IDENTIFIER";

	@Column(name = "BENUTZER_UUID")
	private String benutzerUuid;

	@Column(name = "TEILNAHMENUMMER")
	private String teilnahmenummer;

	@Column(name = "DATEINAME")
	private String dateiname;

	@Column(name = "UPLOAD_TYPE")
	@Enumerated(EnumType.STRING)
	private UploadType uploadType;

	@Column(name = "MEDIATYPE")
	private String mediatype;

	@Column(name = "CHARSET")
	private String encoding;

	@Column(name = "CHECKSUMME")
	private Long checksumme;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private UploadStatus status;

	@Column(name = "DATE_UPLOAD")
	private Date uploadDate;

	@Column(name = "SORTNR")
	private long sortNumber;

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

	public UploadType getUploadType() {

		return uploadType;
	}

	public void setUploadType(final UploadType uploadTyp) {

		this.uploadType = uploadTyp;
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

	public String getEncoding() {

		return encoding;
	}

	public void setEncoding(final String encoding) {

		this.encoding = encoding;
	}

	public long getSortNumber() {

		return sortNumber;
	}

	public void setSortNumber(final long sortNumber) {

		this.sortNumber = sortNumber;
	}

}
