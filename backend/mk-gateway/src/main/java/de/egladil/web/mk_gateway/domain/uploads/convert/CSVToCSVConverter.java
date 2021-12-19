// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.convert;

import java.io.File;
import java.util.Optional;

/**
 * CSVToCSVConverter
 */
public class CSVToCSVConverter implements UploadToCSVConverter {

	@Override
	public File convertToCSVAndPersistInFilesystem(final String pathUpload, final String uuid) {

		File file = checkUpload(pathUpload, uuid);
		return file;
	}

	@Override
	public Optional<String> detectEncoding(final String pathUpload) {

		return Optional.empty();
	}

}
