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

	private final KindRequestData kindRequestData;

	private boolean klassenstufePruefen;

	private boolean dublettePruefen;

	public KindImportDaten(final KindRequestData kindEditorModel) {

		this.kindRequestData = kindEditorModel;
	}

	public KindRequestData getKindRequestData() {

		return kindRequestData;
	}

	public boolean isKlassenstufePruefen() {

		return klassenstufePruefen;
	}

	public void setKlassenstufePruefen(final boolean klassenstufePruefen) {

		this.klassenstufePruefen = klassenstufePruefen;
	}

	public boolean isDublettePruefen() {

		return dublettePruefen;
	}

	public void setDublettePruefen(final boolean dublettePruefen) {

		this.dublettePruefen = dublettePruefen;
	}
}
