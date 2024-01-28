// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_mailer.exception.EmailException;
import de.egladil.web.commons_mailer.exception.InvalidMailAddressException;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.NewsletterauslieferungenRepository;
import de.egladil.web.mk_gateway.profiles.FullDatabaseTestProfile;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.mail.SendFailedException;

/**
 * NewsletterversandServiceTest
 */
@QuarkusTest
@TestProfile(value = FullDatabaseTestProfile.class)
public class NewsletterversandServiceTest {

	@ConfigProperty(name = "emails.testempfaenger")
	String emailEmpfaenger;

	@Inject
	NewsletterversandService service;

	@InjectMock
	NewsletterauslieferungenRepository newsletterAuslieferungenRepository;

	@InjectMock
	NewsletterRepository newsletterRepository;

	@InjectMock
	AdminMailService mailService;

	@InjectMock
	NewsletterAuftraegeService newsletterAuftraegeService;

	@Test
	void should_checkAndSendDoNothing_when_keineVersandinfo() {

		// Arrange
		when(newsletterAuftraegeService.findNichtBeendeteVersandauftraege()).thenReturn(new ArrayList<>());

		// Act
		service.checkAndSend();

		// Assert
		verify(newsletterAuftraegeService).findNichtBeendeteVersandauftraege();
		verify(newsletterRepository, never()).ofId(any(Identifier.class));
		verify(newsletterAuslieferungenRepository, never()).findAllWithVersandauftrag(any(Identifier.class));
		verify(mailService, never()).sendMail(any(DefaultEmailDaten.class));
		verify(newsletterAuftraegeService, never()).versandauftragSpeichern(any(Versandauftrag.class));
		verify(newsletterAuslieferungenRepository, never()).updateAuslieferung(any(NewsletterAuslieferung.class));
	}

	@Test
	void should_checkAndSendThrowException_when_VersandinfoOhneNewsletter() {

		// Arrange
		Identifier uuid = new Identifier(UUID.randomUUID().toString());
		Identifier newsletterId = new Identifier("925d3242-7365-44b4-8b60-bc6d68cd1c22");

		List<Versandauftrag> versandinfos = new ArrayList<>();

		{

			Versandauftrag versandinfo = new Versandauftrag()
				.withAnzahlAktuellVersendet(0)
				.withAnzahlEmpaenger(1000)
				.withErfasstAm("28.02.2024 13:13:15")
				.withStatus(StatusAuslieferung.WAITING)
				.withIdentifier(uuid)
				.withNewsletterID(newsletterId);
			versandinfos.add(versandinfo);
		}

		when(newsletterAuftraegeService.findNichtBeendeteVersandauftraege()).thenReturn(versandinfos);
		when(newsletterRepository.ofId(newsletterId)).thenReturn(Optional.empty());

		// Act + Assert
		try {

			service.checkAndSend();
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals(
				"Datenmatsch: es gibt eine Versandauftrag und NewsletterAuslieferung mit newsletterID=925d3242-7365-44b4-8b60-bc6d68cd1c22', aber keinen Newsletter mehr mit dieser ID",
				e.getMessage());
		}

		// Assert
		verify(newsletterAuftraegeService).findNichtBeendeteVersandauftraege();
		verify(newsletterRepository).ofId(any(Identifier.class));
		verify(newsletterAuslieferungenRepository, never()).findAllWithVersandauftrag(any(Identifier.class));
		verify(mailService, never()).sendMail(any(DefaultEmailDaten.class));
		verify(newsletterAuftraegeService).versandauftragSpeichern(any(Versandauftrag.class));
		verify(newsletterAuslieferungenRepository, never()).updateAuslieferung(any(NewsletterAuslieferung.class));
	}

