// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.impl;

import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;

/**
 * KindImportDaten
 */
public class KindImportDaten {

	private KindRequestData kindRequestData;

	// private boolean klassenstufePruefen;
	//
	// private boolean dublettePruefen;

	private String fehlermeldung;

	private String warnmeldungKlassenstufe;

	private String warnmeldungDublette;

	public static KindImportDaten createWithFehlermeldung(final String fehlermeldung) {

		KindImportDaten result = new KindImportDaten();
		result.fehlermeldung = fehlermeldung;
		return result;
	}

	private KindImportDaten() {

		//
	}

	public KindImportDaten(final KindRequestData kindEditorModel) {

		this.kindRequestData = kindEditorModel;
	}

	@Override
	public String toString() {

		// return "KindImportDaten [kindRequestData=" + kindRequestData + ", klassenstufePruefen=" + klassenstufePruefen
		// + ", dublettePruefen=" + dublettePruefen + "]";

		return kindRequestData.toString();
	}

	public KindRequestData getKindRequestData() {

		return kindRequestData;
	}

	public boolean isKlassenstufePruefen() {

		return warnmeldungKlassenstufe != null;
	}

	// public void setKlassenstufePruefen(final boolean klassenstufePruefen) {
	//
	// this.klassenstufePruefen = klassenstufePruefen;
	// }

	public boolean isDublettePruefen() {

		return warnmeldungDublette != null;
	}

	// public void setDublettePruefen(final boolean dublettePruefen) {
	//
	// this.dublettePruefen = dublettePruefen;
	// }

	public boolean isNichtImportiert() {

		return fehlermeldung != null;
	}

	public String getFehlermeldung() {

		return fehlermeldung;
	}

	public void setFehlermeldung(final String fehlermeldung) {

		this.fehlermeldung = fehlermeldung;
	}

	public String getWarnmeldungKlassenstufe() {

		return warnmeldungKlassenstufe;
	}

	public void setWarnmeldungKlassenstufe(final String warnmeldungKlassenstufe) {

		this.warnmeldungKlassenstufe = warnmeldungKlassenstufe;
	}

	public String getWarnmeldungDublette() {

		return warnmeldungDublette;
	}

	public void setWarnmeldungDublette(final String warnmeldungDublette) {

		this.warnmeldungDublette = warnmeldungDublette;
	}
}
