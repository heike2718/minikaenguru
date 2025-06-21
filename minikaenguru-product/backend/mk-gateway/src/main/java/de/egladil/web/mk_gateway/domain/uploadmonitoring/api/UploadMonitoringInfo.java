// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploadmonitoring.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;

/**
 * UploadMonitoringInfo
 */
public class UploadMonitoringInfo {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private String nameLehrer;

	@JsonProperty
	private String emailLehrer;

	@JsonProperty
	private DateiTyp dateiTyp;

	@JsonProperty
	private UploadStatus uploadStatus;

	@JsonProperty
	private UploadType uploadType;

	@JsonProperty
	private String uploadDatum;

	@JsonProperty
	private long sortnumber;

	@JsonProperty
	private String fileName;

	@JsonProperty
	private String nameFehlerreport;

	@JsonProperty
	private SchuleAPIModel schule;

	public String getUuid() {

		return uuid;
	}

	public UploadMonitoringInfo withUuid(final String uuid) {

		this.uuid = uuid;
		return this;
	}

	public String getTeilnahmenummer() {

		return teilnahmenummer;
	}

	public UploadMonitoringInfo withTeilnahmenummer(final String teilnahmenummer) {

		this.teilnahmenummer = teilnahmenummer;
		return this;
	}

	public String getNameLehrer() {

		return nameLehrer;
	}

	public UploadMonitoringInfo withNameLehrer(final String nameLehrer) {

		this.nameLehrer = nameLehrer;
		return this;
	}

	public String getEmailLehrer() {

		return emailLehrer;
	}

	public UploadMonitoringInfo withEmailLehrer(final String emailLehrer) {

		this.emailLehrer = emailLehrer;
		return this;
	}

	public DateiTyp getDateiTyp() {

		return dateiTyp;
	}

	public UploadMonitoringInfo withDateiTyp(final DateiTyp dateiTyp) {

		this.dateiTyp = dateiTyp;
		return this;
	}

	public UploadStatus getUploadStatus() {

		return uploadStatus;
	}

	public UploadMonitoringInfo withUploadStatus(final UploadStatus uploadStatus) {

		this.uploadStatus = uploadStatus;
		return this;
	}

	public UploadType getUploadType() {

		return uploadType;
	}

	public UploadMonitoringInfo withUploadType(final UploadType uploadType) {

		this.uploadType = uploadType;
		return this;
	}

	public String getUploadDatum() {

		return uploadDatum;
	}

	public void setUploadDatum(final String uploadDatum) {

		this.uploadDatum = uploadDatum;
	}

	public long getSortnumber() {

		return sortnumber;
	}

	public UploadMonitoringInfo withSortnumber(final long sortnumber) {

		this.sortnumber = sortnumber;
		return this;
	}

	public String getFileName() {

		return fileName;
	}

	public UploadMonitoringInfo withFileName(final String fileName) {

		this.fileName = fileName;
		return this;
	}

	public String getNameFehlerreport() {

		return nameFehlerreport;
	}

	public UploadMonitoringInfo withNameFehlerreport(final String nameFehlerreport) {

		this.nameFehlerreport = nameFehlerreport;
		return this;
	}

	public SchuleAPIModel getSchule() {

		return schule;
	}

	public void setSchule(final SchuleAPIModel schulkatalogItem) {

		this.schule = schulkatalogItem;
	}
}
