// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
import de.egladil.web.mk_kataloge.domain.event.MailNotSent;
import de.egladil.web.mk_kataloge.domain.katalogantrag.KatalogMailService;

/**
 * ChangeSchulenMailDelegateTest
 */
public class ChangeSchulenMailDelegateTest {

	@Test
	void should_SendMail_When_Ok() {

		// Arrange
		KatalogMailService mailService = KatalogMailService.createForTest();
		ChangeSchulenMailDelegate delegate = ChangeSchulenMailDelegate.createForTest(mailService);
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest().withEmailAuftraggeber("hh@gmx.de");

		// Act
		boolean mailSent = delegate.sendSchuleCreatedMailQuietly(schulePayload);

		// Assert
		assertTrue(mailSent);

		assertNull(delegate.getMailNotSent());

	}

	@Test
	void should_handleExceptionQueitly() {

		// Arrange
		KatalogMailService mailService = KatalogMailService.createForTestWithMailException();
		ChangeSchulenMailDelegate delegate = ChangeSchulenMailDelegate.createForTest(mailService);
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest().withEmailAuftraggeber("hh@gmx.de");

		// Act
		boolean mailSent = delegate.sendSchuleCreatedMailQuietly(schulePayload);

		// Assert
		assertFalse(mailSent);

		MailNotSent eventObject = delegate.getMailNotSent();
		assertNotNull(eventObject);
		assertNotNull(eventObject.occuredOn());
		assertEquals("Die Mail konnte nicht gesendet werden: Das ist eine gemockte Mailexception", eventObject.toString());

	}
}
