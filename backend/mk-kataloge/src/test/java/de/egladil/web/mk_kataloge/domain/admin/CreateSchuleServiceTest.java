// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
import de.egladil.web.mk_kataloge.domain.error.DataInconsistencyException;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_kataloge.domain.event.MailNotSent;
import de.egladil.web.mk_kataloge.domain.katalogantrag.KatalogMailService;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * CreateSchuleServiceTest
 */
public class CreateSchuleServiceTest {

	private SchuleRepository schuleRepository;

	private KatalogMailService mailService;

	private CreateSchuleService service;

	@BeforeEach
	void setUp() {

		schuleRepository = Mockito.mock(SchuleRepository.class);
		mailService = KatalogMailService.createForTest();
		service = CreateSchuleService.createForTest(schuleRepository, mailService);
	}

	@Test
	void should_mapSchulePayloadToSchule() {

		// Arrange
		SchulePayload schulePayload = createPayloadForTest();

		// Act
		Schule schule = service.mapFromSchulePayload(schulePayload);

		// Assert
		assertEquals("GHKGKGK", schule.getImportiertesKuerzel());
		assertNull(schule.getKuerzel());
		assertEquals("Baumschule", schule.getName());
		assertEquals("TFFFVHVH", schule.getOrtKuerzel());
		assertEquals("Brasilia", schule.getOrtName());
		assertEquals("BR", schule.getLandKuerzel());
		assertEquals("Brasilien", schule.getLandName());

	}

