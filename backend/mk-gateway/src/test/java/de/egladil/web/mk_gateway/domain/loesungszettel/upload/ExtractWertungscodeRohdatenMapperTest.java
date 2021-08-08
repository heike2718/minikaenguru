// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

/**
 * ExtractWertungscodeRohdatenMapperTest
 */
public class ExtractWertungscodeRohdatenMapperTest {

	private ExtractWertungscodeRohdatenMapper mapper = new ExtractWertungscodeRohdatenMapper();

	@Test
	void should_mapperRemoveDigitsAndPoints() {

		// Arrange
		String rohdaten = "r,3,f,-0.75,r,3.0,n,0.0,f,-0.75,f,-1.0,f,-1.0,n,0.0,r,4.0,n,0.0,f,-1.25,f,-1.25,n,0.0,f,-1.25,f,-1.25,16.5";
		String expected = "r,,f,,r,,n,,f,,f,,f,,n,,r,,n,,f,,f,,n,,f,,f";

		// Act
		String result = mapper.apply(rohdaten);

		// Assert
		assertEquals(expected, result);

	}

	@Test
	void should_mapperRemoveNameDigitsAndPoints() {

		// Arrange
		String rohdaten = "Lukas,r,3,f,-0.75,r,3.0,n,0.0,f,-0.75,f,-1.0,f,-1.0,n,0.0,r,4.0,n,0.0,f,-1.25,f,-1.25,n,0.0,f,-1.25,f,-1.25,16.5";
		String expected = "r,,f,,r,,n,,f,,f,,f,,n,,r,,n,,f,,f,,n,,f,,f";

		// Act
		String result = mapper.apply(rohdaten);

		// Assert
		assertEquals(expected, result);

	}

	@Test
	void should_mapperRemoveName_when_alreadyCorrect() {

		// Arrange
		String rohdaten = "Isabella,f,,f,,r,,r,,r,,r,,r,,f,,r,,n,,n,,n,,";
		String expected = "f,,f,,r,,r,,r,,r,,r,,f,,r,,n,,n,,n";

		// Act
		String result = mapper.apply(rohdaten);

		// Assert
		assertEquals(expected, result);

	}

	@Test
	void should_mapperMapUeberschrift() {

		// Arrange
		String rohdaten = "Name,A-1,,A-2,,A-3,,A-4,,A-5,,B-1,,B-2,,B-3,,B-4,,B-5,,C-1,,C-2,,C-3,,C-5,,C-5,,Punkte";
		String expected = "";

		// Act
		String result = mapper.apply(rohdaten);

		// Assert
		assertEquals(expected, result);

	}

	@Test
	void should_mapperMapBlankString() {

		// Arrange
		String rohdaten = "  ";
		String expected = "";

		// Act
		String result = mapper.apply(rohdaten);

		// Assert
		assertEquals(expected, result);

	}

	@Test
	void should_mapperMapLeereZeile() {

		// Arrange
		String rohdaten = ",,0,,0.0,,0.0,,0.0,,0.0,,0.0,,0.0,,0.0,,0.0,,0.0,,0.0,,0.0,,0.0,,0.0,,0.0,15";
		String expected = "";

		// Act
		String result = mapper.apply(rohdaten);

		// Assert
		assertEquals(expected, result);

	}

	@Test
	void should_mapperThrowException_when_ParameterNull() {

		try {

			mapper.apply(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("rohdaten null", e.getMessage());
		}

	}

}