	@Test
	void should_checkAndSendMarkVersandinfoCompleted_when_versandinfoInProgressButAllAuslieferungenCompleted() {

		// Arrange
		Identifier auslieferungId = new Identifier(UUID.randomUUID().toString());
		Identifier newsletterId = new Identifier("925d3242-7365-44b4-8b60-bc6d68cd1c22");
		Identifier versandauftragId = new Identifier("9275b8c8-85a9-4feb-8758-fb6ee249a820");

		List<Versandauftrag> versandinfos = new ArrayList<>();

		{

			Versandauftrag versandauftrag = new Versandauftrag()
				.withAnzahlAktuellVersendet(0)
				.withAnzahlEmpaenger(1000)
				.withErfasstAm("28.02.2024 13:13:15")
				.withStatus(StatusAuslieferung.IN_PROGRESS)
				.withIdentifier(versandauftragId)
				.withNewsletterID(newsletterId);
			versandinfos.add(versandauftrag);
		}

		Newsletter newsletter = new Newsletter().withIdentifier(newsletterId);

		when(newsletterAuftraegeService.findNichtBeendeteVersandauftraege()).thenReturn(versandinfos);
		when(newsletterRepository.ofId(newsletterId)).thenReturn(Optional.of(newsletter));

		List<NewsletterAuslieferung> auslieferungen = new ArrayList<>();

		auslieferungen.add(new NewsletterAuslieferung().withEmpfaenger(StringUtils.split(emailEmpfaenger, ','))
			.withStatus(StatusAuslieferung.COMPLETED).withVersandauftragId(versandauftragId).withIdentifier(auslieferungId));
		auslieferungen.add(new NewsletterAuslieferung().withEmpfaenger(StringUtils.split(emailEmpfaenger, ','))
			.withStatus(StatusAuslieferung.ERRORS).withVersandauftragId(versandauftragId).withIdentifier(auslieferungId));

		when(newsletterAuslieferungenRepository.findAllWithVersandauftrag(auslieferungId)).thenReturn(auslieferungen);
		when(newsletterAuftraegeService.versandauftragSpeichern(any(Versandauftrag.class))).thenReturn(versandinfos.get(0));

		// Act
		service.checkAndSend();

		// Assert
		verify(newsletterAuftraegeService).findNichtBeendeteVersandauftraege();
		verify(newsletterRepository).ofId(any(Identifier.class));
		verify(newsletterAuslieferungenRepository).findAllWithVersandauftrag(any(Identifier.class));
		verify(mailService, never()).sendMail(any(DefaultEmailDaten.class));
		verify(newsletterAuftraegeService).versandauftragSpeichern(any(Versandauftrag.class));
		verify(newsletterAuslieferungenRepository, never()).updateAuslieferung(any(NewsletterAuslieferung.class));
	}

	@Test
	void should_checkAndSendMarkVersandinfoCompleted_when_versandinfoInProgressButAllAuslieferungenCompletedWithoutErrors() {

		// Arrange
		Identifier auslieferungId = new Identifier(UUID.randomUUID().toString());
		Identifier newsletterId = new Identifier("925d3242-7365-44b4-8b60-bc6d68cd1c22");
		Identifier versandauftragId = new Identifier("9275b8c8-85a9-4feb-8758-fb6ee249a820");

		List<Versandauftrag> versandinfos = new ArrayList<>();

		{

			Versandauftrag versandauftrag = new Versandauftrag()
				.withAnzahlAktuellVersendet(0)
				.withAnzahlEmpaenger(1000)
				.withErfasstAm("28.02.2024 13:13:15")
				.withStatus(StatusAuslieferung.IN_PROGRESS)
				.withIdentifier(versandauftragId)
				.withNewsletterID(newsletterId);
			versandinfos.add(versandauftrag);
		}

		Newsletter newsletter = new Newsletter().withIdentifier(newsletterId);

		when(newsletterAuftraegeService.findNichtBeendeteVersandauftraege()).thenReturn(versandinfos);
		when(newsletterRepository.ofId(newsletterId)).thenReturn(Optional.of(newsletter));

		List<NewsletterAuslieferung> auslieferungen = new ArrayList<>();

		auslieferungen.add(new NewsletterAuslieferung().withEmpfaenger(StringUtils.split(emailEmpfaenger, ','))
			.withStatus(StatusAuslieferung.COMPLETED).withVersandauftragId(versandauftragId).withIdentifier(auslieferungId));
		auslieferungen.add(new NewsletterAuslieferung().withEmpfaenger(StringUtils.split(emailEmpfaenger, ','))
			.withStatus(StatusAuslieferung.COMPLETED).withVersandauftragId(versandauftragId).withIdentifier(auslieferungId));

		when(newsletterAuslieferungenRepository.findAllWithVersandauftrag(auslieferungId)).thenReturn(auslieferungen);
		when(newsletterAuftraegeService.versandauftragSpeichern(any(Versandauftrag.class))).thenReturn(versandinfos.get(0));

		// Act
		service.checkAndSend();

		// Assert
		verify(newsletterAuftraegeService).findNichtBeendeteVersandauftraege();
		verify(newsletterRepository).ofId(any(Identifier.class));
		verify(newsletterAuslieferungenRepository).findAllWithVersandauftrag(any(Identifier.class));
		verify(mailService, never()).sendMail(any(DefaultEmailDaten.class));
		verify(newsletterAuftraegeService).versandauftragSpeichern(any(Versandauftrag.class));
		verify(newsletterAuslieferungenRepository, never()).updateAuslieferung(any(NewsletterAuslieferung.class));
	}

