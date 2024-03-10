// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * PersistenterVertragAdvText
 */
@Entity
@Table(name = "VERTRAEGE_ADV_TEXTE")
@NamedQueries({
	@NamedQuery(
		name = "PersistenterVertragAdvText.LOAD",
		query = "select t from PersistenterVertragAdvText t")
})
public class PersistenterVertragAdvText extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 1040338551434619409L;

	public static final String LOAD = "PersistenterVertragAdvText.LOAD";

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
