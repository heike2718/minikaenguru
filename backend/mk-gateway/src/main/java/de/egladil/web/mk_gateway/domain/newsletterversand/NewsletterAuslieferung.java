// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;

/**
 * NewsletterAuslieferung stellt die Auslieferung eines gegebenen Newsletters an eine Gruppe von Empfaengern dar, also einer
 * Sammelmail mit dem Newslettertext.
 */
@AggregateRoot
public class NewsletterAuslieferung {

	@JsonProperty
	private Identifier identifier;

	@JsonProperty
	private Identifier versandauftragId;

	@JsonProperty
	private int sortnummer;

	@JsonProperty
	private StatusAuslieferung status;

	@JsonProperty
	private String[] empfaenger;

	public Identifier getIdentifier() {

		return identifier;
	}

	public NewsletterAuslieferung withIdentifier(final Identifier identifier) {

		this.identifier = identifier;
		return this;
	}

	public Identifier getVersandauftragId() {

		return versandauftragId;
	}

	public NewsletterAuslieferung withVersandauftragId(final Identifier versandauftragId) {

		this.versandauftragId = versandauftragId;
		return this;
	}

	public StatusAuslieferung getStatus() {

		return status;
	}

	public NewsletterAuslieferung withStatus(final StatusAuslieferung status) {

		this.status = status;
		return this;
	}

	public void setStatus(final StatusAuslieferung status) {

		this.status = status;
	}

	public String[] getEmpfaenger() {

		return empfaenger;
	}

	public NewsletterAuslieferung withEmpfaenger(final String[] empfaenger) {

		this.empfaenger = empfaenger;
		return this;
	}

	public int getSortnummer() {

		return sortnummer;
	}

	public NewsletterAuslieferung withSortnummer(final int sortnummer) {

		this.sortnummer = sortnummer;
		return this;
	}
}
