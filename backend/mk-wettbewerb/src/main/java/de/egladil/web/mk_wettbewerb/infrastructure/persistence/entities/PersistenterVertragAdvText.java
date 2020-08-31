// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * PersistenterVertragAdvText
 */
@Entity
@Table(name = "VERTRAEGE_ADV_TEXTE")
public class PersistenterVertragAdvText extends ConcurrencySafeEntity {

	@NotNull
	@Size(max = 20)
	@Column(name = "VERSIONSNUMMER")
	private String versionsnummer;

	@NotNull
	@Size(max = 150)
	@Column(name = "DATEINAME")
	private String dateiname;

	@NotNull
	@Size(max = 128)
	@Column(name = "CHECKSUMME")
	private String checksumme;

	public String getVersionsnummer() {

		return versionsnummer;
	}

	public void setVersionsnummer(final String vertragsnummer) {

		this.versionsnummer = vertragsnummer;
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
