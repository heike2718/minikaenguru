// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.impl;

import java.util.Objects;

import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
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

	@Override
	public int hashCode() {

		return Objects.hash(kindRequestData.kind());
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		KindImportDaten other = (KindImportDaten) obj;
		KindEditorModel otherKind = other.kindRequestData.kind();
		KindEditorModel thisKind = this.kindRequestData.kind();

		return Objects.equals(thisKind, otherKind);
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