	@Test
	void should_notPersistSchuleAndSendMail_when_Vorhanden() {

		// Arrange
		SchulePayload schulePayload = this.createPayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		Mockito.when(schuleRepository.findSchuleInOrtMitName(schule.getOrtKuerzel(), schule.getName()))
			.thenReturn(Optional.of(schule));

		// Act
		ResponsePayload responsePayload = service.schuleAnlegen(schulePayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals("Diese Schule gibt es bereits.", messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals(schulePayload, responseData);

		assertNull(service.getMailNotSent());
		assertNull(service.getRegisteredDataInconsistency());

		assertTrue(mailService.isMailSent());
	}

	@Test
	void should_notPersistSchuleAndNotSendMail_when_VorhandenAberKeineAuftraggeberMail() {

		// Arrange
		SchulePayload schulePayload = this.createPayloadForTest();
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		Mockito.when(schuleRepository.findSchuleInOrtMitName(schule.getOrtKuerzel(), schule.getName()))
			.thenReturn(Optional.of(schule));

		// Act
		ResponsePayload responsePayload = service.schuleAnlegen(schulePayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals("Diese Schule gibt es bereits.", messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals(schulePayload, responseData);

		assertNull(service.getMailNotSent());
		assertNull(service.getRegisteredDataInconsistency());

		assertFalse(mailService.isMailSent());

	}

	@Test
	void should_persistSchuleAndSendMail_when_NichtVorhanden() {

		// Arrange
		SchulePayload schulePayload = this.createPayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		Mockito.when(schuleRepository.findSchuleInOrtMitName(schule.getOrtKuerzel(), schule.getName()))
			.thenReturn(Optional.empty());

		Mockito.when(schuleRepository.addSchule(schule)).thenReturn(Boolean.TRUE);

		// Act
		ResponsePayload responsePayload = service.schuleAnlegen(schulePayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Die Schule wurde erfolgreich angelegt.", messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals(schulePayload, responseData);

		assertNull(service.getMailNotSent());
		assertNull(service.getRegisteredDataInconsistency());

		assertTrue(mailService.isMailSent());
	}

	@Test
	void should_persistSchuleAndNotSendMail_when_NichtVorhandenUndKeineEmpfaengermail() {

		// Arrange
		SchulePayload schulePayload = this.createPayloadForTest();
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		Mockito.when(schuleRepository.findSchuleInOrtMitName(schule.getOrtKuerzel(), schule.getName()))
			.thenReturn(Optional.empty());

		Mockito.when(schuleRepository.addSchule(schule)).thenReturn(Boolean.TRUE);

		// Act
		ResponsePayload responsePayload = service.schuleAnlegen(schulePayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Die Schule wurde erfolgreich angelegt.", messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals(schulePayload, responseData);

		assertNull(service.getMailNotSent());
		assertNull(service.getRegisteredDataInconsistency());

		assertFalse(mailService.isMailSent());
	}

	@Test
	void should_mailserverExceptionNotPreventOk_when_NichtVorhanden() {

		// Arrange
		SchulePayload schulePayload = this.createPayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		Mockito.when(schuleRepository.findSchuleInOrtMitName(schule.getOrtKuerzel(), schule.getName()))
			.thenReturn(Optional.empty());

		Mockito.when(schuleRepository.addSchule(schule)).thenReturn(Boolean.TRUE);

		mailService = KatalogMailService.createForTestWithMailException();

		CreateSchuleService theService = CreateSchuleService.createForTest(schuleRepository, mailService);

		// Act
		ResponsePayload responsePayload = theService.schuleAnlegen(schulePayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Die Schule wurde erfolgreich angelegt.", messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals(schulePayload, responseData);

		assertNotNull(theService.getMailNotSent());
		assertNull(theService.getRegisteredDataInconsistency());

		MailNotSent eventObject = theService.getMailNotSent();
		assertNotNull(eventObject.occuredOn());
		assertEquals("Die Mail konnte nicht gesendet werden: Das ist eine gemockte Mailexception", eventObject.toString());

		assertFalse(mailService.isMailSent());
	}

	@Test
	void should_createDataInconsistencyEventAndNotSendMail_when_DataInconsistencyDetected() {

		// Arrange
		SchulePayload schulePayload = this.createPayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		Mockito.when(schuleRepository.findSchuleInOrtMitName(schule.getOrtKuerzel(), schule.getName()))
			.thenReturn(Optional.empty());

		Mockito.when(schuleRepository.addSchule(schule))
			.thenThrow(new DataInconsistencyException("irgendwas in der DB ist falsch."));

		// Act + Assert
		try {

			service.schuleAnlegen(schulePayload);

			fail("keine KatalogAPIException");
		} catch (KatalogAPIException e) {

			assertEquals("schuleAnlegen: irgendwas in der DB ist falsch.", e.getMessage());

			assertNull(service.getMailNotSent());
			DataInconsistencyRegistered eventObject = service.getRegisteredDataInconsistency();

			assertNotNull(eventObject);
			assertNotNull(eventObject.occuredOn());
			assertEquals("schuleAnlegen: irgendwas in der DB ist falsch.", eventObject.toString());

			assertFalse(mailService.isMailSent());

		}

	}

	@Test
	void should_throwExceptionAndNotSendMail_when_PersistenceException() {

		// Arrange
		SchulePayload schulePayload = this.createPayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		Mockito.when(schuleRepository.findSchuleInOrtMitName(schule.getOrtKuerzel(), schule.getName()))
			.thenReturn(Optional.empty());

		Mockito.when(schuleRepository.addSchule(schule))
			.thenThrow(new PersistenceException("irgendwas in der DB ist falsch."));

		// Act + Assert
		try {

			service.schuleAnlegen(schulePayload);

			fail("keine KatalogAPIException");
		} catch (KatalogAPIException e) {

			assertEquals("Die Schule konnte wegen eines Serverfehlers nicht angelegt werden.", e.getMessage());

			assertNull(service.getMailNotSent());
			DataInconsistencyRegistered eventObject = service.getRegisteredDataInconsistency();
			assertNull(eventObject);

			assertFalse(mailService.isMailSent());
		}

	}

	private SchulePayload createPayloadForTest() {

		String kuerzel = "GHKGKGK";
		String name = "Baumschule";
		String kuerzelOrt = "TFFFVHVH";
		String nameOrt = "Brasilia";
		String kuerzelLand = "BR";
		String nameLand = "Brasilien";

		SchulePayload schulePayload = SchulePayload.create(kuerzel, name, kuerzelOrt, nameOrt, kuerzelLand, nameLand);

		return schulePayload;

	}

}
