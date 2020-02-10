// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import java.util.List;

import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.commons_validation.annotations.StringLatin;

/**
 * KatalogItem
 */
public class KatalogItem {

	private Katalogtyp typ;

	@Kuerzel
	private String kuerzel;

	@StringLatin
	@Size(max = 110)
	private String name;

	private List<KatalogItem> kinder;

	public static KatalogItem createLazy(final Katalogtyp typ, final String kuerzel, final String name) {

		KatalogItem result = new KatalogItem();
		result.typ = typ;
		result.kuerzel = kuerzel;
		result.name = name;
		return result;
	}

	/**
	 *
	 */
	public KatalogItem() {

	}

	public KatalogItem withKinder(final List<KatalogItem> kinder) {

		this.kinder = kinder;
		return this;
	}

	public Katalogtyp getTyp() {

		return typ;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public String getName() {

		return name;
	}

	public List<KatalogItem> getKinder() {

		return kinder;
	}

}
