// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.convert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_openofficetools.OpenOfficeContentReader;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;

/**
 * OpenOfficeToCSVConverter
 */
public class OpenOfficeToCSVConverter implements UploadToCSVConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(OpenOfficeToCSVConverter.class);

	@Override
	public File convertToCSVAndPersistInFilesystem(final String pathUpload, final String uuid) {

		File uploadFile = checkUpload(pathUpload);

		try {

			List<String> lines = new OpenOfficeContentReader().readContentAsLines(uploadFile);
			File result = this.writeUploadFile(pathUpload, lines, uuid);

			return result;

		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte upload " + uuid + " nicht verarbeiten");

		}
	}

	private File writeUploadFile(final String pathUploadDir, final List<String> lines, final String uuid) {

		String path = pathUploadDir + File.separator + uuid + DateiTyp.TEXT.getSuffixWithPoint();

		File file = new File(path);

		String content = StringUtils.join(lines, "\n") + "\n";

		try (FileOutputStream fos = new FileOutputStream(file); InputStream in = new ByteArrayInputStream(content.getBytes())) {

			IOUtils.copy(in, fos);
			fos.flush();

			return file;
		} catch (IOException e) {

			LOGGER.error("Fehler beim Speichern im Filesystem: " + e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte upload nicht ins Filesystem speichern: " + e.getMessage(), e);
		}
	}

}