	@Test
	void should_checkAndSendMarkVersandCompleted_when_versandinfoWaitingButHasNoAuslieferungen() {

		// Arrange
		Identifier newsletterId = new Identifier("925d3242-7365-44b4-8b60-bc6d68cd1c22");
		Identifier versandauftragId = new Identifier("9275b8c8-85a9-4feb-8758-fb6ee249a820");

		List<Versandauftrag> versandinfos = new ArrayList<>();

		{

			Versandauftrag versandinfo = new Versandauftrag()
				.withAnzahlAktuellVersendet(0)
				.withAnzahlEmpaenger(1000)
				.withErfasstAm("28.02.2024 13:13:15")
				.withStatus(StatusAuslieferung.WAITING)
				.withIdentifier(versandauftragId)
				.withNewsletterID(newsletterId);
			versandinfos.add(versandinfo);
		}

		Newsletter newsletter = new Newsletter().withIdentifier(newsletterId);

		when(newsletterAuftraegeService.findNichtBeendeteVersandauftraege()).thenReturn(versandinfos);
		when(newsletterRepository.ofId(newsletterId)).thenReturn(Optional.of(newsletter));

		List<NewsletterAuslieferung> auslieferungen = new ArrayList<>();
		when(newsletterAuslieferungenRepository.findAllWithVersandauftrag(versandauftragId)).thenReturn(auslieferungen);

		when(newsletterAuftraegeService.versandauftragSpeichern(any(Versandauftrag.class))).thenReturn(versandinfos.get(0));

		// Act
		service.checkAndSend();

		// Assert
		verify(newsletterAuftraegeService).findNichtBeendeteVersandauftraege();
		verify(newsletterRepository).ofId(any(Identifier.class));
		verify(newsletterAuslieferungenRepository).findAllWithVersandauftrag(any(Identifier.class));
		verify(mailService, never()).sendMail(any(DefaultEmailDaten.class));
		verify(newsletterAuftraegeService, times(2)).versandauftragSpeichern(any(Versandauftrag.class));
		verify(newsletterAuslieferungenRepository, never()).updateAuslieferung(any(NewsletterAuslieferung.class));
	}

