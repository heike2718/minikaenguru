// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.Aggregate;

/**
 * VertragAuftragsdatenverarbeitung
 */
@Aggregate
public class VertragAuftragsdatenverarbeitung {

	private Identifier identifier;

	private Identifier schulkuerzel;

	private String versionsnummer;

	private String dateinameVertragstext;

	private Anschrift anschrift;

	private String unterzeichnetAm;

	private Identifier unterzeichnenderLehrer;

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

	public String filename() {

		String name = anschrift.schulname().trim().replaceAll(" ", "_");

		return "Vertrag-Auftragsdatenverarbeitung-" + name + ".pdf";

	}

	public String versionsnummer() {

		return versionsnummer;
	}

	public VertragAuftragsdatenverarbeitung withVersionsnummer(final String versionsnummer) {

		this.versionsnummer = versionsnummer;
		return this;
	}

	public String dateinameVertragstext() {

		return dateinameVertragstext;
	}

	public VertragAuftragsdatenverarbeitung withDateinameVertragstext(final String textDateiname) {

		this.dateinameVertragstext = textDateiname;
		return this;
	}
}
