// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

/**
 * KatalogItem
 */
public class KatalogItem {

	private Katalogtyp typ;

	private String name;

	private String kuerzel;

	private KatalogItem parent;

	private int anzahlKinder;

	private boolean leaf;

	public static KatalogItem createWithTypKuerzelName(final Katalogtyp typ, final String kuerzel, final String name, final int anzahlKinder) {

		KatalogItem result = new KatalogItem();
		result.typ = typ;
		result.kuerzel = kuerzel;
		result.name = name;
		result.anzahlKinder = anzahlKinder;

		switch (typ) {

		case SCHULE:
			result.leaf = true;
			break;

		default:
			break;
		}

		return result;
	}

	/**
	 *
	 */
	KatalogItem() {

	}

	public KatalogItem getParent() {

		return parent;
	}

	public void setParent(final KatalogItem parent) {

		this.parent = parent;
	}

	public Katalogtyp getTyp() {

		return typ;
	}

	public String getName() {

		return name;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public int getAnzahlKinder() {

		return anzahlKinder;
	}

	public boolean isLeaf() {

		return leaf;
	}
}