	@Test
	void should_checkAndSendStartVersandButNotSendMailsAndMarkAuslieferungCompleted_when_versandinfoWaitingAndAuslieferungInProgress() {

		// Arrange
		Identifier auslieferungId = new Identifier(UUID.randomUUID().toString());
		Identifier newsletterId = new Identifier("925d3242-7365-44b4-8b60-bc6d68cd1c22");
		Identifier versandauftragId = new Identifier("9275b8c8-85a9-4feb-8758-fb6ee249a820");

		List<Versandauftrag> versandinfos = new ArrayList<>();

		{

			Versandauftrag versandinfo = new Versandauftrag()
				.withAnzahlAktuellVersendet(0)
				.withAnzahlEmpaenger(1000)
				.withErfasstAm("28.02.2024 13:13:15")
				.withStatus(StatusAuslieferung.WAITING)
				.withIdentifier(versandauftragId)
				.withNewsletterID(newsletterId);
			versandinfos.add(versandinfo);
		}

		Newsletter newsletter = new Newsletter().withIdentifier(newsletterId);

		when(newsletterAuftraegeService.findNichtBeendeteVersandauftraege()).thenReturn(versandinfos);
		when(newsletterRepository.ofId(newsletterId)).thenReturn(Optional.of(newsletter));

		List<NewsletterAuslieferung> auslieferungen = new ArrayList<>();

		auslieferungen.add(new NewsletterAuslieferung().withEmpfaenger(StringUtils.split(emailEmpfaenger, ','))
			.withStatus(StatusAuslieferung.IN_PROGRESS).withVersandauftragId(versandauftragId).withIdentifier(auslieferungId));

		when(newsletterAuslieferungenRepository.findAllWithVersandauftrag(versandauftragId)).thenReturn(auslieferungen);

		when(newsletterAuftraegeService.versandauftragSpeichern(any(Versandauftrag.class))).thenReturn(versandinfos.get(0));

		// Act
		service.checkAndSend();

		// Assert
		verify(newsletterAuftraegeService).findNichtBeendeteVersandauftraege();
		verify(newsletterRepository).ofId(any(Identifier.class));
		verify(newsletterAuslieferungenRepository).findAllWithVersandauftrag(any(Identifier.class));
		verify(mailService, never()).sendMail(any(DefaultEmailDaten.class));
		verify(newsletterAuftraegeService, times(1)).versandauftragSpeichern(any(Versandauftrag.class));
		verify(newsletterAuslieferungenRepository, never()).updateAuslieferung(any(NewsletterAuslieferung.class));
	}

	@Test
	void should_checkAndSendStartVersandSendMailsAndMarkAuslieferungCompleted_when_versandinfoWaitingAndHasWaitingAuslieferungen() {

		// Arrange
		Identifier auslieferungId = new Identifier(UUID.randomUUID().toString());
		Identifier newsletterId = new Identifier("925d3242-7365-44b4-8b60-bc6d68cd1c22");
		Identifier versandauftragId = new Identifier("9275b8c8-85a9-4feb-8758-fb6ee249a820");

		List<Versandauftrag> versandinfos = new ArrayList<>();

		{

			Versandauftrag versandinfo = new Versandauftrag()
				.withAnzahlAktuellVersendet(0)
				.withAnzahlEmpaenger(1000)
				.withErfasstAm("28.02.2024 13:13:15")
				.withStatus(StatusAuslieferung.WAITING)
				.withIdentifier(versandauftragId)
				.withNewsletterID(newsletterId);
			versandinfos.add(versandinfo);
		}

		Newsletter newsletter = new Newsletter().withIdentifier(newsletterId);

		when(newsletterAuftraegeService.findNichtBeendeteVersandauftraege()).thenReturn(versandinfos);
		when(newsletterRepository.ofId(newsletterId)).thenReturn(Optional.of(newsletter));

		List<NewsletterAuslieferung> auslieferungen = new ArrayList<>();

		auslieferungen.add(new NewsletterAuslieferung().withEmpfaenger(StringUtils.split(emailEmpfaenger, ','))
			.withStatus(StatusAuslieferung.WAITING).withVersandauftragId(versandauftragId).withIdentifier(auslieferungId));

		when(newsletterAuslieferungenRepository.findAllWithVersandauftrag(versandauftragId)).thenReturn(auslieferungen);

		when(newsletterAuftraegeService.versandauftragSpeichern(any(Versandauftrag.class))).thenReturn(versandinfos.get(0));

		doNothing().when(mailService).sendMail(any(DefaultEmailDaten.class));

		// Act
		service.checkAndSend();

		// Assert
		verify(newsletterAuftraegeService).findNichtBeendeteVersandauftraege();
		verify(newsletterRepository).ofId(any(Identifier.class));
		verify(newsletterAuslieferungenRepository).findAllWithVersandauftrag(any(Identifier.class));
		verify(mailService).sendMail(any(DefaultEmailDaten.class));
		verify(newsletterAuftraegeService, times(2)).versandauftragSpeichern(any(Versandauftrag.class));
		verify(newsletterAuslieferungenRepository, times(2)).updateAuslieferung(any(NewsletterAuslieferung.class));
	}

