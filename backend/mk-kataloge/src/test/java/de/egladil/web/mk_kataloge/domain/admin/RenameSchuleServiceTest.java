// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulePayload;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * RenameSchuleServiceTest
 */
public class RenameSchuleServiceTest {

	private SchuleRepository schuleRepository;

	private ChangeSchulenMailDelegate mailDelegate;

	private RenameSchuleService service;

	@BeforeEach
	void setUp() {

		mailDelegate = Mockito.mock(ChangeSchulenMailDelegate.class);
		schuleRepository = Mockito.mock(SchuleRepository.class);
		service = RenameSchuleService.createForTest(schuleRepository, mailDelegate);
	}

	@Test
	void should_ThrowNotFoundException_when_NichtVorhanden() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");

		Mockito.when(schuleRepository.getSchule(schulePayload.kuerzel()))
			.thenReturn(Optional.empty());

		// Act + Assert
		try {

			service.schuleUmbenennen(schulePayload);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

			assertEquals(404, e.getResponse().getStatus());

			ResponsePayload responsePayload = (ResponsePayload) e.getResponse().getEntity();
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals("Diese Schule gibt es nicht.", messagePayload.getMessage());
			assertEquals(schulePayload, responsePayload.getData());

			Mockito.verify(mailDelegate, Mockito.times(0)).sendSchuleCreatedMailQuietly(schulePayload);
		}

	}

	@Test
	void should_throwWebApplicationException_when_OrtkuerzelFalsch() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = ChangeKatalogTestUtils.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		schule.setOrtKuerzel("HALLI-HALLO");

		assertFalse(schulePayload.kuerzelOrt().equals(schule.getOrtKuerzel()));
		assertEquals(schulePayload.kuerzelLand(), schule.getLandKuerzel());

		Mockito.when(schuleRepository.getSchule(schulePayload.kuerzel()))
			.thenReturn(Optional.of(schule));

		// Act + Assert
		try {

			service.schuleUmbenennen(schulePayload);
			fail("keine WebApplicationException");

		} catch (WebApplicationException e) {

			assertEquals(412, e.getResponse().getStatus());

			ResponsePayload responsePayload = (ResponsePayload) e.getResponse().getEntity();
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals("Umbenennung abgelehnt: Ort oder Land passt nicht.", messagePayload.getMessage());
			assertNull(responsePayload.getData());

			Mockito.verify(mailDelegate, Mockito.times(0)).sendSchuleCreatedMailQuietly(schulePayload);
		}

	}

	@Test
	void should_throwWebApplicationException_when_LandkuerzelFalsch() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest();
		schulePayload = schulePayload.withEmailAuftraggeber("heike@web.de");
		Schule schule = ChangeKatalogTestUtils.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		schule.setLandKuerzel("HALLI-HALLO");

		assertFalse(schulePayload.kuerzelLand().equals(schule.getLandKuerzel()));
		assertEquals(schulePayload.kuerzelOrt(), schule.getOrtKuerzel());

		Mockito.when(schuleRepository.getSchule(schulePayload.kuerzel()))
			.thenReturn(Optional.of(schule));

		// Act + Assert
		try {

			service.schuleUmbenennen(schulePayload);
			fail("keine WebApplicationException");

		} catch (WebApplicationException e) {

			assertEquals(412, e.getResponse().getStatus());

			ResponsePayload responsePayload = (ResponsePayload) e.getResponse().getEntity();
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals("Umbenennung abgelehnt: Ort oder Land passt nicht.", messagePayload.getMessage());
			assertNull(responsePayload.getData());

			Mockito.verify(mailDelegate, Mockito.times(0)).sendSchuleCreatedMailQuietly(schulePayload);
		}

	}

	@Test
	void should_notPersistSchuleAndSendMail_when_AndereGleichenNamensVorhanden() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest().withEmailAuftraggeber("heike@web.de");

		Schule dieSchule = ChangeKatalogTestUtils.mapFromSchulePayload(schulePayload);
		dieSchule.setKuerzel(schulePayload.kuerzel());

		Schule andereSchule = ChangeKatalogTestUtils.mapFromSchulePayload(schulePayload);
		andereSchule.setKuerzel("GJLFUFUFU");

		Mockito.when(schuleRepository.getSchule(schulePayload.kuerzel())).thenReturn(Optional.of(dieSchule));

		List<Schule> trefferliste = new ArrayList<>();
		trefferliste.add(dieSchule);
		trefferliste.add(andereSchule);

		Mockito.when(schuleRepository.findSchulenInOrt(andereSchule.getOrtKuerzel()))
			.thenReturn(trefferliste);

		// Act
		ResponsePayload responsePayload = service.schuleUmbenennen(schulePayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals(
			"Umbenennung abgelehnt: Es gibt im gleichen Ort bereits eine andere Schule mit dem Namen Baumschule. Diese wurde zurückgegeben.",
			messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals("GJLFUFUFU", responseData.kuerzel());

		Mockito.verify(mailDelegate, Mockito.times(1)).sendSchuleCreatedMailQuietly(schulePayload);
	}

	@Test
	void should_notPersistSchuleAndNotSendMail_when_AndereGleichenNamensVorhanden() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest();

		Schule dieSchule = ChangeKatalogTestUtils.mapFromSchulePayload(schulePayload);
		dieSchule.setKuerzel(schulePayload.kuerzel());

		Schule andereSchule = ChangeKatalogTestUtils.mapFromSchulePayload(schulePayload);
		andereSchule.setKuerzel("GJLFUFUFU");

		Mockito.when(schuleRepository.getSchule(schulePayload.kuerzel())).thenReturn(Optional.of(dieSchule));

		List<Schule> trefferliste = new ArrayList<>();
		trefferliste.add(dieSchule);
		trefferliste.add(andereSchule);

		Mockito.when(schuleRepository.findSchulenInOrt(schulePayload.kuerzelOrt()))
			.thenReturn(trefferliste);

		// Act
		ResponsePayload responsePayload = service.schuleUmbenennen(schulePayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals(
			"Umbenennung abgelehnt: Es gibt im gleichen Ort bereits eine andere Schule mit dem Namen Baumschule. Diese wurde zurückgegeben.",
			messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals("GJLFUFUFU", responseData.kuerzel());

		Mockito.verify(mailDelegate, Mockito.times(0)).sendSchuleCreatedMailQuietly(schulePayload);
	}

	@Test
	void should_throwKatalogAPIExceptionAndNotSendMail_when_PersistenceException() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest().withEmailAuftraggeber("heike@web.de");
		Schule schule = ChangeKatalogTestUtils.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());

		Mockito.when(schuleRepository.getSchule(schulePayload.kuerzel())).thenReturn(Optional.of(schule));

		List<Schule> trefferliste = new ArrayList<>();
		trefferliste.add(schule);

		Mockito.when(schuleRepository.findSchulenInOrt(schulePayload.kuerzelOrt()))
			.thenReturn(trefferliste);

		Mockito.when(schuleRepository.replaceSchulen(Arrays.asList(new Schule[] { schule })))
			.thenThrow(new PersistenceException("DB-Exception beim Speichern."));

		// Act + Assert
		try {

			service.schuleUmbenennen(schulePayload);

			fail("keine KatalogAPIException");
		} catch (KatalogAPIException e) {

			assertEquals("Die Schule konnte wegen eines Serverfehlers nicht geändert werden.", e.getMessage());

			Mockito.verify(mailDelegate, Mockito.times(0)).sendSchuleCreatedMailQuietly(schulePayload);
		}

	}

	@Test
	void should_persistSchuleAndSendMail_when_VorhandenUndMailadresseVorhanden() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest().withEmailAuftraggeber("heike@web.de");

		Schule schule = ChangeKatalogTestUtils.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		schule.setName("Alter Name");

		assertFalse("Baumschule".equals(schule.getName()));

		Mockito.when(schuleRepository.getSchule(schulePayload.kuerzel())).thenReturn(Optional.of(schule));

		List<Schule> trefferliste = new ArrayList<>();
		trefferliste.add(schule);

		Mockito.when(schuleRepository.findSchulenInOrt(schulePayload.kuerzelOrt()))
			.thenReturn(trefferliste);

		Mockito.when(schuleRepository.replaceSchulen(Arrays.asList(new Schule[] { schule })))
			.thenReturn(Boolean.TRUE);

		// Act
		ResponsePayload responsePayload = service.schuleUmbenennen(schulePayload);

		// Assert

		assertEquals("Baumschule", schule.getName());

		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Die Schule wurde erfolgreich geändert.", messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals(schulePayload, responseData);

		Mockito.verify(mailDelegate, Mockito.times(1)).sendSchuleCreatedMailQuietly(schulePayload);
	}

	@Test
	void should_persistSchuleAndNotSendMail_when_VorhandenUndMailadresseNichtVorhanden() {

		// Arrange
		SchulePayload schulePayload = ChangeKatalogTestUtils.createSchulePayloadForTest();

		assertEquals("", schulePayload.emailAuftraggeber());

		Schule schule = ChangeKatalogTestUtils.mapFromSchulePayload(schulePayload);
		schule.setKuerzel(schulePayload.kuerzel());
		schule.setName("Alter Name");

		assertFalse("Baumschule".equals(schule.getName()));

		Mockito.when(schuleRepository.getSchule(schulePayload.kuerzel())).thenReturn(Optional.of(schule));

		List<Schule> trefferliste = new ArrayList<>();
		trefferliste.add(schule);

		Mockito.when(schuleRepository.findSchulenInOrt(schulePayload.kuerzelOrt()))
			.thenReturn(trefferliste);

		Mockito.when(schuleRepository.replaceSchulen(Arrays.asList(new Schule[] { schule })))
			.thenReturn(Boolean.TRUE);

		// Act
		ResponsePayload responsePayload = service.schuleUmbenennen(schulePayload);

		// Assert

		assertEquals("Baumschule", schule.getName());

		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals("Die Schule wurde erfolgreich geändert.", messagePayload.getMessage());

		SchulePayload responseData = (SchulePayload) responsePayload.getData();
		assertEquals(schulePayload, responseData);

		Mockito.verify(mailDelegate, Mockito.times(0)).sendSchuleCreatedMailQuietly(schulePayload);
	}

}
