// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import de.egladil.web.mk_gateway.domain.klassenlisten.impl.KindImportDaten;

/**
 * KindImportVO
 */
public class KindImportVO {

	private final KlassenimportZeile importZeile;

	private final KindImportDaten kindImportDaten;

	/**
	 * @param importZeile
	 * @param kindImportDaten
	 */
	public KindImportVO(final KlassenimportZeile importZeile, final KindImportDaten kindImportDaten) {

		super();
		this.importZeile = importZeile;
		this.kindImportDaten = kindImportDaten;
	}

	@Override
	public String toString() {

		return this.importZeile.getImportRohdaten();
	}

	public KlassenimportZeile getImportZeile() {

		return importZeile;
	}

	public KindImportDaten getKindImportDaten() {

		return kindImportDaten;
	}

	public String getWarnungDublette() {

		return kindImportDaten.getWarnmeldungDublette();
	}

	public void setWarnungDublette(final String warnungDublette) {

		this.kindImportDaten.setWarnmeldungDublette(warnungDublette);
	}

	public String getWarnungKlassenstufe() {

		return kindImportDaten.getWarnmeldungKlassenstufe();
	}

	public void setWarnungKlassenstufe(final String warnungKlassenstufe) {

		this.kindImportDaten.setWarnmeldungKlassenstufe(warnungKlassenstufe);
	}

	public String getImportRohdaten() {

		return importZeile == null ? null : importZeile.getImportRohdaten();
	}

	// public void setDublettePruefen(final boolean dublettePruefen) {
	//
	// this.kindImportDaten.setDublettePruefen(dublettePruefen);
	// }

	public boolean isDublettePruefen() {

		return this.kindImportDaten.isDublettePruefen();
	}

	// public void setKlassenstufePruefen(final boolean klassenstufePruefen) {
	//
	// this.kindImportDaten.setKlassenstufePruefen(klassenstufePruefen);
	// }

	public boolean isKlassentufePruefen() {

		return this.kindImportDaten.isKlassenstufePruefen();
	}

}
