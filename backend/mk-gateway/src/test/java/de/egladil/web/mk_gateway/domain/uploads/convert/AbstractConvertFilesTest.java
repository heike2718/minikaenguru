// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.convert;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;

/**
 * AbstractConvertFilesTest
 */
public abstract class AbstractConvertFilesTest {

	protected static final String PATH_DIR_SOURCEFILES_AUSWERTUNG = "/home/heike/mkv/upload/original-files/auswertungen/";

	protected static final String PATH_DIR_SOURCEFILES_KLASSENLISTE = "/home/heike/mkv/upload/original-files/klassenlisten/";

	/**
	 *
	 */
	protected void clearFiles() {

		{

			File[] allFiles = new File(PATH_DIR_SOURCEFILES_AUSWERTUNG).listFiles();

			List<File> intermediateFiles = Arrays.stream(allFiles).filter(
				f -> !f.getName().startsWith("auswertung"))
				.collect(Collectors.toList());

			intermediateFiles.forEach(f -> FileUtils.deleteQuietly(f));
		}

		{

			File[] allFiles = new File(PATH_DIR_SOURCEFILES_KLASSENLISTE).listFiles();

			List<File> intermediateFiles = Arrays.stream(allFiles).filter(
				f -> !f.getName().startsWith("klassenliste"))
				.collect(Collectors.toList());

			intermediateFiles.forEach(f -> FileUtils.deleteQuietly(f));
		}
	}

	protected String getNameSourcefileAuswertung() {

		return "auswertung" + getDateiTyp().getSuffixWithPoint();
	}

	protected String getNameSourcefileKlassenliste() {

		return "klassenliste" + getDateiTyp().getSuffixWithPoint();
	}

	protected abstract DateiTyp getDateiTyp();

	protected void clearResult(final String path) {

		FileUtils.deleteQuietly(new File(path));
	}

	protected void printResult(final File file) {

		List<String> lines = MkGatewayFileUtils.readLines(file.getAbsolutePath());

		lines.forEach(l -> System.out.println(l));

	}

}
