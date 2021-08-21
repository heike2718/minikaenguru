// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * AuswertungimportZeileSensorTest
 */
public class AuswertungimportZeileSensorTest {

	private AuswertungimportZeileSensor sensor = new AuswertungimportZeileSensor();

	@Nested
	class UeberschriftTests {

		@Test
		void should_isUeberschriftReturnTrue_when_rohdatenEndsWithPunkte() {

			// Act + Assert
			assertTrue(sensor.isUeberschrift(UUID.randomUUID().toString() + ",Punkte"));

		}

		@Test
		void should_isUeberschriftReturnFalse_when_rohdatenNull() {

			// Act + Assert
			assertFalse(sensor.isUeberschrift(null));

		}

		@Test
		void should_isUeberschriftReturnFalse_when_rohdatenBlank() {

			// Act + Assert
			assertFalse(sensor.isUeberschrift(" "));

		}

		@Test
		void should_isUeberschriftReturnFalse_when_rohdatenDoesNotEndWithPunkte() {

			// Act + Assert
			assertFalse(sensor.isUeberschrift(
				"Karl;r;3;r;3.0;r;3.0;r;3.0;r;3.0;r;4.0;r;4.0;f;-1.0;r;4.0;r;4.0;r;5.0;r;5.0;r;5.0;r;5.0;f;-1.25;63.75"));
		}
	}

	@Nested
	class LeereZeileTests {

		@Test
		void should_isLeereZeileReturnTrue_when_rohdatenNull() {

			// Act + Assert
			assertTrue(sensor.isLeereZeile(null));
		}

		@Test
		void should_isLeereZeileReturnTrue_when_rohdatenBlank() {

			// Act + Assert
			assertTrue(sensor.isLeereZeile(" "));

		}

		@Test
		void should_isLeereZeileReturnTrue_when_rohdatenContainsLeereZeileIndikator() {

			// Act + Assert
			assertTrue(sensor.isLeereZeile(";0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;15"));

		}

		@Test
		void should_isLeereZeileReturnFalse_when_LeereZeileIndikatorAndVolleZellen() {

			// Act + Assert
			assertFalse(sensor.isLeereZeile(
				"Henrik;r;3;r;3.0;r;3.0;f;-0.75;f;-0.75;r;4.0;r;4.0;r;4.0;n;0.0;f;-1.0;n;0.0;n;0.0;n;0.0;n;0.0;n;0.0;33.5"));
		}

	}

	@Nested
	class DetectKlassenstufeTests {

		@Test
		void should_detectKlassenstufeWork_when_ueberschriftFromExcelKlasse2() {

			// Arrange
			String ueberschrift = "Name;A-1;A-2;A-3;A-4;A-5;B-1;B-2;B-3;B-4;B-5;C-1;C-2;C-3;C-4;C-5;Punkte";

			// Act
			Klassenstufe klassenstufe = sensor.detectKlassenstufe(ueberschrift);

			// Assert
			assertEquals(Klassenstufe.ZWEI, klassenstufe);

		}

		@Test
		void should_detectKlassenstufeWork_when_ueberschriftFromCsvOrOdsKlasse1() {

			// Arrange
			String ueberschrift = "A-1;A-2;A-3;A-4;B-1;B-2;B-3;B-4;C-1;C-2;C-3;C-4;Punkte";

			// Act
			Klassenstufe klassenstufe = sensor.detectKlassenstufe(ueberschrift);

			// Assert
			assertEquals(Klassenstufe.EINS, klassenstufe);
		}

		@Test
		void should_detectKlassenstufeWork_when_ueberschriftFromExcelIKID() {

			// Arrange
			String ueberschrift = "Name;A-1;A-2;B-1;B-2;C-1;C-2;Punkte";

			// Act
			Klassenstufe klassenstufe = sensor.detectKlassenstufe(ueberschrift);

			// Assert
			assertEquals(Klassenstufe.IKID, klassenstufe);

		}

