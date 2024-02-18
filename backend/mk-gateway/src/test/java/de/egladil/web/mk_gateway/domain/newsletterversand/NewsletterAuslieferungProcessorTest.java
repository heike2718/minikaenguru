// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_mailer.exception.EmailException;
import de.egladil.web.commons_mailer.exception.InvalidMailAddressException;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.mail.AdminMailService;
import de.egladil.web.mk_gateway.domain.newsletters.Newsletter;
import de.egladil.web.mk_gateway.domain.newsletterversand.event.NewsletterversandFailed;
import de.egladil.web.mk_gateway.profiles.FullDatabaseTestProfile;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.mail.Address;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

/**
 * NewsletterAuslieferungProcessorTest
 */
@QuarkusTest
@TestProfile(value = FullDatabaseTestProfile.class)
public class NewsletterAuslieferungProcessorTest {

	@ConfigProperty(name = "emails.testempfaenger")
	String emailEmpfaenger;

	@InjectMock
	AuslieferungPicker auslieferungPicker;

	@InjectMock
	AuslieferungStatusUpdater auslieferungStatusUpdater;

	@InjectMock
	VersandauftragStatusUpdater versandauftragStatusUpdater;

	@InjectMock
	NewsletterVersandauftragService newsletterAuftraegeService;

	@InjectMock
	AdminMailService mailService;

	@InjectMock
	DomainEventHandler domainEventHandler;

	@Inject
	NewsletterAuslieferungProcessor processor;

