// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.adv;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_wettbewerb.domain.error.ImmutableObjectException;
import de.egladil.web.mk_wettbewerb.domain.semantik.ValueObject;

/**
 * Anschrift
 */
@ValueObject
public class Anschrift {

	private String schulname;

	private String strasse;

	private String hausnummer;

	private String ort;

	private String plz;

	private String laendercode;

	public String schulname() {

		return schulname;
	}

	public Anschrift withSchulname(final String schulname) {

		if (StringUtils.isNotBlank(schulname)) {

			throw new ImmutableObjectException("schulname darf nicht geaendert werden");
		}

		if (StringUtils.isBlank(schulname)) {

			throw new IllegalArgumentException("schulname darf nicht blank sein");
		}

		this.schulname = schulname;
		return this;
	}

	public String strasse() {

		return strasse;
	}

	public Anschrift withStrasse(final String strasse) {

		if (StringUtils.isNotBlank(strasse)) {

			throw new ImmutableObjectException("strasse darf nicht geaendert werden");
		}

		if (StringUtils.isBlank(strasse)) {

			throw new IllegalArgumentException("strasse darf nicht blank sein");
		}
		this.strasse = strasse;
		return this;
	}

	public String hausnummer() {

		return hausnummer;
	}

	public Anschrift withHausnummer(final String hausnummer) {

		if (StringUtils.isNotBlank(hausnummer)) {

			throw new ImmutableObjectException("hausnummer darf nicht geaendert werden");
		}

		if (StringUtils.isBlank(hausnummer)) {

			throw new IllegalArgumentException("hausnummer darf nicht blank sein");
		}
		this.hausnummer = hausnummer;
		return this;
	}

	public String ort() {

		return ort;
	}

	public Anschrift withOrt(final String ort) {

		if (StringUtils.isNotBlank(ort)) {

			throw new ImmutableObjectException("ort darf nicht geaendert werden");
		}

		if (StringUtils.isBlank(ort)) {

			throw new IllegalArgumentException("ort darf nicht blank sein");
		}
		this.ort = ort;
		return this;
	}

	public String plz() {

		return plz;
	}

	public Anschrift withPlz(final String plz) {

		if (StringUtils.isNotBlank(plz)) {

			throw new ImmutableObjectException("plz darf nicht geaendert werden");
		}

		if (StringUtils.isBlank(plz)) {

			throw new IllegalArgumentException("plz darf nicht blank sein");
		}
		this.plz = plz;
		return this;
	}

	public String laendercode() {

		return laendercode;

	}

	public Anschrift withLaendercode(final String laendercode) {

		if (StringUtils.isNotBlank(laendercode)) {

			throw new ImmutableObjectException("laendercode darf nicht geaendert werden");
		}

		if (StringUtils.isBlank(laendercode)) {

			throw new IllegalArgumentException("laendercode darf nicht blank sein");
		}
		this.laendercode = laendercode;
		return this;
	}

}
