// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.domain.ITeilnahmeIdentifierProvider;
import de.egladil.web.mk_commons.domain.enums.Teilnahmeart;
import de.egladil.web.mk_commons.domain.enums.UploadMimeType;
import de.egladil.web.mk_commons.domain.enums.UploadStatus;
import de.egladil.web.mk_commons.exception.MkRuntimeException;

/**
 * UploadInfo
 */
@Entity
@Table(name = "vw_uploadinfos")
public class UploadInfo implements IMkEntity, ITeilnahmeIdentifierProvider {

	@Id
	@Column(name = "ID")
	@JsonIgnore
	private Long id;

	@Column(name = "TEILNAHMEKUERZEL")
	@JsonProperty
	private String teilnahmekuerzel;

	@Column(name = "TEILNAHMEART")
	@Enumerated(EnumType.STRING)
	@JsonProperty
	private Teilnahmeart teilnahmeart;

	@Column(name = "CHECKSUMME")
	@JsonProperty
	private String checksumme;

	@Column(name = "MIMETYPE")
	@Enumerated(EnumType.STRING)
	@JsonProperty
	private UploadMimeType mimetype;

	@Column(name = "DATEINAME")
	@JsonProperty
	private String dateiname;

	@Column(name = "JAHR")
	@JsonProperty
	private String jahr;

	@Column(name = "LANDKUERZEL")
	@JsonProperty
	private String landkuerzel;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	@JsonProperty
	private UploadStatus uploadStatus;

	public Long getId() {

		return id;
	}

	public String getTeilnahmekuerzel() {

		return teilnahmekuerzel;
	}

	public Teilnahmeart getTeilnahmeart() {

		return teilnahmeart;
	}

	public UploadMimeType getMimetype() {

		return mimetype;
	}

	public String getDateiname() {

		return dateiname;
	}

	public String getJahr() {

		return jahr;
	}

	public String getLandkuerzel() {

		return landkuerzel;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		final StringBuffer sb = new StringBuffer();
		sb.append("UploadInfo [id=");
		sb.append(id);
		sb.append(", teilnahmekuerzel=");
		sb.append(teilnahmekuerzel);
		sb.append(", teilnahmeart=");
		sb.append(teilnahmeart);
		sb.append(", mimetype=");
		sb.append(mimetype);
		sb.append(", landkuerzel=");
		sb.append(landkuerzel);
		sb.append(", uploadStatus=");
		sb.append(uploadStatus);
		sb.append(", dateiname=");
		sb.append(dateiname);
		sb.append(", jahr=");
		sb.append(jahr);
		sb.append("]");
		return sb.toString();
	}

	public UploadStatus getUploadStatus() {

		return uploadStatus;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	public void setMimetype(final UploadMimeType mimetype) {

		this.mimetype = mimetype;
	}

	public void setDateiname(final String dateiname) {

		this.dateiname = dateiname;
	}

	@Override
	public TeilnahmeIdentifier provideTeilnahmeIdentifier() {

		switch (teilnahmeart) {

		case S:
			return TeilnahmeIdentifier.createSchulteilnahmeIdentifier(teilnahmekuerzel, jahr);

		case P:
			return TeilnahmeIdentifier.createPrivatteilnahmeIdentifier(teilnahmekuerzel, jahr);

		default:
			throw new MkRuntimeException("unbekannte Teilnahmeart " + teilnahmeart);
		}
	}
}
