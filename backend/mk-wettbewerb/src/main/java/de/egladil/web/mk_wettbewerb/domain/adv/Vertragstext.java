// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.adv;

import java.util.Objects;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.semantik.DomainEntity;

/**
 * Vertragstext
 */
@DomainEntity
public class Vertragstext {

	private Identifier identifier;

	private String versionsnummer;

	private String dateiname;

	private String checksumme;

	public Identifier identifier() {

		return identifier;
	}

	public Vertragstext withIdentifier(final Identifier identifier) {

		this.identifier = identifier;
		return this;
	}

	public String versionsnummer() {

		return versionsnummer;
	}

	public Vertragstext withVersionsnummer(final String versionsnummer) {

		this.versionsnummer = versionsnummer;
		return this;
	}

	public String dateiname() {

		return dateiname;

	}

	public Vertragstext withDateiname(final String dateiname) {

		this.dateiname = dateiname;
		return this;
	}

	public String checksumme() {

		return checksumme;
	}

	public Vertragstext withChecksumme(final String checksumme) {

		this.checksumme = checksumme;
		return this;
	}

	@Override
	public int hashCode() {

		return Objects.hash(identifier);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		Vertragstext other = (Vertragstext) obj;
		return Objects.equals(identifier, other.identifier);
	}

	@Override
	public String toString() {

		return dateiname;
	}

}
