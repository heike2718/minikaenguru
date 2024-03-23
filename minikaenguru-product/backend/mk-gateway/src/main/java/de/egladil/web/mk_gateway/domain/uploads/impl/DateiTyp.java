// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

import de.egladil.web.commons_officetools.FileType;

/**
 * DateiTyp
 */
public enum DateiTyp {

	EXCEL_ALT("application/vnd.ms-excel", ".xls") {

		@Override
		public FileType getFileType() {

			return FileType.EXCEL_ALT;
		}

	},
	EXCEL_NEU("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx") {

		@Override
		public FileType getFileType() {

			return FileType.EXCEL_NEU;
		}

	},
	ODS("application/vnd.oasis.opendocument.spreadsheet", ".ods") {

		@Override
		public FileType getFileType() {

			return FileType.ODS;
		}

	},
	TEXT("text/plain", ".csv") {

		@Override
		public FileType getFileType() {

			return null;
		}

	};

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

	public abstract FileType getFileType();

}
