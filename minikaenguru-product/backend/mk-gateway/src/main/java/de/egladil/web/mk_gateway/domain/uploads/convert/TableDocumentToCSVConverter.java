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

import de.egladil.web.commons_officetools.EncodingDetector;
import de.egladil.web.commons_officetools.FileType;
import de.egladil.web.commons_officetools.TableDocumentContentReader;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;

/**
 * TableDocumentToCSVConverter
 */
public class TableDocumentToCSVConverter implements UploadToCSVConverter {

	private static final Logger LOGGER = LoggerFactory.getLogger(TableDocumentToCSVConverter.class);

	private final FileType fileType;

	public TableDocumentToCSVConverter(final FileType fileType) {

		super();
		this.fileType = fileType;
	}

	@Override
	public File convertToCSVAndPersistInFilesystem(final String pathUpload, final String uuid) {

		File uploadFile = checkUpload(pathUpload, uuid);

		try {

			TableDocumentContentReader contentReader = TableDocumentContentReader.getContentReader(fileType);

			List<String> lines = contentReader.readContentAsLines(uploadFile);

			String pathUploadDir = uploadFile.getParent();

			File result = this.writeUploadFile(pathUploadDir, lines, uuid);

			return result;

		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
			throw new MkGatewayRuntimeException(
				"Die Datei " + pathUpload + " zum upload " + uuid + " konnte nicht verarbeitet werden: " + e.getMessage(), e);
		}
	}

	@Override
	public Optional<String> detectEncoding(final String pathUpload) {

		EncodingDetector encodingDetector = EncodingDetector.getEncodingDetector(fileType);
		return encodingDetector.detectEncoding(pathUpload);
	}

	private File writeUploadFile(final String pathUploadDir, final List<String> lines, final String uuid) {

		String path = pathUploadDir + File.separator + uuid + DateiTyp.TEXT.getSuffixWithPoint();

		return MkGatewayFileUtils.writeLines(lines, path);
	}
}
