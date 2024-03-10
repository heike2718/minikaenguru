// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth.signup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.mk_gateway.domain.auth.session.tokens.TokenExchangeService;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;

/**
 * AuthResultToResourceOwnerMapperTest
 */
@QuarkusTest
public class AuthResultToResourceOwnerMapperTest {

	@InjectMock
	JWTService jwtService;

	@InjectMock
	TokenExchangeService tokenExchangeService;

	@InjectMock
	DomainEventHandler domainEventHandler;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@Inject
	AuthResultToResourceOwnerMapper mapper;

	@Test
	void should_ApplyThrowException_when_ParameterNull() {

		// Arrange
		// Act
		try {

			mapper.apply(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("authResult darf nicht null sein", e.getMessage());
		}
	}

}
