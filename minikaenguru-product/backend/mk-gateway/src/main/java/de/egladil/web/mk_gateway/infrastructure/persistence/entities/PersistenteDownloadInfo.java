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
import jakarta.validation.constraints.Size;

import de.egladil.web.mk_gateway.domain.unterlagen.DownloadType;

/**
 * PersistenteDownloadInfo
 */
@Entity
@Table(name = "DOWNLOADS")
@NamedQueries({
	@NamedQuery(
		name = "PersistenteDownloadInfo.FIND_DOWNLOAD_BY_VERANSTALTER_JAHR",
		query = "select d from PersistenteDownloadInfo d where d.veranstalterUuid = :veranstalterUuid AND d.jahr = :jahr order by d.sortNumber"),
	@NamedQuery(
		name = "PersistenteDownloadInfo.FIND_DOWNLOAD_BY_VERANSTALTER_JAHR_TYPE",
		query = "select d from PersistenteDownloadInfo d where d.veranstalterUuid = :veranstalterUuid AND d.jahr = :jahr and d.downloadType = :downloadType order by d.sortNumber")
})
public class PersistenteDownloadInfo extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 4425087009158588453L;

	public static final String FIND_DOWNLOAD_BY_VERANSTALTER_JAHR = "PersistenteDownloadInfo.FIND_DOWNLOAD_BY_VERANSTALTER_JAHR";

	public static final String FIND_DOWNLOAD_BY_VERANSTALTER_JAHR_TYPE = "PersistenteDownloadInfo.FIND_DOWNLOAD_BY_VERANSTALTER_JAHR_TYPE";

	@Column(name = "VERANSTALTER_UUID")
	@Size(max = 36)
	private String veranstalterUuid;

	@Column(name = "JAHR")
	private Integer jahr;

	@Column(name = "DOWNLOAD_TYPE")
	@Enumerated(EnumType.STRING)
	private DownloadType downloadType;

	@Column
	private int anzahl;

	@Column(name = "DATE_DOWNLOAD")
	private Date downloadDate;

	@Column(name = "SORTNR")
	private long sortNumber;

	public String getVeranstalterUuid() {

		return veranstalterUuid;
	}

	public void setVeranstalterUuid(final String veranstalterUuid) {

		this.veranstalterUuid = veranstalterUuid;
	}

	public Integer getJahr() {

		return jahr;
	}

	public void setJahr(final Integer jahr) {

		this.jahr = jahr;
	}

	public DownloadType getDownloadType() {

		return downloadType;
	}

	public void setDownloadType(final DownloadType downloadType) {

		this.downloadType = downloadType;
	}

	public int getAnzahl() {

		return anzahl;
	}

	public void setAnzahl(final int anzahl) {

		this.anzahl = anzahl;
	}

	public Date getDownloadDate() {

		return downloadDate;
	}

	public void setDownloadDate(final Date downloadDate) {

		this.downloadDate = downloadDate;
	}

	public long getSortNumber() {

		return sortNumber;
	}

	public void setSortNumber(final long sortNumber) {

		this.sortNumber = sortNumber;
	}

}
