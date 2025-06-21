// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * KatalogItem
 */
@Schema(name = "KatalogItem", description = "Element eines hierarchisch aufgebauten Katalogs")
public class KatalogItem {

	@JsonProperty
	private Katalogtyp typ;

	@JsonProperty
	private String name;

	@JsonProperty
	private String pfad;

	@JsonProperty
	private String kuerzel;

	@JsonProperty
	private KatalogItem parent;

	@JsonProperty
	private int anzahlKinder;

	@JsonProperty
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

	public String getPfad() {

		return pfad;
	}

	public void setPfad(final String lage) {

		this.pfad = lage;
	}
}
