// =====================================================
// Project: mk-kataloge-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_kataloge_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_kataloge.application.impl.KatalogFacadeImpl;
import de.egladil.web.mk_kataloge.domain.apimodel.SchuleAPIModel;

/**
 * KatalogFacadeImplIT
 */
public class KatalogFacadeImplIT extends AbstractIntegrationTest {

	private KatalogFacadeImpl facade;

	@BeforeEach
	void setUp() {

		init();

		facade = KatalogFacadeImpl.createForIntegrationTest(entityManager);
	}

	@Test
	void should_findSchulen_work() throws Exception {

		// Arrange
		List<String> schulkuerzel = Arrays.asList(new String[] { "093O1J71", "3OWU9HL5", "67N6ARM5", "8EUG2PWG", "9OICW0LP",
			"9PDDS51Q", "EQ1KQ5DJ", "KW7N4BXP", "LRH9E5KP", "M94P3IH9", "MGVKA847", "PHP0BZS7", "QIZUP119", "RODAAL9Z", "ROSUB7GZ",
			"SSBXQUBN", "SWWAM1HC", "UQWNIRV7", "WWRK09NT", "ZW9Z0FIT" });

		// Act
		List<SchuleAPIModel> schulen = facade.findSchulen(StringUtils.join(schulkuerzel, ","));

		// Assert
		assertEquals(20, schulen.size());

		for (SchuleAPIModel schule : schulen) {

			assertNotNull(schule.getKuerzelLand(), "Fehler bei " + schule.getKuerzel());
		}

		// String json = new ObjectMapper().writeValueAsString(schulen);
		//
		// System.out.println(json);

	}

}
