// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.convert;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;

/**
 * ExcelAltToCSVConverterTest
 */
public class ExcelAltToCSVConverterTest extends AbstractConvertFilesTest {

	private static final String NAME_TARGET = "ff573035-70ff-40e8-bcad-09d781788324";

	private ExcelToCSVConverter converter;

	@BeforeEach
	public void setUp() {

		converter = new ExcelToCSVConverter();
	}

	@AfterEach
	public void tearDown() {

		clearFiles();
	}

	@Override
	protected DateiTyp getDateiTyp() {

		return DateiTyp.EXCEL_ALT;
	}

	@Nested
	class KlassenlisteTests {
		@Test
		void should_convertToCSVAndPersistInFilesystemWork_when_excelFile() {

			// Arrange
			String path = PATH_DIR_SOURCEFILES_KLASSENLISTE + NAME_TARGET + DateiTyp.TEXT.getSuffixWithPoint();
			clearResult(path);

			String pathSourceFile = PATH_DIR_SOURCEFILES_KLASSENLISTE + getNameSourcefileKlassenliste();

			// Act
			File result = converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);

			// Assert
			assertTrue(result.isFile());
			assertTrue(result.canRead());

			// printResult(result);

		}

		@Test
		void should_convertToCSVAndPersistInFilesystemThrowMkGatewayRuntimeException_when_textFile() {

			// Arrange
			String pathSourceFile = PATH_DIR_SOURCEFILES_KLASSENLISTE + "klassenliste.csv";

			try {

				converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
				fail("keine MkGatewayRuntimeException");
			} catch (MkGatewayRuntimeException e) {

				assertEquals(
					"Die Datei /home/heike/mkv/upload/original-files/klassenlisten/klassenliste.csv zum upload ff573035-70ff-40e8-bcad-09d781788324 konnte nicht konvertiert werden.",
					e.getMessage());
			}

		}

		@Test
		void should_convertToCSVAndPersistInFilesystemThrowMkGatewayRuntimeException_when_odsFile() {

			// Arrange
			String pathSourceFile = PATH_DIR_SOURCEFILES_KLASSENLISTE + "klassenliste.ods";

			try {

				converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
				fail("keine MkGatewayRuntimeException");
			} catch (MkGatewayRuntimeException e) {

				assertEquals(
					"Die Datei /home/heike/mkv/upload/original-files/klassenlisten/klassenliste.ods zum upload ff573035-70ff-40e8-bcad-09d781788324 konnte nicht konvertiert werden.",
					e.getMessage());
			}

		}
	}

	@Nested
	class AuswertungTests {
		@Test
		void should_convertToCSVAndPersistInFilesystemWork_when_excelFile() {

			// Arrange
			String path = PATH_DIR_SOURCEFILES_AUSWERTUNG + NAME_TARGET + DateiTyp.TEXT.getSuffixWithPoint();
			clearResult(path);

			String pathSourceFile = PATH_DIR_SOURCEFILES_AUSWERTUNG + getNameSourcefileAuswertung();

			// Act
			File result = converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);

			// Assert
			assertTrue(result.isFile());
			assertTrue(result.canRead());

			// printResult(result);

		}

		@Test
		void should_convertToCSVAndPersistInFilesystemThrowMkGatewayRuntimeException_when_textFile() {

			// Arrange
			String pathSourceFile = PATH_DIR_SOURCEFILES_AUSWERTUNG + "auswertung.csv";

			try {

				converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
				fail("keine MkGatewayRuntimeException");
			} catch (MkGatewayRuntimeException e) {

				assertEquals(
					"Die Datei /home/heike/mkv/upload/original-files/auswertungen/auswertung.csv zum upload ff573035-70ff-40e8-bcad-09d781788324 konnte nicht konvertiert werden.",
					e.getMessage());
			}

		}

		@Test
		void should_convertToCSVAndPersistInFilesystemThrowMkGatewayRuntimeException_when_odsFile() {

			// Arrange
			String pathSourceFile = PATH_DIR_SOURCEFILES_AUSWERTUNG + "auswertung.ods";

			try {

				converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
				fail("keine MkGatewayRuntimeException");
			} catch (MkGatewayRuntimeException e) {

				assertEquals(
					"Die Datei /home/heike/mkv/upload/original-files/auswertungen/auswertung.ods zum upload ff573035-70ff-40e8-bcad-09d781788324 konnte nicht konvertiert werden.",
					e.getMessage());
			}

		}
	}

}
