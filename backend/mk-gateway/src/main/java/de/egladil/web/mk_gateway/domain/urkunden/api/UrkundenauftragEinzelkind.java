// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_gateway.domain.urkunden.Farbschema;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenart;

/**
 * UrkundenauftragEinzelkind
 */
public class UrkundenauftragEinzelkind {

	@JsonProperty
	@NotNull
	private Urkundenart urkundenart;

	@JsonProperty
	@NotBlank
	@UuidString
	private String kindUuid;

	/** Tagesdatum im Format dd.MM.yyyy, das auf die Urkunde gedruckt werden soll. */
	@JsonProperty
	private String dateString;

	@JsonProperty
	@NotNull
	private Farbschema farbschema;

	public String dateString() {

		return dateString;
	}

	public UrkundenauftragEinzelkind withDateString(final String dateString) {

		this.dateString = dateString;
		return this;
	}

	public Farbschema farbschema() {

		return farbschema;
	}

	public UrkundenauftragEinzelkind withFarbschema(final Farbschema farbschema) {

		this.farbschema = farbschema;
		return this;
	}

	public Urkundenart urkundenart() {

		return urkundenart;
	}

	public UrkundenauftragEinzelkind withUrkundenart(final Urkundenart urkundenart) {

		this.urkundenart = urkundenart;
		return this;
	}

	public String kindUuid() {

		return kindUuid;
	}

	public UrkundenauftragEinzelkind withKindUuid(final String kindUuid) {

		this.kindUuid = kindUuid;
		return this;
	}

}
