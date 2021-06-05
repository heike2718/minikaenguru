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

	private final List<Klasse> klassen;

	private final List<Kind> kinder;

	public KlassenImportErgebnis(final List<Klasse> klassen, final List<Kind> kinder) {

		super();
		this.klassen = klassen;
		this.kinder = kinder;
	}

	public List<Klasse> getKlassen() {

		return klassen;
	}

	public List<Kind> getKinder() {

		return kinder;
	}

}
