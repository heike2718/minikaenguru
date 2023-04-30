// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_mailer.exception.EmailException;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
import de.egladil.web.mk_kataloge.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_kataloge.domain.katalogantrag.KatalogMailService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

/**
 * ChangeSchulenMailDelegateTest
 */
@QuarkusTest
public class ChangeSchulenMailDelegateTest {

	@InjectMock
	KatalogMailService mailService;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@Inject
	ChangeSchulenMailDelegate delegate;

	@Test
	void should_SendMail_When_Ok() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest().withEmailAuftraggeber("hh@gmx.de");

		doNothing().when(mailService).sendMail(any());

		// Act
		boolean mailSent = delegate.sendSchuleCreatedMailQuietly(schulePayload);

		// Assert
		assertTrue(mailSent);

	}

	@Test
	void should_handleExceptionQuietly() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest().withEmailAuftraggeber("hh@gmx.de");

		doThrow(new EmailException("Das ist eine gemockte Mailexception")).when(mailService).sendMail(any());

		// Act
		boolean mailSent = delegate.sendSchuleCreatedMailQuietly(schulePayload);

		// Assert
		assertFalse(mailSent);
		verify(eventDelegate).fireMailNotSentEvent(any(), any());
	}
}
