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

/**
 * AbstractConvertFilesTest
 */
public abstract class AbstractConvertFilesTest {

	private static final String PATH_DIR_SOURCEFILES_AUSWERTUNG = "/home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload";

	private static final String PATH_DIR_SOURCEFILES_KLASSENLISTE = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload";

	/**
	*
	*/
	protected void clearFiles() {

		{

			File[] allFiles = new File(PATH_DIR_SOURCEFILES_AUSWERTUNG).listFiles();

			List<File> intermediateFiles = Arrays.stream(allFiles).filter(
				f -> f.getName().startsWith("642cd963-2c8a-49f9-be95-f31a1b7e251a")
					|| f.getName().startsWith("40f991fe-4ab1-4207-b118-26670b7fd181")
					|| f.getName().startsWith("ff573035-70ff-40e8-bcad-09d781788324"))
				.collect(Collectors.toList());

			intermediateFiles.forEach(f -> FileUtils.deleteQuietly(f));
		}

		{

			File[] allFiles = new File(PATH_DIR_SOURCEFILES_KLASSENLISTE).listFiles();

			List<File> intermediateFiles = Arrays.stream(allFiles).filter(
				f -> f.getName().startsWith("642cd963-2c8a-49f9-be95-f31a1b7e251a")
					|| f.getName().startsWith("40f991fe-4ab1-4207-b118-26670b7fd181")
					|| f.getName().startsWith("ff573035-70ff-40e8-bcad-09d781788324"))
				.collect(Collectors.toList());

			intermediateFiles.forEach(f -> FileUtils.deleteQuietly(f));
		}
	}

	protected void clearResult(final String path) {

		FileUtils.deleteQuietly(new File(path));
	}

	protected void printResult(final File file) {

		List<String> lines = MkGatewayFileUtils.readLines(file.getAbsolutePath(), MkGatewayFileUtils.DEFAULT_ENCODING);

		lines.forEach(l -> System.out.println(l));

	}

}
