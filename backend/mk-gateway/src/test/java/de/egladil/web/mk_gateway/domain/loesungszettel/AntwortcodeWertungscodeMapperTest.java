// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * AntwortcodeWertungscodeMapperTest
 */
public class AntwortcodeWertungscodeMapperTest {

	@Test
	void should_mapTheAntwortbuchstaben_when_Minus() {

		// Arrange
		String antwortbuchstaben = "AABBBC";
		String loesungsbuchstabenWettbewerb = "AA-CB-BC";
		String expected = "rrfrrr";

		// Act
		String actual = new AntwortcodeWertungscodeMapper().apply(antwortbuchstaben, loesungsbuchstabenWettbewerb);

		// Assert
		assertEquals(expected, actual);

	}

}
