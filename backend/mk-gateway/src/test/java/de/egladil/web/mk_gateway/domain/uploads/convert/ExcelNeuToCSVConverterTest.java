// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_exceltools.FileType;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;

/**
 * ExcelNeuToCSVConverterTest
 */
public class ExcelNeuToCSVConverterTest extends AbstractConvertFilesTest {

	private static final String NAME_TARGET = "642cd963-2c8a-49f9-be95-f31a1b7e251a";

	private ExcelToCSVConverter converter;

	@BeforeEach
	public void setUp() {

		converter = new ExcelToCSVConverter(FileType.EXCEL_NEU);
	}

	@AfterEach
	public void tearDown() {

		clearFiles();
	}

	@Override
	protected DateiTyp getDateiTyp() {

		return DateiTyp.EXCEL_NEU;
	}

	@Nested
	class AuswertungTests {

		@Test
		void should_convertToCSVAndPersistInFilesystemWork_when_auswertungAusProduktion() {

			// Arrange
			String path = "/home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload" + NAME_TARGET
				+ DateiTyp.TEXT.getSuffixWithPoint();
			clearResult(path);

			String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload/2021_auswertung_minikaenguru_klasse_1.xlsx";

			// Act
			File result = converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);

			// Assert
			assertTrue(result.isFile());
			assertTrue(result.canRead());

			printResult(result);

		}

		@Test
		void should_convertToCSVAndPersistInFilesystemWork_when_auswertungExcelFile() {

			// Arrange
			String path = "/home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload/" + NAME_TARGET
				+ DateiTyp.TEXT.getSuffixWithPoint();
			clearResult(path);

			String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload/"
				+ getNameSourcefileAuswertung();

			// Act
			File result = converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);

			// Assert
			assertTrue(result.isFile());
			assertTrue(result.canRead());

			// printResult(result);

		}

	}

	@Nested
	class KlassenlisteTests {

		@Test
		void should_convertToCSVAndPersistInFilesystemThrowMkGatewayRuntimeException_when_fileDoesNotExist() {

			// Arrange
			String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/bla.csv";

			try {

				converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
				fail("keine MkGatewayRuntimeException");
			} catch (MkGatewayRuntimeException e) {

				assertEquals(
					"Die Datei /home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/bla.csv zum upload 642cd963-2c8a-49f9-be95-f31a1b7e251a existiert nicht oder ist keine Datei oder hat Zugriffsbeschraenkungen",
					e.getMessage());
			}

		}

		@Test
		void should_convertToCSVAndPersistInFilesystemWork_when_klassenlisteExcelFile() {

			// Arrange
			String path = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/" + NAME_TARGET
				+ DateiTyp.TEXT.getSuffixWithPoint();
			clearResult(path);

			String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/"
				+ getNameSourcefileKlassenliste();

			// Act
			File result = converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);

			// Assert
			assertTrue(result.isFile());
			assertTrue(result.canRead());

			// printResult(result);

		}
	}
}
