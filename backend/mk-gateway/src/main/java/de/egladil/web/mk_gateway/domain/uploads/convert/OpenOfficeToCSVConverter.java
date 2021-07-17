// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.convert;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_openofficetools.OpenOfficeContentReader;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;

/**
 * OpenOfficeToCSVConverter
 */
public class OpenOfficeToCSVConverter implements UploadToCSVConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(OpenOfficeToCSVConverter.class);

	@Override
	public File convertToCSVAndPersistInFilesystem(final String pathUpload, final String uuid) {

		File uploadFile = checkUpload(pathUpload, uuid);

		try {

			List<String> lines = new OpenOfficeContentReader().readContentAsLines(uploadFile);

			String pathUploadDir = uploadFile.getParent();

			File result = this.writeUploadFile(pathUploadDir, lines, uuid);

			return result;

		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
			throw new MkGatewayRuntimeException(
				"Die Datei " + pathUpload + " zum upload " + uuid + " konnte nicht verarbeitet werden: " + e.getMessage(), e);

		}
	}

	private File writeUploadFile(final String pathUploadDir, final List<String> lines, final String uuid) {

		String path = pathUploadDir + File.separator + uuid + DateiTyp.TEXT.getSuffixWithPoint();

		return MkGatewayFileUtils.writeLines(lines, path);
	}

}
