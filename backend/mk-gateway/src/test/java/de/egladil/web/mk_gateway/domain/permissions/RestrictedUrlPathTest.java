// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.permissions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.HttpMethod;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * RestrictedUrlPathTest
 */
public class RestrictedUrlPathTest {

	@Test
	void should_ConstructorThrowException_when_PathNull() {

		try {

			List<Rolle> erlaubteRollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			new RestrictedUrlPath(null, erlaubteRollen,
				Arrays.asList(new String[] { HttpMethod.GET }));
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("path darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_PathBlank() {

		try {

			List<Rolle> erlaubteRollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			new RestrictedUrlPath("  ", erlaubteRollen,
				Arrays.asList(new String[] { HttpMethod.GET }));
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("path darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_ErlaubteRollenLeer() {

		try {

			new RestrictedUrlPath("/wettbewerb", new ArrayList<>(),
				Arrays.asList(new String[] { HttpMethod.GET }));
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("erlaubteRollen darf nicht null oder leer sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_ErlaubteRollenNull() {

		try {

			new RestrictedUrlPath("/wettbewerb", null,
				Arrays.asList(new String[] { HttpMethod.GET }));
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("erlaubteRollen darf nicht null oder leer sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_ErlaubteHttpVerbenLeer() {

		List<Rolle> erlaubteRollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });

		try {

			new RestrictedUrlPath("/wettbewerb", erlaubteRollen,
				new ArrayList<>());
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("erlaubteHttpVerben darf nicht null oder leer sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_ErlaubteHttpVerbenNull() {

		List<Rolle> erlaubteRollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });

		try {

			new RestrictedUrlPath("/wettbewerb", erlaubteRollen,
				null);
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("erlaubteHttpVerben darf nicht null oder leer sein.", e.getMessage());
		}
	}

	void should_ConstructorInitAttributes() {
		// Arrange

		String path = "/wettbewerb";
		List<Rolle> erlaubteRollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
		List<String> erlaubteHttpVerben = Arrays.asList(new String[] { HttpMethod.GET, HttpMethod.PUT });

		// Act
		RestrictedUrlPath result = new RestrictedUrlPath(path, erlaubteRollen, erlaubteHttpVerben);

		// Assert
		assertEquals(path, result.path());
		assertEquals(result, result);
		assertTrue(result.isAllowedForRolle(Rolle.LEHRER));
		assertFalse(result.isAllowedForRolle(Rolle.PRIVAT));
		assertFalse(result.isAllowedForRolle(Rolle.ADMIN));

		assertTrue(result.isRestrictedForMethod(HttpMethod.GET));
		assertFalse(result.isRestrictedForMethod(HttpMethod.DELETE));
		assertFalse(result.isRestrictedForMethod(HttpMethod.POST));
		assertTrue(result.isRestrictedForMethod(HttpMethod.PUT));
	}

	@Test
	void should_EqualsAndHashCodeUseThePath() {

		String path = "/wettbewerb";
		RestrictedUrlPath urlPath1 = new RestrictedUrlPath(path, Arrays.asList(new Rolle[] { Rolle.LEHRER }),
			Arrays.asList(new String[] { HttpMethod.GET, HttpMethod.PUT }));
		RestrictedUrlPath urlPath2 = new RestrictedUrlPath(path, Arrays.asList(new Rolle[] { Rolle.ADMIN }),
			Arrays.asList(new String[] { HttpMethod.GET, HttpMethod.PUT }));
		RestrictedUrlPath urlPath3 = new RestrictedUrlPath("/wettbewerb/bla/*/blub/*", Arrays.asList(new Rolle[] { Rolle.LEHRER }),
			Arrays.asList(new String[] { HttpMethod.GET, HttpMethod.PUT }));

		// Assert
		assertEquals(urlPath1, urlPath2);
		assertEquals(urlPath1.hashCode(), urlPath2.hashCode());
		assertFalse(urlPath1.equals(null));
		assertFalse(urlPath1.equals(new Object()));
		assertFalse(urlPath1.equals(urlPath3));
		assertFalse(urlPath1.hashCode() == urlPath3.hashCode());

		String[] urlPath1Tokens = urlPath1.tokens();
		assertEquals(2, urlPath1Tokens.length);
		assertEquals("", urlPath1Tokens[0]);
		assertEquals("wettbewerb", urlPath1Tokens[1]);

		String[] urlPath3Tokens = urlPath3.tokens();
		assertEquals(6, urlPath3Tokens.length);
		assertEquals("", urlPath3Tokens[0]);
		assertEquals("wettbewerb", urlPath3Tokens[1]);
		assertEquals("bla", urlPath3Tokens[2]);
		assertEquals("*", urlPath3Tokens[3]);
		assertEquals("blub", urlPath3Tokens[4]);
		assertEquals("*", urlPath3Tokens[5]);

		assertEquals("/wettbewerb/bla", urlPath3.nonWildcardPrefix());
	}

}
