// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.adv;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.semantik.Aggregate;

/**
 * VertragAuftragsdatenverarbeitung
 */
@Aggregate
public class VertragAuftragsdatenverarbeitung {

	@NotNull
	private Identifier identifier;

	@NotNull
	private Identifier schulkuerzel;

	@NotNull
	private Anschrift anschrift;

	@NotBlank
	private String unterzeichnetAm;

	@NotNull
	private Identifier unterzeichnenderLehrer;

	@NotNull
	private Identifier idVertragstext;

	public String uuid() {

		return identifier.identifier();
	}

	public Identifier identifier() {

		return identifier;
	}

	public VertragAuftragsdatenverarbeitung withIdentifier(final Identifier identifier) {

		this.identifier = identifier;
		return this;
	}

	public Identifier schulkuerzel() {

		return schulkuerzel;
	}

	public VertragAuftragsdatenverarbeitung withSchulkuerzel(final Identifier schulkuerzel) {

		this.schulkuerzel = schulkuerzel;
		return this;
	}

	public String unterzeichnetAm() {

		return unterzeichnetAm;
	}

	public VertragAuftragsdatenverarbeitung withUnterzeichnetAm(final String unterzeichnetAm) {

		this.unterzeichnetAm = unterzeichnetAm;
		return this;
	}

	public Identifier unterzeichnenderLehrer() {

		return unterzeichnenderLehrer;
	}

	public VertragAuftragsdatenverarbeitung withUnterzeichnenderLehrer(final Identifier unterzeichnenderLehrer) {

		this.unterzeichnenderLehrer = unterzeichnenderLehrer;
		return this;
	}

	public Identifier idVertragstext() {

		return idVertragstext;
	}

	public VertragAuftragsdatenverarbeitung withVertragstext(final Identifier idVertragstext) {

		this.idVertragstext = idVertragstext;
		return this;
	}

	public Anschrift anschrift() {

		return anschrift;
	}

	public VertragAuftragsdatenverarbeitung withAnschrift(final Anschrift anschrift) {

		this.anschrift = anschrift;
		return this;
	}

}
