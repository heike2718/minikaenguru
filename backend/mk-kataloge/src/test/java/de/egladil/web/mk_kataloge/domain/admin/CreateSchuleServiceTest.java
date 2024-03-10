// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
import de.egladil.web.mk_kataloge.domain.error.DuplicateEntityException;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;

/**
 * CreateSchuleServiceTest
 */
@QuarkusTest
public class CreateSchuleServiceTest {

	@InjectMock
	SchuleRepository schuleRepository;

	@InjectMock
	ChangeSchulenMailDelegate mailDelegate;

	@Inject
	private CreateSchuleService service;

	@Test
	void should_mapSchulePayloadToSchule() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest();

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
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");

		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel("HALLI-HALLO");

		List<Schule> trefferliste = new ArrayList<>();
		trefferliste.add(schule);

		when(schuleRepository.findSchulenInOrt(any()))
			.thenReturn(trefferliste);

		when(mailDelegate.sendSchuleCreatedMailQuietly(any())).thenReturn(Boolean.TRUE);

		// Act
		ResponsePayload responsePayload = service.schuleAnlegen(schulePayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals("Diese Schule gibt es bereits.", messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals("HALLI-HALLO", responseData.kuerzel());

		verify(mailDelegate).sendSchuleCreatedMailQuietly(schulePayload);
	}

	@Test
	void should_notPersistSchuleAndNotSendMail_when_VorhandenAberKeineAuftraggeberMail() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest();
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel("HALLI-HALLO");

		List<Schule> trefferliste = new ArrayList<>();
		trefferliste.add(schule);

		when(schuleRepository.findSchulenInOrt(schulePayload.kuerzelOrt()))
			.thenReturn(trefferliste);

		// Act
		ResponsePayload responsePayload = service.schuleAnlegen(schulePayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals("Diese Schule gibt es bereits.", messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals("HALLI-HALLO", responseData.kuerzel());

		verify(mailDelegate, never()).sendSchuleCreatedMailQuietly(schulePayload);

	}

	@Test
	void should_persistSchuleAndSendMail_when_NichtVorhanden() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());

		when(schuleRepository.findSchulenInOrt(schulePayload.kuerzelOrt()))
			.thenReturn(new ArrayList<>());

		when(schuleRepository.addSchule(schule)).thenReturn(Boolean.TRUE);

		when(mailDelegate.sendSchuleCreatedMailQuietly(any())).thenReturn(Boolean.TRUE);

		// Act
		ResponsePayload responsePayload = service.schuleAnlegen(schulePayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Die Schule wurde erfolgreich angelegt.", messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals(schulePayload, responseData);

		verify(mailDelegate).sendSchuleCreatedMailQuietly(schulePayload);
		verify(schuleRepository).addSchule(any());
	}

	@Test
	void should_persistSchuleAndNotSendMail_when_NichtVorhandenUndKeineEmpfaengermail() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest();
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());

		when(schuleRepository.findSchulenInOrt(schulePayload.kuerzelOrt()))
			.thenReturn(new ArrayList<>());

		when(schuleRepository.addSchule(schule)).thenReturn(Boolean.TRUE);

		// Act
		ResponsePayload responsePayload = service.schuleAnlegen(schulePayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Die Schule wurde erfolgreich angelegt.", messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals(schulePayload, responseData);

		verify(mailDelegate, never()).sendSchuleCreatedMailQuietly(schulePayload);
		verify(schuleRepository).addSchule(any());
	}

	@Test
	void should_mailserverExceptionNotPreventOk_when_NichtVorhanden() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());

		when(schuleRepository.findSchulenInOrt(schulePayload.kuerzelOrt()))
			.thenReturn(new ArrayList<>());
		when(schuleRepository.addSchule(schule)).thenReturn(Boolean.TRUE);

		when(mailDelegate.sendSchuleCreatedMailQuietly(schulePayload))
			.thenReturn(Boolean.FALSE);

		// Act
		ResponsePayload responsePayload = service.schuleAnlegen(schulePayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Die Schule wurde erfolgreich angelegt.", messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals(schulePayload, responseData);

		verify(mailDelegate).sendSchuleCreatedMailQuietly(schulePayload);
		verify(schuleRepository).addSchule(any());
	}

	@Test
	void should_createDataInconsistencyEventAndNotSendMail_when_DuplicateEntryDetected() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());

		when(schuleRepository.findSchulenInOrt(schulePayload.kuerzelOrt()))
			.thenReturn(new ArrayList<>());

		when(schuleRepository.addSchule(any()))
			.thenThrow(new DuplicateEntityException("Das Schulkürzel gibet schon."));

		// Act + Assert
		try {

			service.schuleAnlegen(schulePayload);

			fail("keine KatalogAPIException");
		} catch (KatalogAPIException e) {

			assertEquals("schuleAnlegen: Das Schulkürzel gibet schon.", e.getMessage());

			verify(mailDelegate, never()).sendSchuleCreatedMailQuietly(any());
		}

	}

	@Test
	void should_throwExceptionAndNotSendMail_when_PersistenceException() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = service.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());

		when(schuleRepository.findSchulenInOrt(schulePayload.kuerzelOrt()))
			.thenReturn(new ArrayList<>());

		when(schuleRepository.addSchule(any()))
			.thenThrow(new PersistenceException("irgendwas in der DB ist falsch."));

		// Act + Assert
		try {

			service.schuleAnlegen(schulePayload);

			fail("keine KatalogAPIException");
		} catch (KatalogAPIException e) {

			assertEquals("Die Schule konnte wegen eines Serverfehlers nicht angelegt werden.", e.getMessage());

			verify(mailDelegate, never()).sendSchuleCreatedMailQuietly(any());
		}

	}

}
