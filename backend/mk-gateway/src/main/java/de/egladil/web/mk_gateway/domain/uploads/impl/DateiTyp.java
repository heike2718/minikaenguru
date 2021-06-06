// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

/**
 * DateiTyp
 */
public enum DateiTyp {

	EXCEL_ALT("application/vnd.ms-excel"),
	EXCEL_NEU("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
	OOS("application/vnd.oasis.opendocument.spreadsheet"),
	TEXT("text/plain");

	private final String tikaName;

	private DateiTyp(final String tikaName) {

		this.tikaName = tikaName;
	}

	public String getTikaName() {

		return tikaName;
	}

	public static DateiTyp valueOfTikaName(final String tikaName) {

		for (DateiTyp dateiTyp : DateiTyp.values()) {

			if (dateiTyp.tikaName.equalsIgnoreCase(tikaName)) {

				return dateiTyp;
			}
		}

		return null;

	}

}
