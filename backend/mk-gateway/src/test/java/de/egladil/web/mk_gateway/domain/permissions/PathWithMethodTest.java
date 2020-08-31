// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.permissions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.ws.rs.HttpMethod;

import org.junit.jupiter.api.Test;

/**
 * PathWithMethodTest
 */
public class PathWithMethodTest {

	@Test
	void should_ConstructorThrowException_when_PathNull() {

		try {

			new PathWithMethod(null, HttpMethod.GET);
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("path darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_PathBlank() {

		try {

			new PathWithMethod("   ", HttpMethod.GET);
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("path darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_MethodNull() {

		try {

			new PathWithMethod("/statistik/laender/*/*", null);
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("method darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorThrowException_when_MethodBlank() {

		try {

			new PathWithMethod("/statistik/laender/*/*", "   ");
			fail("keine IllegalArgumentException");

		} catch (IllegalArgumentException e) {

			assertEquals("method darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void should_ConstructorInitAttributes() {

		// Act
		PathWithMethod pwm = new PathWithMethod("/laender/*/orte", HttpMethod.GET);

		// Assert
		assertEquals("/laender/*/orte", pwm.path());
		assertEquals("GET", pwm.method());

	}

	@Test
	void should_nonWildcardPrefix_return_prefix_before_wildcards() {

		// Arrange
		PathWithMethod pwm = new PathWithMethod("wb-kataloge/laender/*/orte", HttpMethod.GET);

		// Act
		String actual = pwm.nonWildcardPrefix();

		// Assert
		assertEquals("wb-kataloge/laender", actual);
	}
}
