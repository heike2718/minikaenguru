// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.testdaten.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.Identifier;

/**
 * InMemoryTeilnahme
 */
public class InMemoryTeilnahme {

	@JsonIgnore
	public static final String ART_PRIVAT = "PRIVAT";

	@JsonIgnore
	public static final String ART_SCHULE = "SCHULE";

	@JsonProperty
	private Integer wettbewerbJahr;

	@JsonProperty
	private Identifier teilnahmenummer;

	@JsonProperty
	private String art;

	@JsonProperty
	private String nameSchule;

	@JsonProperty
	private Identifier angemeldetDurchVeranstalterId;

	public static InMemoryTeilnahme createSchulteilnahme() {

		InMemoryTeilnahme result = new InMemoryTeilnahme();
		result.angemeldetDurchVeranstalterId = new Identifier("ghGUIGAZSGDZQ");
		result.art = ART_SCHULE;
		result.nameSchule = "bjalhha-";
		result.teilnahmenummer = new Identifier("ABCDEFGH");
		result.wettbewerbJahr = 2020;

		return result;

	}

	public static InMemoryTeilnahme createPrivatteilnahme() {

		InMemoryTeilnahme result = new InMemoryTeilnahme();
		result.art = ART_PRIVAT;
		result.teilnahmenummer = new Identifier("ABCDEFGH");
		result.wettbewerbJahr = 2020;

		return result;

	}

	public Integer getWettbewerbJahr() {

		return wettbewerbJahr;
	}

	public Identifier getTeilnahmenummer() {

		return teilnahmenummer;
	}

	public String getArt() {

		return art;
	}

	public String getNameSchule() {

		return nameSchule;
	}

	public Identifier getAngemeldetDurchVeranstalterId() {

		return angemeldetDurchVeranstalterId;
	}

}
