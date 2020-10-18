// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VertragAdvAPIModel;

/**
 * VertragAuftragsdatenverarbeitung
 */
@AggregateRoot
public class VertragAuftragsdatenverarbeitung {

	private Identifier identifier;

	private Identifier schulkuerzel;

	private Anschrift anschrift;

	private String unterzeichnetAm;

	private Identifier unterzeichnenderLehrer;

	private Vertragstext vertragstext;

	public static VertragAuftragsdatenverarbeitung createFromPayload(final VertragAdvAPIModel payload, final PostleitzahlLand plzLand) {

		return new VertragAuftragsdatenverarbeitung()
			.withSchulkuerzel(new Identifier(payload.schulkuerzel()))
			.withAnschrift(Anschrift.createFromPayload(payload, plzLand));
	}

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

	public String unterzeichnetAmDruck() {

		return unterzeichnetAm.substring(0, 10);
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

	public Anschrift anschrift() {

		return anschrift;
	}

	public VertragAuftragsdatenverarbeitung withAnschrift(final Anschrift anschrift) {

		this.anschrift = anschrift;
		return this;
	}

	public Vertragstext vertragstext() {

		return vertragstext;
	}

	public VertragAuftragsdatenverarbeitung withVertragstext(final Vertragstext vertragstext) {

		this.vertragstext = vertragstext;
		return this;
	}

	public String filename() {

		String name = anschrift.schulname().trim().replaceAll(" ", "_");

		return "Vertrag-Auftragsdatenverarbeitung-" + name + ".pdf";

	}
}