		@Test
		void should_detectKlassenstufeThrowException_when_ueberschriftFromExcelUnerwarteteAnzahl() {

			// Arrange
			String ueberschrift = "Name;A-1;A-2;A-3;B-1;B-2;B-3;C-1;C-2;C-3;Punkte";

			// Act
			try {

				sensor.detectKlassenstufe(ueberschrift);
				fail("keine UploadFormatException");
			} catch (UploadFormatException e) {

				assertEquals(
					"unerwartete Anzahl A- in 'Name;A-1;A-2;A-3;B-1;B-2;B-3;C-1;C-2;C-3;Punkte'. Klassenstufe lässt sich nicht ermitteln",
					e.getMessage());
			}
		}

		@Test
		void should_detectKlassenstufeThrowException_when_ParameterNull() {

			// Arrange
			String ueberschrift = null;

			// Act
			try {

				sensor.detectKlassenstufe(ueberschrift);
				fail("keine IllegalArgumentException");
			} catch (IllegalArgumentException e) {

				assertEquals(
					"ueberschrift null",
					e.getMessage());
			}
		}

		@Test
		void should_detectKlassenstufeThrowException_when_keineUeberschrift() {

			// Arrange
			String ueberschrift = "Karl;r;3;r;3.0;r;3.0;r;3.0;r;3.0;r;4.0;r;4.0;f;-1.0;r;4.0;r;4.0;r;5.0;r;5.0;r;5.0;r;5.0;f;-1.25;63.75";

			// Act
			try {

				sensor.detectKlassenstufe(ueberschrift);
				fail("keine MkGatewayRuntimeException");
			} catch (MkGatewayRuntimeException e) {

				assertEquals(
					"'Karl;r;3;r;3.0;r;3.0;r;3.0;r;3.0;r;4.0;r;4.0;f;-1.0;r;4.0;r;4.0;r;5.0;r;5.0;r;5.0;r;5.0;f;-1.25;63.75' ist keine Ueberschrift. Klassenstufe laesst sich nicht ermitteln",
					e.getMessage());
			}
		}
	}

	class PositionTests {

		@Test
		void should_hasNamenSpalteThrowException_when_lineKeineUeberschrift() {

			// Arrange
			AuswertungimportZeile zeile = new AuswertungimportZeile().withIndex(3)
				.withRohdaten("Tarja;f;f;r;r;f;r;r;r;r;r;r;r;");

			// Act
			try {

				sensor.hasNamenSpalte(zeile);
				fail("keine MkGatewayRuntimeException");
			} catch (MkGatewayRuntimeException e) {

				assertEquals(zeile.toString() + " ist keine Ueberschrift", e.getMessage());
			}

		}

		@Test
		void should_hasNamenSpalteThrowException_when_parameterNull() {

			// Act
			try {

				sensor.hasNamenSpalte(null);
				fail("keine IllegalArgumentException");
			} catch (IllegalArgumentException e) {

				assertEquals("ueberschrift null", e.getMessage());
			}

		}

		@Test
		void should_hasNamenSpalteFalse_when_A1IsFirstToken() {

			// Arrange
			// String ueberschrift = "Name;A-1;A-2;A-3;A-4;A-5;B-1;B-2;B-3;B-4;B-5;C-1;C-2;C-3;C-4;C-5;Punkte";
			String ueberschrift = "A-1;A-2;A-3;A-4;A-5;B-1;B-2;B-3;B-4;B-5;C-1;C-2;C-3;C-4;C-5;Punkte";
			AuswertungimportZeile zeile = new AuswertungimportZeile().withIndex(3)
				.withRohdaten(ueberschrift);

			// Act
			boolean result = sensor.hasNamenSpalte(zeile);

			// Assert
			assertEquals(false, result);

		}

		@Test
		void should_hasNamenSpaltereturnTrue_when_Name() {

			// Arrange
			String ueberschrift = "Name;A-1;A-2;A-3;A-4;A-5;B-1;B-2;B-3;B-4;B-5;C-1;C-2;C-3;C-4;C-5;Punkte";
			AuswertungimportZeile zeile = new AuswertungimportZeile().withIndex(3)
				.withRohdaten(ueberschrift);

			// Act
			boolean result = sensor.hasNamenSpalte(zeile);

			// Assert
			assertEquals(true, result);

		}
	}
}
