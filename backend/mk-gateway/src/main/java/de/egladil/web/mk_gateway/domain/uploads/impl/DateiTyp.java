// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

/**
 * DateiTyp
 */
public enum DateiTyp {

	EXCEL_ALT("application/vnd.ms-excel", ".xls"),
	EXCEL_NEU("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
	OSD("application/vnd.oasis.opendocument.spreadsheet", ".osd"),
	TEXT("text/plain", ".csv");

	private final String tikaName;

	private final String suffixWithPoint;

	private DateiTyp(final String tikaName, final String suffixWithPoint) {

		this.tikaName = tikaName;
		this.suffixWithPoint = suffixWithPoint;
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

	public String getSuffixWithPoint() {

		return suffixWithPoint;
	}

}
