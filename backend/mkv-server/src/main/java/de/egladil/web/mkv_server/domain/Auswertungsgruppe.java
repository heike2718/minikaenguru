// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.domain;

import java.util.ArrayList;
import java.util.List;

import de.egladil.web.mk_commons.exception.MkRuntimeException;

/**
 * Auswertungsgruppe
 */
public class Auswertungsgruppe {

	private String kuerzel;

	private String schulkuerzel;

	private String name;

	private Klassenstufe klassenstufe;

	private Auswertungsgruppe schule;

	private List<Auswertungsgruppe> klassen = new ArrayList<>();

	public static Auswertungsgruppe createSchule(final String kuerzel, final String schulkuerzel, final String name) {

		Auswertungsgruppe result = new Auswertungsgruppe();
		result.kuerzel = kuerzel;
		result.schulkuerzel = schulkuerzel;
		result.name = name;
		return result;
	}

	public static Auswertungsgruppe createKlasse(final Auswertungsgruppe schule, final String kuerzel, final Klassenstufe klassenstufe, final String name) {

		Auswertungsgruppe result = new Auswertungsgruppe();
		result.kuerzel = kuerzel;
		result.schulkuerzel = schule.getSchulkuerzel();
		result.klassenstufe = klassenstufe;
		schule.addKlasse(result);
		result.schule = schule;
		return result;
	}

	/**
	 *
	 */
	Auswertungsgruppe() {

	}

	void addKlasse(final Auswertungsgruppe klasse) {

		if (this.klassenstufe != null) {

			throw new MkRuntimeException("Einer Klasse kann keine weitere Klasse zugeordnet werden");
		}

		if (klasse == null) {

			throw new IllegalArgumentException("klasse null");
		}

		klassen.add(klasse);
	}

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public String getSchulkuerzel() {

		return schulkuerzel;
	}

	public Klassenstufe getKlassenstufe() {

		return klassenstufe;
	}

	public Auswertungsgruppe getSchule() {

		return schule;
	}

	public List<Auswertungsgruppe> getKlassen() {

		return klassen;
	}
}
