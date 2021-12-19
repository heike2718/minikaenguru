// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;

/**
 * UploadsMonitoringViewItem
 */
@Entity
@Table(name = "VW_UPLOADS")
@NamedQueries({
	@NamedQuery(
		name = "UploadsMonitoringViewItem.FIND_BY_UPLOAD_TYPE_AND_TEILNAHMENUMMER",
		query = "select u from UploadsMonitoringViewItem u where u.teilnahmenummer = :teilnahmenummer and u.uploadType = :uploadType order by u.sortNumber desc"),
	@NamedQuery(
		name = "UploadsMonitoringViewItem.LOAD_PAGE",
		query = "select u from UploadsMonitoringViewItem u ORDER by u.sortNumber desc"),
})
public class UploadsMonitoringViewItem {

	public static final String FIND_BY_UPLOAD_TYPE_AND_TEILNAHMENUMMER = "UploadsMonitoringViewItem.FIND_BY_UPLOAD_TYPE_AND_TEILNAHMENUMMER";

	public static final String LOAD_PAGE = "UploadsMonitoringViewItem.LOAD_PAGE";

	@Id
	@Column(name = "UUID")
	private String uuid;

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

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private UploadStatus status;

	@Column(name = "DATE_UPLOAD")
	private Date uploadDate;

	@Column(name = "FULL_NAME")
	private String nameLehrer;

	@Column(name = "EMAIL")
	private String emailLehrer;

	@Column(name = "SORTNR")
	private long sortNumber;

	@Override
	public int hashCode() {

		return Objects.hash(uuid);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		UploadsMonitoringViewItem other = (UploadsMonitoringViewItem) obj;
		return Objects.equals(uuid, other.uuid);
	}

	public String getUuid() {

		return uuid;
	}

	public String getBenutzerUuid() {

		return benutzerUuid;
	}

	public String getTeilnahmenummer() {

		return teilnahmenummer;
	}

	public String getDateiname() {

		return dateiname;
	}

	public UploadType getUploadType() {

		return uploadType;
	}

	public String getMediatype() {

		return mediatype;
	}

	public String getEncoding() {

		return encoding;
	}

	public UploadStatus getStatus() {

		return status;
	}

	public Date getUploadDate() {

		return uploadDate;
	}

	public String getNameLehrer() {

		return nameLehrer;
	}

	public String getEmailLehrer() {

		return emailLehrer;
	}

	public long getSortNumber() {

		return sortNumber;
	}

	public void setSortNumber(final long sortNumber) {

		this.sortNumber = sortNumber;
	}

}
