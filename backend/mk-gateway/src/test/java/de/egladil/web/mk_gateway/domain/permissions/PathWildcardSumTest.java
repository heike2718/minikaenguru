// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.permissions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * PathWildcardSumTest
 */
public class PathWildcardSumTest {

	@Test
	void should_ApplyReturnNull_when_TokensNull() {

		// Arrange
		RestrictedUrlPath urlPath = new RestrictedUrlPath("/statistik/laender/*/*", Arrays.asList(new Rolle[] { Rolle.ADMIN }));

		// Act
		String result = new PathWildcardSum().apply(urlPath, null);

		// Assert
		assertNull(result);
	}

	@Test
	void should_ApplyReturnNull_when_DifferentArrayLength() {

		// Arrange
		RestrictedUrlPath urlPath = new RestrictedUrlPath("/statistik/laender/*/*", Arrays.asList(new Rolle[] { Rolle.ADMIN }));

		// Act
		String result = new PathWildcardSum().apply(urlPath, new String[] { "statistik", "laender", "de-he" });

		// Assert
		assertNull(result);
	}

	@Test
	void should_ApplyLetWinToken_when_UrlTokenWildcard() {

		// Arrange
		RestrictedUrlPath urlPath = new RestrictedUrlPath("/statistik/laender/*/*", Arrays.asList(new Rolle[] { Rolle.ADMIN }));

		// Act
		String result = new PathWildcardSum().apply(urlPath, new String[] { "", "statistik", "laender", "DE-HE", "2018" });

		// Assert
		assertEquals("/statistik/laender/de-he/2018", result);
	}

	@Test
	void should_ApplyLetWinUrl_when_UrlTokenNotWildcard() {

		// Arrange
		RestrictedUrlPath urlPath = new RestrictedUrlPath("/statistik/laender/*/*", Arrays.asList(new Rolle[] { Rolle.ADMIN }));

		// Act
		String result = new PathWildcardSum().apply(urlPath, new String[] { "", "statistik", "schulen", "H635FRZ6", "2018" });

		// Assert
		assertEquals("/statistik/schulen/h635frz6/2018", result);
	}

}
