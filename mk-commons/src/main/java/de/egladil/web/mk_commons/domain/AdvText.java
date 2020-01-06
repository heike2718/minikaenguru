// =====================================================
// Projekt: de.egladil.mkv.persistence
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.Dateiname;
import de.egladil.web.commons_validation.annotations.DeutscherName;
import de.egladil.web.commons_validation.annotations.UuidString;

/**
 * AdvText
 */
@Entity
@Table(name = "adv_texte")
public class AdvText implements IDomainObject {

	/* serialVersionUID */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "VERSION")
	private int version;

	@NotBlank
	@DeutscherName
	@Column(name = "VERSIONSNUMMER")
	private String versionsnummer;

	@NotNull
	@Size(max = 150)
	@Dateiname
	@Column(name = "DATEINAME")
	private String dateiname;

	@NotNull
	@Size(min = 1, max = 128)
	@UuidString
	@Column(name = "CHECKSUMME")
	private String checksumme;

	@Override
	public Long getId() {

		return id;
	}

	public String getVersionsnummer() {

		return versionsnummer;
	}

	public void setVersionsnummer(final String versionsnummer) {

		this.versionsnummer = versionsnummer;
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

}