	@Test
	void should_checkAndSendUpdateStatus_when_mailserviceThrowsInvalidMailAddresses() {

		// Arrange
		Identifier auslieferungId = new Identifier(UUID.randomUUID().toString());
		Identifier newsletterId = new Identifier("925d3242-7365-44b4-8b60-bc6d68cd1c22");
		Identifier versandauftragId = new Identifier("9275b8c8-85a9-4feb-8758-fb6ee249a820");

		List<Versandauftrag> versandinfos = new ArrayList<>();

		{

			Versandauftrag versandinfo = new Versandauftrag()
				.withAnzahlAktuellVersendet(0)
				.withAnzahlEmpaenger(1000)
				.withErfasstAm("28.02.2024 13:13:15")
				.withStatus(StatusAuslieferung.WAITING)
				.withIdentifier(versandauftragId)
				.withNewsletterID(newsletterId);
			versandinfos.add(versandinfo);
		}

		Newsletter newsletter = new Newsletter().withIdentifier(newsletterId);

		when(newsletterAuftraegeService.findNichtBeendeteVersandauftraege()).thenReturn(versandinfos);
		when(newsletterRepository.ofId(newsletterId)).thenReturn(Optional.of(newsletter));

		List<NewsletterAuslieferung> auslieferungen = new ArrayList<>();

		auslieferungen.add(new NewsletterAuslieferung().withEmpfaenger(StringUtils.split(emailEmpfaenger, ','))
			.withStatus(StatusAuslieferung.WAITING).withVersandauftragId(versandauftragId).withIdentifier(auslieferungId));

		when(newsletterAuslieferungenRepository.findAllWithVersandauftrag(versandauftragId)).thenReturn(auslieferungen);

		when(newsletterAuftraegeService.versandauftragSpeichern(any(Versandauftrag.class))).thenReturn(versandinfos.get(0));

		doThrow(new InvalidMailAddressException("lauter invalid mailadressen", new SendFailedException(
			"bla"))).when(mailService)
				.sendMail(any(DefaultEmailDaten.class));

		// Act
		service.checkAndSend();

		// Assert
		verify(newsletterAuftraegeService).findNichtBeendeteVersandauftraege();
		verify(newsletterRepository).ofId(any(Identifier.class));
		verify(newsletterAuslieferungenRepository).findAllWithVersandauftrag(any(Identifier.class));
		verify(mailService).sendMail(any(DefaultEmailDaten.class));
		verify(newsletterAuftraegeService, times(2)).versandauftragSpeichern(any(Versandauftrag.class));
		verify(newsletterAuslieferungenRepository, times(2)).updateAuslieferung(any(NewsletterAuslieferung.class));
	}

