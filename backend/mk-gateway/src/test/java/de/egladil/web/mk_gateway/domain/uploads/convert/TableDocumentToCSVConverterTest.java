// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.convert;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_officetools.FileType;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;

/**
 * TableDocumentToCSVConverterTest
 */
public class TableDocumentToCSVConverterTest extends AbstractConvertFilesTest {

	@Nested
	class ExcelAltTests {

		private static final String NAME_TARGET = "ff573035-70ff-40e8-bcad-09d781788324";

		private final TableDocumentToCSVConverter converter = new TableDocumentToCSVConverter(FileType.EXCEL_ALT);

		@Nested
		class ExcelAltKlassenlisteTests {

			@Test
			void should_convertToCSVAndPersistInFilesystemWork_when_excelFile() {

				// Arrange
				String path = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/" + NAME_TARGET
					+ DateiTyp.TEXT.getSuffixWithPoint();
				clearResult(path);

				String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.xls";

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
						"Die Datei /home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.csv zum upload ff573035-70ff-40e8-bcad-09d781788324 konnte nicht verarbeitet werden: Fehler beim Lesen einer MSOffice-Datei: Invalid header signature; read 0x3B656D616E726F56, expected 0xE11AB1A1E011CFD0 - Your file appears not to be a valid OLE2 document",
						e.getMessage());
				}

			}

			@Test
			void should_convertToCSVAndPersistInFilesystemThrowMkGatewayRuntimeException_when_odsFile() {

				// Arrange
				String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.ods";

				try {

					converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
					fail("keine MkGatewayRuntimeException");
				} catch (MkGatewayRuntimeException e) {

					assertEquals(
						"Die Datei /home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.ods zum upload ff573035-70ff-40e8-bcad-09d781788324 konnte nicht verarbeitet werden: Fehler beim Lesen einer MSOffice-Datei: The supplied data appears to be in the Office 2007+ XML. You are calling the part of POI that deals with OLE2 Office Documents. You need to call a different part of POI to process this data (eg XSSF instead of HSSF)",
						e.getMessage());
				}
			}

		}

		@Nested
		class ExcelAltAuswertungenTests {

			@Test
			void should_convertToCSVAndPersistInFilesystemWork_when_excelFile() {

				// Arrange
				String path = "/home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload/" + NAME_TARGET
					+ DateiTyp.TEXT.getSuffixWithPoint();
				clearResult(path);

				String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload/auswertung.xls";

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
				String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload/auswertung.csv";

				try {

					converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
					fail("keine MkGatewayRuntimeException");
				} catch (MkGatewayRuntimeException e) {

					assertEquals(
						"Die Datei /home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload/auswertung.csv zum upload ff573035-70ff-40e8-bcad-09d781788324 konnte nicht verarbeitet werden: Fehler beim Lesen einer MSOffice-Datei: Invalid header signature; read 0x3A64656D616E6E55, expected 0xE11AB1A1E011CFD0 - Your file appears not to be a valid OLE2 document",
						e.getMessage());
				}

			}

			@Test
			void should_convertToCSVAndPersistInFilesystemThrowMkGatewayRuntimeException_when_odsFile() {

				// Arrange
				String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload/auswertung.ods";

				try {

					converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
					fail("keine MkGatewayRuntimeException");
				} catch (MkGatewayRuntimeException e) {

					assertEquals(
						"Die Datei /home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload/auswertung.ods zum upload ff573035-70ff-40e8-bcad-09d781788324 konnte nicht verarbeitet werden: Fehler beim Lesen einer MSOffice-Datei: The supplied data appears to be in the Office 2007+ XML. You are calling the part of POI that deals with OLE2 Office Documents. You need to call a different part of POI to process this data (eg XSSF instead of HSSF)",
						e.getMessage());
				}

			}
		}
	}

	@Nested
	class ExcelNeuTests {

		private static final String NAME_TARGET = "642cd963-2c8a-49f9-be95-f31a1b7e251a";

		private final TableDocumentToCSVConverter converter = new TableDocumentToCSVConverter(FileType.EXCEL_NEU);

		@Nested
		class ExcelNeuKlassenlisteTests {
			@Test
			void should_convertToCSVAndPersistInFilesystemWork_when_excelFile() {

				// Arrange
				String path = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/" + NAME_TARGET
					+ DateiTyp.TEXT.getSuffixWithPoint();
				clearResult(path);

				String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.xlsx";

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
						"Die Datei /home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.csv zum upload 642cd963-2c8a-49f9-be95-f31a1b7e251a konnte nicht verarbeitet werden: Fehler beim Lesen einer MSOffice-Datei: No valid entries or contents found, this is not a valid OOXML (Office Open XML) file",
						e.getMessage());
				}

			}

			@Test
			void should_convertToCSVAndPersistInFilesystemThrowMkGatewayRuntimeException_when_odsFile() {

				// Arrange
				String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.ods";

				try {

					converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
					fail("keine MkGatewayRuntimeException");
				} catch (MkGatewayRuntimeException e) {

					assertEquals(
						"Die Datei /home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.ods zum upload 642cd963-2c8a-49f9-be95-f31a1b7e251a konnte nicht verarbeitet werden: Fehler beim Lesen einer MSOffice-Datei: The supplied data appears to be in ODF (Open Document) Format. Formats like these (eg ODS, ODP) are not supported, try Apache ODFToolkit",
						e.getMessage());
				}

			}
		}

		@Nested
		class ExcelNeuAuswertungenTests {

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

				String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload/auswertung.xlsx";

				// Act
				File result = converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);

				// Assert
				assertTrue(result.isFile());
				assertTrue(result.canRead());

				// printResult(result);

			}

		}
	}

	@Nested
	class OpenOfficeTests {

		private static final String NAME_TARGET = "40f991fe-4ab1-4207-b118-26670b7fd181";

		private final TableDocumentToCSVConverter converter = new TableDocumentToCSVConverter(FileType.ODS);

		@Test
		void should_convertToCSVAndPersistInFilesystemWork_when_klassenliste() {

			// Arrange
			String path = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/" + NAME_TARGET
				+ DateiTyp.TEXT.getSuffixWithPoint();
			clearResult(path);

			String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/klassenliste.ods";

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

			String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/auswertungen/korrekt/upload/auswertung.ods";

			// Act
			File result = converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);

			// Assert
			assertTrue(result.isFile());
			assertTrue(result.canRead());

			printResult(result);

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
	}
}
