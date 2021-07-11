// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.convert;

import java.io.File;

import de.egladil.web.commons_exceltools.csv.ConvertscriptGenerator;
import de.egladil.web.commons_exceltools.csv.ConvertscriptRunner;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;

/**
 * ExcelToCSVConverter
 */
public class ExcelToCSVConverter implements UploadToCSVConverter {

	@Override
	public File convertToCSVAndPersistInFilesystem(final String pathUpload, final String uuid) {

		File upload = checkUpload(pathUpload);

		String pathWorkDir = upload.getParent();
		String nameExcelFile = upload.getName();
		String pathOutputFile = pathWorkDir + File.separator + uuid + DateiTyp.TEXT.getSuffixWithPoint();

		File pyFile = new ConvertscriptGenerator().generatePyFile(pathWorkDir, nameExcelFile, pathOutputFile);

		new ConvertscriptRunner().executePyScript(pyFile, 3000);

		return new File(pathOutputFile);
	}

}