	@Test
	void should_processNextAuslieferungDoNothing_when_thereIsNoNextAuslieferung() {

		// Arrange
		when(auslieferungPicker.getNextPendingAuslieferung()).thenReturn(null);

		// Act
		processor.processNextAuslieferung();

		// Assert
		verify(newsletterAuftraegeService, never()).getVersandauftragAndNewsletterWithVersandauftragID(any(Identifier.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragCompletedWithDataError(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragStarted(any(Versandauftrag.class));
		verify(auslieferungStatusUpdater, never()).markAuslieferungCompleted(any(NewsletterAuslieferung.class));
		verify(auslieferungStatusUpdater, never()).markAuslieferungStarted(any(NewsletterAuslieferung.class));
		verify(versandauftragStatusUpdater, never()).updateStatusVersandauftrag(any(Versandauftrag.class), any(Integer.class));
		verify(mailService, never()).sendMail(any(DefaultEmailDaten.class));
	}

	@Test
	void should_processNextAuslieferungPropagateMkGatewayRuntimeException_from_getVersandauftragAndNewsletterWithVersandauftragID() {

		// Arrange
		NewsletterAuslieferung auslieferung = new NewsletterAuslieferung().withIdentifier(new Identifier("1"))
			.withVersandauftragId(new Identifier("2"));

		when(auslieferungPicker.getNextPendingAuslieferung()).thenReturn(auslieferung);
		when(newsletterAuftraegeService.getVersandauftragAndNewsletterWithVersandauftragID(new Identifier("2")))
			.thenThrow(new MkGatewayRuntimeException("Datenmatsch"));
		doNothing().when(versandauftragStatusUpdater).markVersandauftragCompletedWithDataError(any(Versandauftrag.class));

		// Act
		try {

			processor.processNextAuslieferung();
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Datenmatsch", e.getMessage());

		}

		// Assert
		verify(newsletterAuftraegeService).getVersandauftragAndNewsletterWithVersandauftragID(new Identifier("2"));
		verify(versandauftragStatusUpdater, never()).markVersandauftragCompletedWithDataError(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragStarted(any(Versandauftrag.class));
		verify(auslieferungStatusUpdater).markAuslieferungCompleted(any(NewsletterAuslieferung.class));
		verify(auslieferungStatusUpdater, never()).markAuslieferungStarted(any(NewsletterAuslieferung.class));
		verify(versandauftragStatusUpdater, never()).updateStatusVersandauftrag(any(Versandauftrag.class), any(Integer.class));
		verify(mailService, never()).sendMail(any(DefaultEmailDaten.class));
	}

	@Test
	void should_processNextAuslieferungDoNothing_when_currentAuslieferungNotWaiting() {

		// hypothetisch, denn es kommen nur Aufträge mit Status WAITING zurück
		StatusAuslieferung[] values = new StatusAuslieferung[] { StatusAuslieferung.COMPLETED, StatusAuslieferung.ERRORS,
			StatusAuslieferung.IN_PROGRESS, StatusAuslieferung.NEW };
		StatusAuslieferung status = values[new Random().nextInt(values.length)];

		// Arrange
		NewsletterAuslieferung auslieferung = new NewsletterAuslieferung().withIdentifier(new Identifier("1"))
			.withVersandauftragId(new Identifier("2")).withStatus(status);

		Pair<Versandauftrag, Newsletter> versandauftragAndNewsletter = Pair.of(new Versandauftrag(), new Newsletter());

		when(auslieferungPicker.getNextPendingAuslieferung()).thenReturn(auslieferung);
		when(newsletterAuftraegeService.getVersandauftragAndNewsletterWithVersandauftragID(new Identifier("2")))
			.thenReturn(versandauftragAndNewsletter);

		// Act
		processor.processNextAuslieferung();

		// Assert
		verify(newsletterAuftraegeService).getVersandauftragAndNewsletterWithVersandauftragID(any(Identifier.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragCompletedWithDataError(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragStarted(any(Versandauftrag.class));
		verify(auslieferungStatusUpdater, never()).markAuslieferungCompleted(any(NewsletterAuslieferung.class));
		verify(auslieferungStatusUpdater, never()).markAuslieferungStarted(any(NewsletterAuslieferung.class));
		verify(versandauftragStatusUpdater, never()).updateStatusVersandauftrag(any(Versandauftrag.class), any(Integer.class));
		verify(mailService, never()).sendMail(any(DefaultEmailDaten.class));
	}

	@Test
	void should_processNextAuslieferungDoNothing_when_currentAuslieferungNeu() {

		// Arrange
		NewsletterAuslieferung auslieferung = new NewsletterAuslieferung().withIdentifier(new Identifier("1"))
			.withVersandauftragId(new Identifier("2")).withStatus(StatusAuslieferung.NEW);

		Pair<Versandauftrag, Newsletter> versandauftragAndNewsletter = Pair.of(new Versandauftrag(), new Newsletter());

		when(auslieferungPicker.getNextPendingAuslieferung()).thenReturn(auslieferung);
		when(newsletterAuftraegeService.getVersandauftragAndNewsletterWithVersandauftragID(new Identifier("2")))
			.thenReturn(versandauftragAndNewsletter);

		// Act
		processor.processNextAuslieferung();

		// Assert
		verify(newsletterAuftraegeService).getVersandauftragAndNewsletterWithVersandauftragID(any(Identifier.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragCompletedWithDataError(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragStarted(any(Versandauftrag.class));
		verify(auslieferungStatusUpdater, never()).markAuslieferungCompleted(any(NewsletterAuslieferung.class));
		verify(auslieferungStatusUpdater, never()).markAuslieferungStarted(any(NewsletterAuslieferung.class));
		verify(versandauftragStatusUpdater, never()).updateStatusVersandauftrag(any(Versandauftrag.class), any(Integer.class));
		verify(mailService, never()).sendMail(any(DefaultEmailDaten.class));
	}

	@Test
	void should_processNextAuslieferungStartVersandauftragAndAuslieferungAndSendMail_when_versandauftragAndCurrentAuslieferungWaiting() {

		// Arrange
		NewsletterAuslieferung auslieferung = new NewsletterAuslieferung().withIdentifier(new Identifier("1"))
			.withVersandauftragId(new Identifier("2")).withStatus(StatusAuslieferung.WAITING)
			.withEmpfaenger(new String[] { "heike@egladil.de", "mini@egladil.de" });

		Versandauftrag versandauftrag = new Versandauftrag().withStatus(StatusAuslieferung.WAITING);
		Pair<Versandauftrag, Newsletter> versandauftragAndNewsletter = Pair.of(versandauftrag, new Newsletter());

		when(auslieferungPicker.getNextPendingAuslieferung()).thenReturn(auslieferung);
		when(newsletterAuftraegeService.getVersandauftragAndNewsletterWithVersandauftragID(new Identifier("2")))
			.thenReturn(versandauftragAndNewsletter);

		// Act
		processor.processNextAuslieferung();

		// Assert
		verify(newsletterAuftraegeService).getVersandauftragAndNewsletterWithVersandauftragID(any(Identifier.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragCompletedWithDataError(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater).markVersandauftragStarted(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater).updateStatusVersandauftrag(any(Versandauftrag.class), any(Integer.class));
		verify(auslieferungStatusUpdater).markAuslieferungCompleted(any(NewsletterAuslieferung.class));
		verify(auslieferungStatusUpdater).markAuslieferungStarted(any(NewsletterAuslieferung.class));
		verify(mailService).sendMail(any(DefaultEmailDaten.class));
	}

	@Test
	void should_processNextAuslieferungStartAuslieferungAndSendMail_when_currentAuslieferungWaiting() {

		// Arrange
		NewsletterAuslieferung auslieferung = new NewsletterAuslieferung().withIdentifier(new Identifier("1"))
			.withVersandauftragId(new Identifier("2")).withStatus(StatusAuslieferung.WAITING)
			.withEmpfaenger(new String[] { "heike@egladil.de", "mini@egladil.de" });

		Versandauftrag versandauftrag = new Versandauftrag().withStatus(StatusAuslieferung.IN_PROGRESS);
		Pair<Versandauftrag, Newsletter> versandauftragAndNewsletter = Pair.of(versandauftrag, new Newsletter());

		when(auslieferungPicker.getNextPendingAuslieferung()).thenReturn(auslieferung);
		when(newsletterAuftraegeService.getVersandauftragAndNewsletterWithVersandauftragID(new Identifier("2")))
			.thenReturn(versandauftragAndNewsletter);

		// Act
		processor.processNextAuslieferung();

		// Assert
		verify(newsletterAuftraegeService).getVersandauftragAndNewsletterWithVersandauftragID(any(Identifier.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragCompletedWithDataError(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragStarted(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater).updateStatusVersandauftrag(any(Versandauftrag.class), any(Integer.class));
		verify(auslieferungStatusUpdater).markAuslieferungCompleted(any(NewsletterAuslieferung.class));
		verify(auslieferungStatusUpdater).markAuslieferungStarted(any(NewsletterAuslieferung.class));
		verify(mailService).sendMail(any(DefaultEmailDaten.class));
	}

	@Test
	void should_processNextAuslieferung_fireEvent_whenMailerThrowsInvalidMailAddressException() throws AddressException {

		// Arrange
		NewsletterAuslieferung auslieferung = new NewsletterAuslieferung().withIdentifier(new Identifier("1"))
			.withVersandauftragId(new Identifier("2")).withStatus(StatusAuslieferung.WAITING)
			.withEmpfaenger(new String[] { "heike@egladil.de", "mini@egladil.de" });

		Versandauftrag versandauftrag = new Versandauftrag().withStatus(StatusAuslieferung.IN_PROGRESS);
		Pair<Versandauftrag, Newsletter> versandauftragAndNewsletter = Pair.of(versandauftrag, new Newsletter());

		when(auslieferungPicker.getNextPendingAuslieferung()).thenReturn(auslieferung);
		when(newsletterAuftraegeService.getVersandauftragAndNewsletterWithVersandauftragID(new Identifier("2")))
			.thenReturn(versandauftragAndNewsletter);

		Address[] validSent = new Address[] { new InternetAddress("iche@egladil.de") };
		Address[] validUnsent = new Address[] { new InternetAddress("heike@egladil.de") };
		Address[] invalid = new Address[] { new InternetAddress("teufelchen") };

		InvalidMailAddressException exception = new InvalidMailAddressException("tja",
			new SendFailedException("tja", new Exception(), validSent, validUnsent, invalid));

		doThrow(exception).when(mailService).sendMail(any(DefaultEmailDaten.class));

		// Act
		processor.processNextAuslieferung();

		// Assert
		verify(newsletterAuftraegeService).getVersandauftragAndNewsletterWithVersandauftragID(any(Identifier.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragCompletedWithDataError(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragStarted(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater).updateStatusVersandauftrag(any(Versandauftrag.class), any(Integer.class));
		verify(auslieferungStatusUpdater).markAuslieferungCompleted(any(NewsletterAuslieferung.class));
		verify(auslieferungStatusUpdater).markAuslieferungStarted(any(NewsletterAuslieferung.class));
		verify(mailService).sendMail(any(DefaultEmailDaten.class));
		verify(domainEventHandler).handleEvent(any(NewsletterversandFailed.class));
	}

	@Test
	void should_processNextAuslieferung_notFireEvent_whenMailerThrowsEmailException() {

		// Arrange
		NewsletterAuslieferung auslieferung = new NewsletterAuslieferung().withIdentifier(new Identifier("1"))
			.withVersandauftragId(new Identifier("2")).withStatus(StatusAuslieferung.WAITING)
			.withEmpfaenger(new String[] { "heike@egladil.de", "mini@egladil.de" });

		Versandauftrag versandauftrag = new Versandauftrag().withStatus(StatusAuslieferung.IN_PROGRESS);
		Pair<Versandauftrag, Newsletter> versandauftragAndNewsletter = Pair.of(versandauftrag, new Newsletter());

		when(auslieferungPicker.getNextPendingAuslieferung()).thenReturn(auslieferung);
		when(newsletterAuftraegeService.getVersandauftragAndNewsletterWithVersandauftragID(new Identifier("2")))
			.thenReturn(versandauftragAndNewsletter);
		doThrow(EmailException.class).when(mailService).sendMail(any(DefaultEmailDaten.class));

		// Act
		processor.processNextAuslieferung();

		// Assert
		verify(newsletterAuftraegeService).getVersandauftragAndNewsletterWithVersandauftragID(any(Identifier.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragCompletedWithDataError(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragStarted(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater).updateStatusVersandauftrag(any(Versandauftrag.class), any(Integer.class));
		verify(auslieferungStatusUpdater).markAuslieferungCompleted(any(NewsletterAuslieferung.class));
		verify(auslieferungStatusUpdater).markAuslieferungStarted(any(NewsletterAuslieferung.class));
		verify(mailService).sendMail(any(DefaultEmailDaten.class));
		verify(domainEventHandler, never()).handleEvent(any(NewsletterversandFailed.class));
	}

	@Test
	void should_processNextAuslieferung_notPropagateEvent_whenMailerThrowsUnexpectedException() {

		// Arrange
		NewsletterAuslieferung auslieferung = new NewsletterAuslieferung().withIdentifier(new Identifier("1"))
			.withVersandauftragId(new Identifier("2")).withStatus(StatusAuslieferung.WAITING)
			.withEmpfaenger(new String[] { "heike@egladil.de", "mini@egladil.de" });

		Versandauftrag versandauftrag = new Versandauftrag().withStatus(StatusAuslieferung.IN_PROGRESS);
		Pair<Versandauftrag, Newsletter> versandauftragAndNewsletter = Pair.of(versandauftrag, new Newsletter());

		when(auslieferungPicker.getNextPendingAuslieferung()).thenReturn(auslieferung);
		when(newsletterAuftraegeService.getVersandauftragAndNewsletterWithVersandauftragID(new Identifier("2")))
			.thenReturn(versandauftragAndNewsletter);
		doThrow(RuntimeException.class).when(mailService).sendMail(any(DefaultEmailDaten.class));

		// Act
		processor.processNextAuslieferung();

		// Assert
		verify(newsletterAuftraegeService).getVersandauftragAndNewsletterWithVersandauftragID(any(Identifier.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragCompletedWithDataError(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater, never()).markVersandauftragStarted(any(Versandauftrag.class));
		verify(versandauftragStatusUpdater).updateStatusVersandauftrag(any(Versandauftrag.class), any(Integer.class));
		verify(auslieferungStatusUpdater).markAuslieferungCompleted(any(NewsletterAuslieferung.class));
		verify(auslieferungStatusUpdater).markAuslieferungStarted(any(NewsletterAuslieferung.class));
		verify(mailService).sendMail(any(DefaultEmailDaten.class));
		verify(domainEventHandler, never()).handleEvent(any(NewsletterversandFailed.class));
	}
}
