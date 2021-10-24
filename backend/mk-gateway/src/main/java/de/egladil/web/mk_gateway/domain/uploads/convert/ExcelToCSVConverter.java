// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.convert;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_exceltools.FileType;
import de.egladil.web.commons_exceltools.MSSpreadSheetContentReader;
import de.egladil.web.commons_exceltools.error.ExceltoolsRuntimeException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;

/**
 * ExcelToCSVConverter
 */
public class ExcelToCSVConverter implements UploadToCSVConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelToCSVConverter.class);

	private final FileType excelFileType;

	public ExcelToCSVConverter(final FileType excelFileType) {

		this.excelFileType = excelFileType;
	}

	@Override
	public File convertToCSVAndPersistInFilesystem(final String pathUpload, final String uuid) {

		try {

			File upload = checkUpload(pathUpload, uuid);
			MSSpreadSheetContentReader contentReader = MSSpreadSheetContentReader.getDefault();

			String pathWorkDir = upload.getParent();
			String pathOutputFile = pathWorkDir + File.separator + uuid + DateiTyp.TEXT.getSuffixWithPoint();

			List<String> lines = contentReader.readContentAsLines(upload, excelFileType);

			File result = new File(pathOutputFile);
			MkGatewayFileUtils.writeLines(lines, result.getAbsolutePath());
			return result;
		} catch (ExceltoolsRuntimeException e) {

			LOGGER.error("Fehler beim Konvertieren der Datei {}: {}", pathUpload, e.getMessage(), e);
			throw new MkGatewayRuntimeException(
				"Die Datei " + pathUpload + " zum upload " + uuid + " konnte nicht konvertiert werden.", e);
		}
	}

	@Override
	public Optional<String> detectEncoding(final String pathUpload) {

		return MSSpreadSheetContentReader.getDefault().detectEncoding(pathUpload, excelFileType);
	}

}
