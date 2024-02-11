// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import de.egladil.web.mk_gateway.domain.newsletterversand.StatusAuslieferung;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * PersistenteNewsletterauslieferung
 */
@Entity
@Table(name = "NEWSLETTER_AUSLIEFERUNGEN")
@NamedQueries({
	@NamedQuery(
		name = "PersistenteNewsletterauslieferung.FIND_ALL_BY_VERDANDAUFTRAG_ID",
		query = "select a from PersistenteNewsletterauslieferung a where a.versandauftragId = :versandauftragId order by a.sortnummer"),
})
public class PersistenteNewsletterauslieferung extends ConcurrencySafeEntity {

	public static final String FIND_ALL_BY_VERDANDAUFTRAG_ID = "PersistenteNewsletterauslieferung.FIND_ALL_BY_VERDANDAUFTRAG_ID";

	private static final long serialVersionUID = 1L;

	@Column(name = "VERSANDAUFTRAG_UUID")
	private String versandauftragId;

	@Column(name = "EMPFAENGER")
	private String empfaengergruppe;

	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private StatusAuslieferung status;

	@Column(name = "SORTNR")
	private int sortnummer;

	public String getVersandauftragId() {

		return versandauftragId;
	}

	public void setVersandauftragId(final String versandinfoID) {

		this.versandauftragId = versandinfoID;
	}

	public String getEmpfaengergruppe() {

		return empfaengergruppe;
	}

	public void setEmpfaengergruppe(final String emmpfaengergruppe) {

		this.empfaengergruppe = emmpfaengergruppe;
	}

	public StatusAuslieferung getStatus() {

		return status;
	}

	public void setStatus(final StatusAuslieferung status) {

		this.status = status;
	}

	public int getSortnummer() {

		return sortnummer;
	}

	public void setSortnummer(final int sortnummer) {

		this.sortnummer = sortnummer;
	}

}
