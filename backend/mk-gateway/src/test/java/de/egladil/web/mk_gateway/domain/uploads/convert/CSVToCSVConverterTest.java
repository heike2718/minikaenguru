// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * CSVToCSVConverterTest
 */
public class CSVToCSVConverterTest extends AbstractConvertFilesTest {

	private static final String NAME_TARGET = "cf6469ad-347d-4530-9416-a70f15f02a93";

	private CSVToCSVConverter converter;

	@BeforeEach
	public void setUp() {

		converter = new CSVToCSVConverter();
	}

	@Test
	void should_convertToCSVAndPersistInFilesystemThrowMkGatewayRuntimeException_when_fileDoesNotExist() {

		// Arrange
		String pathSourceFile = "/home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/" + "bla.csv";

		try {

			converter.convertToCSVAndPersistInFilesystem(pathSourceFile, NAME_TARGET);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals(
				"Die Datei /home/heike/git/testdaten/minikaenguru/klassenlisten/korrekt/upload/bla.csv zum upload cf6469ad-347d-4530-9416-a70f15f02a93 existiert nicht oder ist keine Datei oder hat Zugriffsbeschraenkungen",
				e.getMessage());
		}

	}

}
