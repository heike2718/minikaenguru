// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_kataloge.domain.apimodel.SchulkatalogAntrag;

/**
 * KatalogAntragMailtextGeneratorTest
 */
public class KatalogAntragMailtextGeneratorTest {

	@Test
	void should_generateTheCorrectText_when_AntragComplete() {

		// Arrange
		SchulkatalogAntrag antrag = SchulkatalogAntrag.createForTest("bla@web.de", "Hessen",
			"Mainz-Kastel", "55252", "Blümchenschule", "Mainzer Straße 15", "");
		String expected = getExpectedText("/expectedKatalogeintragSchuleMailComplete.txt");

		// Act
		String actual = new KatalogAntragMailtextGenerator().getSchuleKatalogantragText(antrag);

		// Assert
		assertEquals(expected, actual);

	}

	@Test
	void should_generateTheCorrectText_when_AntragIncomplete() {

		// Arrange
		SchulkatalogAntrag antrag = SchulkatalogAntrag.createForTest("bla@web.de", "Hessen",
			"Mainz-Kastel", " ", "Blümchenschule", "   ", "");
		String expected = getExpectedText("/expectedKatalogeintragSchuleMailIncomplete.txt");

		// Act
		String actual = new KatalogAntragMailtextGenerator().getSchuleKatalogantragText(antrag);

		// Assert
		assertEquals(expected, actual);

	}

	private String getExpectedText(final String path) {

		try (InputStream in = getClass().getResourceAsStream(path); StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, "UTF-8");
			return sw.toString();
		} catch (Exception e) {

			e.printStackTrace();
			fail("Test nicht möglich: " + path);
			return "";
		}
	}

}
