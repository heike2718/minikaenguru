// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.convert;

import java.io.File;
import java.util.Optional;

import de.egladil.web.commons_exceltools.FileType;
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
	 * Ermittelt das Encoding
	 *
	 * @param  pathUpload
	 * @return
	 */
	Optional<String> detectEncoding(final String pathUpload);

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

		case ODS:
			return new OpenOfficeToCSVConverter();

		case EXCEL_ALT:
		case EXCEL_NEU:
			return new ExcelToCSVConverter(FileType.valueOf(dateiTyp.toString()));

		default:
			throw new UploadFormatException("Dateityp " + dateiTyp + " kann nicht verarbeitet werden");
		}
	}

	default File checkUpload(final String pathUpload, final String uuid) {

		File file = new File(pathUpload);

		if (!file.exists() || !file.isFile() || !file.canRead()) {

			throw new MkGatewayRuntimeException(
				"Die Datei " + pathUpload + " zum upload " + uuid
					+ " existiert nicht oder ist keine Datei oder hat Zugriffsbeschraenkungen");
		}

		return file;

	}
}
