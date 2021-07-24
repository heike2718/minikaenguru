// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.convert;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_exceltools.csv.ConvertscriptGenerator;
import de.egladil.web.commons_exceltools.csv.ConvertscriptRunner;
import de.egladil.web.commons_exceltools.error.ExceltoolsRuntimeException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;

/**
 * ExcelToCSVConverter
 */
public class ExcelToCSVConverter implements UploadToCSVConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelToCSVConverter.class);

	@Override
	public File convertToCSVAndPersistInFilesystem(final String pathUpload, final String uuid) {

		try {

			File upload = checkUpload(pathUpload, uuid);

			String pathWorkDir = upload.getParent();
			String nameExcelFile = upload.getName();
			String pathOutputFile = pathWorkDir + File.separator + uuid + DateiTyp.TEXT.getSuffixWithPoint();

			File pyFile = new ConvertscriptGenerator().generatePyFile(pathWorkDir, nameExcelFile, pathOutputFile);

			new ConvertscriptRunner().executePyScript(pyFile, 3000);

			File result = new File(pathOutputFile);

			return result;
		} catch (ExceltoolsRuntimeException e) {

			LOGGER.error("Fehler beim Konvertieren der Datei {}: {}", pathUpload, e.getMessage(), e);
			throw new MkGatewayRuntimeException(
				"Die Datei " + pathUpload + " zum upload " + uuid + " konnte nicht konvertiert werden.", e);
		}
	}

}
