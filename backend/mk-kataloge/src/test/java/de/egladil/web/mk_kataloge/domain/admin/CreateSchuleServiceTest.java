// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import de.egladil.web.mk_kataloge.domain.error.DuplicateEntityException;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * CreateSchuleServiceTest
 */
public class CreateSchuleServiceTest {

	private SchuleRepository schuleRepository;

	private ChangeSchulenMailDelegate mailDelegate;

	private CreateSchuleService service;

	@BeforeEach
	void setUp() {

		mailDelegate = Mockito.mock(ChangeSchulenMailDelegate.class);
		schuleRepository = Mockito.mock(SchuleRepository.class);
		service = CreateSchuleService.createForTest(schuleRepository, mailDelegate);
	}

	@Test
	void should_mapSchulePayloadToSchule() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createPayloadForTest();

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
		SchulePayload schulePayload = ChangeKatalogTestUtils.createPayloadForTest();
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

		Mockito.verify(mailDelegate, Mockito.times(1)).sendSchuleCreatedMailQuietly(schulePayload);

		assertNull(service.getRegisteredDataInconsistency());

		Mockito.verify(mailDelegate, Mockito.times(1)).sendSchuleCreatedMailQuietly(schulePayload);
	}

	@Test
	void should_notPersistSchuleAndNotSendMail_when_VorhandenAberKeineAuftraggeberMail() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createPayloadForTest();
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

		Mockito.verify(mailDelegate, Mockito.times(0)).sendSchuleCreatedMailQuietly(schulePayload);
		assertNull(service.getRegisteredDataInconsistency());

	}

	@Test
	void should_persistSchuleAndSendMail_when_NichtVorhanden() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createPayloadForTest();
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

		Mockito.verify(mailDelegate, Mockito.times(1)).sendSchuleCreatedMailQuietly(schulePayload);
		assertNull(service.getRegisteredDataInconsistency());
	}

	@Test
	void should_persistSchuleAndNotSendMail_when_NichtVorhandenUndKeineEmpfaengermail() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createPayloadForTest();
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

		Mockito.verify(mailDelegate, Mockito.times(0)).sendSchuleCreatedMailQuietly(schulePayload);
		assertNull(service.getRegisteredDataInconsistency());
	}

	@Test
	void should_mailserverExceptionNotPreventOk_when_NichtVorhanden() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createPayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		Mockito.when(schuleRepository.findSchuleInOrtMitName(schule.getOrtKuerzel(), schule.getName()))
			.thenReturn(Optional.empty());

		Mockito.when(schuleRepository.addSchule(schule)).thenReturn(Boolean.TRUE);

		Mockito.when(mailDelegate.sendSchuleCreatedMailQuietly(schulePayload))
			.thenReturn(Boolean.FALSE);

		// Act
		ResponsePayload responsePayload = service.schuleAnlegen(schulePayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Die Schule wurde erfolgreich angelegt.", messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals(schulePayload, responseData);

		Mockito.verify(mailDelegate, Mockito.times(1)).sendSchuleCreatedMailQuietly(schulePayload);
		assertNull(service.getRegisteredDataInconsistency());
	}

	@Test
	void should_createDataInconsistencyEventAndNotSendMail_when_DataInconsistencyDetected() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createPayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		Mockito.when(schuleRepository.findSchuleInOrtMitName(schule.getOrtKuerzel(), schule.getName()))
			.thenThrow(new DataInconsistencyException("irgendwas in der DB ist falsch."));

		Mockito.when(schuleRepository.addSchule(schule))
			.thenThrow(new DataInconsistencyException("irgendwas in der DB ist falsch."));

		// Act + Assert
		try {

			service.schuleAnlegen(schulePayload);

			fail("keine KatalogAPIException");
		} catch (KatalogAPIException e) {

			assertEquals("schuleAnlegen: irgendwas in der DB ist falsch.", e.getMessage());

			Mockito.verify(mailDelegate, Mockito.times(0)).sendSchuleCreatedMailQuietly(schulePayload);
			DataInconsistencyRegistered eventObject = service.getRegisteredDataInconsistency();

			assertNotNull(eventObject);
			assertNotNull(eventObject.occuredOn());
			assertEquals("schuleAnlegen: irgendwas in der DB ist falsch.", eventObject.toString());
		}

	}

	@Test
	void should_createDataInconsistencyEventAndNotSendMail_when_DuplicateEntryDetected() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createPayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		Mockito.when(schuleRepository.findSchuleInOrtMitName(schule.getOrtKuerzel(), schule.getName()))
			.thenReturn(Optional.empty());

		Mockito.when(schuleRepository.addSchule(schule))
			.thenThrow(new DuplicateEntityException("Das Schulkürzel gibet schon."));

		// Act + Assert
		try {

			service.schuleAnlegen(schulePayload);

			fail("keine KatalogAPIException");
		} catch (KatalogAPIException e) {

			assertEquals("schuleAnlegen: Das Schulkürzel gibet schon.", e.getMessage());

			Mockito.verify(mailDelegate, Mockito.times(0)).sendSchuleCreatedMailQuietly(schulePayload);
			DataInconsistencyRegistered eventObject = service.getRegisteredDataInconsistency();

			assertNotNull(eventObject);
			assertNotNull(eventObject.occuredOn());
			assertEquals("schuleAnlegen: Das Schulkürzel gibet schon.", eventObject.toString());
		}

	}

	@Test
	void should_throwExceptionAndNotSendMail_when_PersistenceException() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createPayloadForTest();
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

			Mockito.verify(mailDelegate, Mockito.times(0)).sendSchuleCreatedMailQuietly(schulePayload);
			DataInconsistencyRegistered eventObject = service.getRegisteredDataInconsistency();
			assertNull(eventObject);
		}

	}

}
