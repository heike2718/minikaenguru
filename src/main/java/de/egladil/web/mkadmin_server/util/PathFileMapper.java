// =====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkadmin_server.util;

import java.io.File;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PathFileMapper
 */
public class PathFileMapper implements Function<String, File> {

	private static final Logger LOG = LoggerFactory.getLogger(PathFileMapper.class);

	@Override
	public File apply(final String pathToFile) {

		if (pathToFile == null) {

			LOG.error("parameter pathToFile null");
			return null;
		}

		if (!new File(pathToFile).canRead()) {

			LOG.error("Keine Leseberechtigung für Datei {}", pathToFile);

			return null;
		}
		return new File(pathToFile);
	}

}
