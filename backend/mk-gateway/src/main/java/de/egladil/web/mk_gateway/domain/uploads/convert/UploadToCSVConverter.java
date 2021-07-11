// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.convert;

import java.io.File;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;

/**
 * UploadToCSVConverter
 */
public interface UploadToCSVConverter {

	/**
	 * Konvertiert die Datei mit absolute path pathUpload nach CSV und speichert sie mit der gleichen UUID.
	 *
	 * @param  pathUpload
	 * @param  uuid
	 *                    String UUID der Upload-Metadaten.
	 * @return            File handle auf die CSV-Datei.
	 */
	File convertToCSVAndPersistInFilesystem(String pathUpload, String uuid);

	/**
	 * Gibt den passeden Converter zurück.
	 *
	 * @param  dateiTyp
	 * @return          UploadToCSVConverter
	 */
	static UploadToCSVConverter createForDateityp(final DateiTyp dateiTyp) {

		switch (dateiTyp) {

		case TEXT:
			return new CSVToCSVConverter();

		case OSD:
			return new OpenOfficeToCSVConverter();

		case EXCEL_ALT:
		case EXCEL_NEU:
			return new ExcelToCSVConverter();

		default:
			throw new UploadFormatException("Dateityp " + dateiTyp + " kann nicht verarbeitet werden");
		}
	}

	default File checkUpload(final String pathUpload) {

		File file = new File(pathUpload);

		if (!file.exists() || !file.isFile() || !file.canRead()) {

			throw new MkGatewayRuntimeException(
				pathUpload + " existiert nicht oder ist keine Datei oder hat Zugriffsbeschraenkungen");
		}

		return file;

	}
}
