// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import de.egladil.web.mk_commons.domain.IDownload;
import de.egladil.web.mk_commons.domain.IMkEntity;

/**
 * Download
 */
@Entity
@Table(name = "privatdownloads")
public class Privatdownload implements IMkEntity, IDownload {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@NotNull
	@Embedded
	private Downloaddaten downloaddaten;

	@ManyToOne()
	@JoinColumn(name = "KONTO")
	private Privatkonto privatkonto;

	@Version
	@Column(name = "VERSION")
	private int version;

	@Override
	public String toString() {

		return "Privatdownload [downloaddaten=" + downloaddaten + "]";
	}

	public Long getId() {

		return id;
	}

	@Override
	public Downloaddaten getDownloaddaten() {

		return downloaddaten;
	}

	@Override
	public void setDownloaddaten(final Downloaddaten downloaddaten) {

		this.downloaddaten = downloaddaten;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	public Privatkonto getPrivatkonto() {

		return privatkonto;
	}

	public void setPrivatkonto(final Privatkonto privatkonto) {

		this.privatkonto = privatkonto;
	}
}
