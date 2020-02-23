// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

/**
 * InverseKatalogItem
 */
public class InverseKatalogItem {

	private Katalogtyp typ;

	private String name;

	private String kuerzel;

	private InverseKatalogItem parent;

	private int anzahlKinder;

	private boolean leaf;

	public static InverseKatalogItem createWithTypKuerzelName(final Katalogtyp typ, final String kuerzel, final String name) {

		InverseKatalogItem result = new InverseKatalogItem();
		result.typ = typ;
		result.kuerzel = kuerzel;
		result.name = name;
		return result;
	}

	/**
	 *
	 */
	InverseKatalogItem() {

	}

	public InverseKatalogItem getParent() {

		return parent;
	}

	public void setParent(final InverseKatalogItem parent) {

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

	public void setAnzahlKinder(final int anzahlKinder) {

		this.anzahlKinder = anzahlKinder;
	}

	public boolean isLeaf() {

		return leaf;
	}

	public void setLeaf(final boolean leaf) {

		this.leaf = leaf;
	}

}