	@Test
	void should_checkAndSendUpdateStatus_when_mailserviceThrowsEmailException() {

		// Arrange
		Identifier auslieferungId = new Identifier(UUID.randomUUID().toString());
		Identifier newsletterId = new Identifier("925d3242-7365-44b4-8b60-bc6d68cd1c22");
		Identifier versandauftragId = new Identifier("9275b8c8-85a9-4feb-8758-fb6ee249a820");

		List<Versandauftrag> versandinfos = new ArrayList<>();

		{

			Versandauftrag versandinfo = new Versandauftrag()
				.withAnzahlAktuellVersendet(0)
				.withAnzahlEmpaenger(1000)
				.withErfasstAm("28.02.2024 13:13:15")
				.withStatus(StatusAuslieferung.WAITING)
				.withIdentifier(versandauftragId)
				.withNewsletterID(newsletterId);
			versandinfos.add(versandinfo);
		}

		Newsletter newsletter = new Newsletter().withIdentifier(newsletterId);

		when(newsletterAuftraegeService.findNichtBeendeteVersandauftraege()).thenReturn(versandinfos);
		when(newsletterRepository.ofId(newsletterId)).thenReturn(Optional.of(newsletter));

		List<NewsletterAuslieferung> auslieferungen = new ArrayList<>();

		auslieferungen.add(new NewsletterAuslieferung().withEmpfaenger(StringUtils.split(emailEmpfaenger, ','))
			.withStatus(StatusAuslieferung.WAITING).withVersandauftragId(versandauftragId).withIdentifier(auslieferungId));

		when(newsletterAuslieferungenRepository.findAllWithVersandauftrag(versandauftragId)).thenReturn(auslieferungen);

		when(newsletterAuftraegeService.versandauftragSpeichern(any(Versandauftrag.class))).thenReturn(versandinfos.get(0));

		doThrow(new EmailException("irgendeine blöde EmailException")).when(mailService)
			.sendMail(any(DefaultEmailDaten.class));

		// Act
		service.checkAndSend();

		// Assert
		verify(newsletterAuftraegeService).findNichtBeendeteVersandauftraege();
		verify(newsletterRepository).ofId(any(Identifier.class));
		verify(newsletterAuslieferungenRepository).findAllWithVersandauftrag(any(Identifier.class));
		verify(mailService).sendMail(any(DefaultEmailDaten.class));
		verify(newsletterAuftraegeService, times(2)).versandauftragSpeichern(any(Versandauftrag.class));
		verify(newsletterAuslieferungenRepository, times(2)).updateAuslieferung(any(NewsletterAuslieferung.class));
	}

	@Test
	void should_checkAndSendUpdateStatus_when_mailserviceThrowsUnexpectedException() {

		// Arrange
		Identifier auslieferungId = new Identifier(UUID.randomUUID().toString());
		Identifier newsletterId = new Identifier("925d3242-7365-44b4-8b60-bc6d68cd1c22");
		Identifier versandauftragId = new Identifier("9275b8c8-85a9-4feb-8758-fb6ee249a820");

		List<Versandauftrag> versandinfos = new ArrayList<>();

		{

			Versandauftrag versandinfo = new Versandauftrag()
				.withAnzahlAktuellVersendet(0)
				.withAnzahlEmpaenger(1000)
				.withErfasstAm("28.02.2024 13:13:15")
				.withStatus(StatusAuslieferung.WAITING)
				.withIdentifier(versandauftragId)
				.withNewsletterID(newsletterId);
			versandinfos.add(versandinfo);
		}

		Newsletter newsletter = new Newsletter().withIdentifier(newsletterId);

		when(newsletterAuftraegeService.findNichtBeendeteVersandauftraege()).thenReturn(versandinfos);
		when(newsletterRepository.ofId(newsletterId)).thenReturn(Optional.of(newsletter));

		List<NewsletterAuslieferung> auslieferungen = new ArrayList<>();

		auslieferungen.add(new NewsletterAuslieferung().withEmpfaenger(StringUtils.split(emailEmpfaenger, ','))
			.withStatus(StatusAuslieferung.WAITING).withVersandauftragId(versandauftragId).withIdentifier(auslieferungId));

		when(newsletterAuslieferungenRepository.findAllWithVersandauftrag(versandauftragId)).thenReturn(auslieferungen);

		when(newsletterAuftraegeService.versandauftragSpeichern(any(Versandauftrag.class))).thenReturn(versandinfos.get(0));

		doThrow(new RuntimeException("RuntimeException")).when(mailService)
			.sendMail(any(DefaultEmailDaten.class));

		// Act
		service.checkAndSend();

		// Assert
		verify(newsletterAuftraegeService).findNichtBeendeteVersandauftraege();
		verify(newsletterRepository).ofId(any(Identifier.class));
		verify(newsletterAuslieferungenRepository).findAllWithVersandauftrag(any(Identifier.class));
		verify(mailService).sendMail(any(DefaultEmailDaten.class));
		verify(newsletterAuftraegeService, times(2)).versandauftragSpeichern(any(Versandauftrag.class));
		verify(newsletterAuslieferungenRepository, times(2)).updateAuslieferung(any(NewsletterAuslieferung.class));
	}
}
