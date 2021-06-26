// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.impl;

import java.util.List;

import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;

/**
 * KlassenImportErgebnis
 */
public class KlassenImportErgebnis {

	private List<Klasse> klassen;

	private final List<Kind> kinder;

	private final List<KindImportDaten> kindImportDaten;

	public KlassenImportErgebnis(final List<KindImportDaten> kindImportDaten, final List<Kind> kinder) {

		super();
		this.kindImportDaten = kindImportDaten;
		this.kinder = kinder;
	}

	public List<Klasse> getKlassen() {

		return klassen;
	}

	public List<KindImportDaten> getKindImportDaten() {

		return kindImportDaten;
	}

	public List<Kind> getKinder() {

		return kinder;
	}

	public void setKlassen(final List<Klasse> klassen) {

		this.klassen = klassen;
	}

}
