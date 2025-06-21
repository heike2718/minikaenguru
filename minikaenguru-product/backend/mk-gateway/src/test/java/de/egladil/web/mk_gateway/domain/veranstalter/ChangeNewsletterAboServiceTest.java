// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.TestConstants;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;

/**
 * ChangeNewsletterAboServiceTest
 */
@QuarkusTest
public class ChangeNewsletterAboServiceTest {

	@InjectMock
	DomainEventHandler domainEventHandler;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@InjectMock
	VeranstalterRepository veranstalterRepository;

	@Inject
	ChangeNewsletterAboService service;

	@Test
	void should_CreateSecurityIncidentAndThrowException_when_VeranstalterNotPresent() {

		// Arrange
		String uuid = "987676H";

		when(veranstalterRepository.ofId(new Identifier(uuid))).thenReturn(Optional.empty());

		// Act + Assert
		try {

			service.changeStatusNewsletter(uuid);
			fail("BadRequestException");
		} catch (BadRequestException e) {

			verify(eventDelegate).fireSecurityEvent(any(), any());
		}

	}

	@Test
	void should_changeStatusNewsletterToggleTheFlag_when_VeranstalterPresent() {

		// Arrange
		Veranstalter veranstalter = new Lehrer(new Person(TestConstants.UUID_LEHRER_1, "GGGG"), true,
			Collections.singletonList(new Identifier(TestConstants.SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		when(veranstalterRepository.changeVeranstalter(veranstalter)).thenReturn(Boolean.TRUE);

		// Act
		service.changeStatusNewsletter(TestConstants.UUID_LEHRER_1);

		// Assert
		verify(veranstalterRepository).ofId(new Identifier(TestConstants.UUID_LEHRER_1));
		verify(veranstalterRepository).changeVeranstalter(veranstalter);
		verify(domainEventHandler, never()).handleEvent(any());
	}

}
