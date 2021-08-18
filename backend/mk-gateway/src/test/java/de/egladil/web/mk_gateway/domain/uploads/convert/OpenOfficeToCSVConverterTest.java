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
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;

/**
 * OpenOfficeToCSVConverterTest
 */
public class OpenOfficeToCSVConverterTest extends AbstractConvertFilesTest {

	private static final String NAME_TARGET = "40f991fe-4ab1-4207-b118-26670b7fd181";

	private OpenOfficeToCSVConverter converter;

	@BeforeEach
	public void setUp() {

		converter = new OpenOfficeToCSVConverter();
	}

	@AfterEach
	public void tearDown() {

		clearFiles();
	}

	@Override
	protected DateiTyp getDateiTyp() {

		return DateiTyp.ODS;
	}

	@Test
	void should_convertToCSVAndPersistInFilesystemWork_when_klassenliste() {

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

		printResult(result);

	}

	@Test
	void should_convertToCSVAndPersistInFilesystemWork_when_auswertung() {

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

		printResult(result);

	}

	@Test
	void should_convertToCSVAndPersistInFilesystemThrowMkGatewayRuntimeException_when_textFile() {

		// Arrange
		String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.csv";

		try {

			converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals(
				"Die Datei /home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.csv zum upload 40f991fe-4ab1-4207-b118-26670b7fd181 konnte nicht verarbeitet werden: Exception Erzeugen eines ZipFiles (vermutlich kein Zip-Archiv): zip END header not found",
				e.getMessage());
		}

	}

	@Test
	void should_convertToCSVAndPersistInFilesystemThrowMkGatewayRuntimeException_when_altesExcelFile() {

		// Arrange
		String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.xls";

		try {

			converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals(
				"Die Datei /home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.xls zum upload 40f991fe-4ab1-4207-b118-26670b7fd181 konnte nicht verarbeitet werden: Exception Erzeugen eines ZipFiles (vermutlich kein Zip-Archiv): zip END header not found",
				e.getMessage());
		}

	}

	@Test
	void should_convertToCSVAndPersistInFilesystemThrowMkGatewayRuntimeException_when_neuesExcelFile() {

		// Arrange
		String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.xlsx";

		try {

			converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals(
				"Die Datei /home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.xlsx zum upload 40f991fe-4ab1-4207-b118-26670b7fd181 konnte nicht verarbeitet werden: Das Zip-Archiv enthält keinen ZipEntry mit Namen 'content.xml' in oberster Ebene.",
				e.getMessage());
		}

	}

	@Test
	void should_convertToCSVAndPersistInFilesystemThrowMkGatewayRuntimeException_when_fileDoesNotExist() {

		// Arrange
		String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/bla.csv";

		try {

			converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals(
				"Die Datei /home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/bla.csv zum upload 40f991fe-4ab1-4207-b118-26670b7fd181 existiert nicht oder ist keine Datei oder hat Zugriffsbeschraenkungen",
				e.getMessage());
		}

	}

}
