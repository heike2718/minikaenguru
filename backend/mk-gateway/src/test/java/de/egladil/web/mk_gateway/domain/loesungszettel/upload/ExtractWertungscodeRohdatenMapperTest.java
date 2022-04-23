// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ExtractWertungscodeRohdatenMapperTest
 */
public class ExtractWertungscodeRohdatenMapperTest {

	private static final String LOESUNGSBUCHSTABEN_IKID = "AB-CB-CB".replaceAll("-", "");

	private static final String LOESUNGSBUCHSTABEN_EINS = "CDDE-EDBB-ADCE".replaceAll("-", "");

	private static final String LOESUNGSBUCHSTABEN_ZWEI = "CDCEA-DEABB-BEABA".replaceAll("-", "");

	@Nested
	class RemovalTests {

		@Test
		void should_mapperRemoveDigitsAndPoints() {

			// Arrange
			String rohdaten = "r;3;f;-0.75;r;3.0;c;0.0;f;-0.75;f;-1.0;f;-1.0;n;0.0;r;4.0;n;0.0;f;-1.25;f;-1.25;n;0.0;f;-1.25;f;-1.25;16.5";
			String expected = "r;f;r;f;f;f;f;n;r;n;f;f;n;f;f";

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(false);

			// Act
			String result = mapper.apply(rohdaten, LOESUNGSBUCHSTABEN_ZWEI);

			// Assert
			assertEquals(expected, result);

		}

		@Test
		void should_mapperRemoveNameDigitsAndPoints() {

			// Arrange
			String rohdaten = "Lukas;r;3;f;-0.75;r;3.0;e;0.0;f;-0.75;f;-1.0;f;-1.0;n;0.0;r;4.0;n;0.0;f;-1.25;f;-1.25;n;0.0;f;-1.25;f;-1.25;16.5";
			String expected = "r;f;r;r;f;f;f;n;r;n;f;f;n;f;f";

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(true);

			// Act
			String result = mapper.apply(rohdaten, LOESUNGSBUCHSTABEN_ZWEI);

			// Assert
			assertEquals(expected, result);

		}

		@Test
		void should_mapperRemoveNameDigitsAndPoints_when_NameLength1() {

			// Arrange
			String rohdaten = "L;r;3;f;-0.75;r;3.0;n;0.0;f;-0.75;f;-1.0;f;-1.0;n;0.0;r;4.0;n;0.0;f;-1.25;f;-1.25;n;0.0;f;-1.25;f;-1.25;16.5";
			String expected = "r;f;r;n;f;f;f;n;r;n;f;f;n;f;f";

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(true);

			// Act
			String result = mapper.apply(rohdaten, LOESUNGSBUCHSTABEN_ZWEI);

			// Assert
			assertEquals(expected, result);

		}

		@Test
		void should_mapperRemoveName_when_alreadyCorrect() {

			// Arrange
			String rohdaten = "Isabella;f;f;r;r;r;r;r;f;r;n;n;n;";
			String expected = "f;f;r;r;r;r;r;f;r;n;n;n";

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(true);

			// Act
			String result = mapper.apply(rohdaten, LOESUNGSBUCHSTABEN_ZWEI);

			// Assert
			assertEquals(expected, result);

		}
	}

	@Nested
	class Klasse1ReplacementTests {

		@Test
		void should_mapperReplaceInputWithN_when_notInputEqual_nrf_and_notEqual_abcde() {

			// Arrange
			String rohdaten = "Isabella;f;f;r;r;r;r;r;f;r;y;n;n;";
			String expected = "f;f;r;r;r;r;r;f;r;n;n;n";

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(true);

			// Act
			String result = mapper.apply(rohdaten, LOESUNGSBUCHSTABEN_EINS);

			// Assert
			assertEquals(expected, result);

		}

		@Test
		void should_mapperReplaceInputWithR_when_inputEqual_a_and_a_isCorrect() {

			// Arrange
			String rohdaten = "Isabella;f;f;r;r;r;r;r;f;a;y;n;n;";
			String expected = "f;f;r;r;r;r;r;f;r;n;n;n";

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(true);

			// Act
			String result = mapper.apply(rohdaten, LOESUNGSBUCHSTABEN_EINS);

			// Assert
			assertEquals(expected, result);

		}

		@Test
		void should_mapperReplaceInputWithF_when_inputEqual_a_and_a_isNotCorrect() {

			// Arrange
			String rohdaten = "f;f;r;r;r;r;r;f;r;y;n;a;";
			String expected = "f;f;r;r;r;r;r;f;r;n;n;f";

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(false);

			// Act
			String result = mapper.apply(rohdaten, LOESUNGSBUCHSTABEN_EINS);

			// Assert
			assertEquals(expected, result);

		}

	}

	@Nested
	class IkidsReplacementTests {

		@Test
		void should_mapperReplaceInputWithN_when_notInputEqual_nrf_and_notEqual_abcde() {

			// Arrange
			String rohdaten = "Isabella;d;f;r;r;r;r;";
			String expected = "n;f;r;r;r;r";

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(true);

			// Act
			String result = mapper.apply(rohdaten, LOESUNGSBUCHSTABEN_IKID);

			// Assert
			assertEquals(expected, result);

		}

		@Test
		void should_mapperReplaceInputWithR_when_inputEqual_a_and_a_isCorrect() {

			// Arrange
			String rohdaten = "Isabella;A;f;r;r;r;r;";
			String expected = "r;f;r;r;r;r";

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(true);

			// Act
			String result = mapper.apply(rohdaten, LOESUNGSBUCHSTABEN_IKID);

			// Assert
			assertEquals(expected, result);

		}

		@Test
		void should_mapperReplaceInputWithF_when_inputEqual_a_and_a_isNotCorrect() {

			// Arrange
			String rohdaten = "f;f;r;r;r;a;";
			String expected = "f;f;r;r;r;f";

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(false);

			// Act
			String result = mapper.apply(rohdaten, LOESUNGSBUCHSTABEN_IKID);

			// Assert
			assertEquals(expected, result);

		}

	}

	@Nested
	class NotPunktzeileInputTests {

		@Test
		void should_mapperMapUeberschrift() {

			// Arrange
			String rohdaten = "Name;A-1;A-2;A-3;A-4;A-5;B-1;B-2;B-3;B-4;B-5;C-1;C-2;C-3;C-5;C-5;Punkte";
			String expected = "";

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(true);

			// Act
			String result = mapper.apply(rohdaten, LOESUNGSBUCHSTABEN_ZWEI);

			// Assert
			assertEquals(expected, result);

		}

		@Test
		void should_mapperMapBlankString() {

			// Arrange
			String rohdaten = "  ";
			String expected = "";

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(false);

			// Act
			String result = mapper.apply(rohdaten, LOESUNGSBUCHSTABEN_ZWEI);

			// Assert
			assertEquals(expected, result);

		}

		@Test
		void should_mapperMapLeereZeile() {

			// Arrange
			String rohdaten = ";0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;15";
			String expected = "";

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(false);

			// Act
			String result = mapper.apply(rohdaten, LOESUNGSBUCHSTABEN_ZWEI);

			// Assert
			assertEquals(expected, result);

		}

		@Test
		void should_mapperThrowException_when_ParameterNull() {

			ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper(false);

			try {

				mapper.apply(null, LOESUNGSBUCHSTABEN_ZWEI);
				fail("keine IllegalArgumentException");
			} catch (IllegalArgumentException e) {

				assertEquals("rohdaten null", e.getMessage());
			}

		}
	}
}
