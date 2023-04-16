// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mustertexte.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertext;
import de.egladil.web.mk_gateway.domain.mustertexte.MustertexteRepository;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertextkategorie;
import de.egladil.web.mk_gateway.domain.mustertexte.api.MustertextAPIModel;
import de.egladil.web.mk_gateway.domain.mustertexte.events.MustertextDeletedEvent;
import de.egladil.web.mk_gateway.domain.mustertexte.events.MustertextSavedEvent;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

/**
 * MustertextServiceImplTest
 */
@QuarkusTest
public class MustertextServiceImplTest {

	@InjectMock
	MustertexteRepository mustertexteRepository;

	@InjectMock
	DomainEventHandler domainEventHandler;

	@Inject
	MustertextServiceImpl service;

	@Nested
	class LoadMustertexteTests {

		@Test
		void should_loadMustertexteWork() {

			// Arrange
			Mustertextkategorie kategorieMail = Mustertextkategorie.MAIL;
			Mustertextkategorie kategorieNewletter = Mustertextkategorie.NEWSLETTER;

			List<Mustertext> trefferlisteMail = new ArrayList<>();
			trefferlisteMail.add(new Mustertext(new Identifier("id-1")).withKategorie(kategorieMail).withName("Browsercache"));
			trefferlisteMail
				.add(new Mustertext(new Identifier("id-2")).withKategorie(kategorieMail).withName("Passwort vergessen"));

			List<Mustertext> trefferlisteNewletter = new ArrayList<>();
			trefferlisteNewletter
				.add(new Mustertext(new Identifier("id-3")).withKategorie(kategorieNewletter).withName("Anmeldung gestartet"));
			trefferlisteNewletter.add(new Mustertext(new Identifier("id-4")).withKategorie(kategorieNewletter)
				.withName("Freischaltung Unterlagen Lehrer"));

			doReturn(trefferlisteMail).when(mustertexteRepository).loadMustertexteByKategorie(kategorieMail);
			doReturn(trefferlisteNewletter).when(mustertexteRepository).loadMustertexteByKategorie(kategorieNewletter);

			// Act
			ResponsePayload result = service.loadMustertexte();

			// Assert
			assertTrue(result.isOk());

			@SuppressWarnings("unchecked")
			List<MustertextAPIModel> liste = (List<MustertextAPIModel>) result.getData();

			assertEquals(4, liste.size());

			{

				MustertextAPIModel apiModel = liste.get(0);
				assertEquals("id-1", apiModel.getUuid());
				assertEquals(kategorieMail, apiModel.getKategorie());
				assertEquals("Browsercache", apiModel.getName());
				assertNull(apiModel.getText());
			}

			{

				MustertextAPIModel apiModel = liste.get(1);
				assertEquals("id-2", apiModel.getUuid());
				assertEquals(kategorieMail, apiModel.getKategorie());
				assertEquals("Passwort vergessen", apiModel.getName());
				assertNull(apiModel.getText());
			}

			{

				MustertextAPIModel apiModel = liste.get(2);
				assertEquals("id-3", apiModel.getUuid());
				assertEquals(kategorieNewletter, apiModel.getKategorie());
				assertEquals("Anmeldung gestartet", apiModel.getName());
				assertNull(apiModel.getText());
			}

			{

				MustertextAPIModel apiModel = liste.get(3);
				assertEquals("id-4", apiModel.getUuid());
				assertEquals(kategorieNewletter, apiModel.getKategorie());
				assertEquals("Freischaltung Unterlagen Lehrer", apiModel.getName());
				assertNull(apiModel.getText());
			}

			verify(mustertexteRepository).loadMustertexteByKategorie(kategorieMail);
			verify(mustertexteRepository).loadMustertexteByKategorie(kategorieNewletter);

		}

		@Test
		void should_loadMustertexteReturnAnEmptyList_when_noMustertexteArePresent() {

			// Arrange
			Mustertextkategorie kategorie = Mustertextkategorie.MAIL;

			when(mustertexteRepository.loadMustertexteByKategorie(kategorie)).thenReturn(new ArrayList<>());

			// Act
			ResponsePayload result = service.loadMustertexte();

			// Assert
			assertTrue(result.isOk());

			@SuppressWarnings("unchecked")
			List<MustertextAPIModel> liste = (List<MustertextAPIModel>) result.getData();

			assertEquals(0, liste.size());

			verify(mustertexteRepository).loadMustertexteByKategorie(kategorie);
		}

	}

	@Nested
	class LoadDetailsTests {

		@Test
		void should_loadDetailsThrowNotFoundException_when_ResourceNotPresent() {

			// Arrange
			Identifier identifier = new Identifier("id-1");

			when(mustertexteRepository.findMustertextByIdentifier(identifier)).thenReturn(Optional.empty());

			// Act
			try {

				service.loadDetails(identifier);
				fail();
			} catch (NotFoundException e) {

				verify(mustertexteRepository).findMustertextByIdentifier(identifier);
			}

		}

		@Test
		void should_loadDetailsLoadTheText_when_ResourcePresent() {

			// Arrange
			Identifier identifier = new Identifier("id-1");
			Mustertext expected = new Mustertext(identifier).withKategorie(Mustertextkategorie.MAIL).withName("Browsercache")
				.withText("Veraltete Version, bitte Browsercache leeren");

			when(mustertexteRepository.findMustertextByIdentifier(identifier)).thenReturn(Optional.of(expected));

			// Act
			ResponsePayload result = service.loadDetails(identifier);

			// Assert
			assertTrue(result.isOk());

			MustertextAPIModel apiModel = (MustertextAPIModel) result.getData();
			assertEquals("id-1", apiModel.getUuid());
			assertEquals(Mustertextkategorie.MAIL, apiModel.getKategorie());
			assertEquals("Browsercache", apiModel.getName());
			assertEquals("Veraltete Version, bitte Browsercache leeren", apiModel.getText());
		}

	}

	@Nested
	class InsertTests {

		@Test
		void should_mustertextSpeichernCreateNewEntryInDatabase_when_neuUndKeineConstraintViolation() {

			// Arrange
			Identifier identifier = new Identifier("id-1");
			String adminUuid = "admin-uuid";

			MustertextAPIModel apiModel = new MustertextAPIModel();
			apiModel.setKategorie(Mustertextkategorie.MAIL);
			apiModel.setName("Browsercache");
			apiModel.setText("Veraltete Version, bitte Browsercache leeren");
			apiModel.setUuid(MustertextAPIModel.KEINE_UUID);

			Mustertext result = new Mustertext(identifier)
				.withKategorie(Mustertextkategorie.MAIL).withName("Browsercache")
				.withText("Veraltete Version, bitte Browsercache leeren");

			when(mustertexteRepository.addOrUpdate(any(Mustertext.class)))
				.thenReturn(result);
			doNothing().when(domainEventHandler).handleEvent(any(MustertextSavedEvent.class));
			when(mustertexteRepository.loadMustertexteByKategorie(Mustertextkategorie.MAIL))
				.thenReturn(new ArrayList<>());

			// Act
			ResponsePayload responsePayload = service.mustertextSpeichern(apiModel, adminUuid);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals("Mustertext erfolgreich gespeichert", messagePayload.getMessage());

			MustertextAPIModel data = (MustertextAPIModel) responsePayload.getData();
			assertEquals(identifier.identifier(), data.getUuid());
			assertEquals(Mustertextkategorie.MAIL, data.getKategorie());
			assertEquals("Browsercache", data.getName());
			assertEquals("Veraltete Version, bitte Browsercache leeren", data.getText());

			verify(domainEventHandler).handleEvent(any(MustertextSavedEvent.class));
			verify(mustertexteRepository).addOrUpdate(any(Mustertext.class));
			verify(mustertexteRepository).loadMustertexteByKategorie(Mustertextkategorie.MAIL);
		}

		@Test
		void should_mustertextSpeichernReturnWarn_when_neuUndGleicherName() {

			// Arrange
			Identifier identifier = new Identifier("id-1");
			String adminUuid = "admin-uuid";

			Mustertext mustertext = new Mustertext(identifier)
				.withKategorie(Mustertextkategorie.MAIL).withName("Browsercache")
				.withText("Veraltete Version, bitte Browsercache leeren");

			MustertextAPIModel apiModel = new MustertextAPIModel();
			apiModel.setKategorie(mustertext.getKategorie());
			apiModel.setName(mustertext.getName());
			apiModel.setText(mustertext.getText());
			apiModel.setUuid(MustertextAPIModel.KEINE_UUID);

			when(mustertexteRepository.loadMustertexteByKategorie(Mustertextkategorie.MAIL))
				.thenReturn(Collections.singletonList(mustertext));

			// Act
			ResponsePayload responsePayload = service.mustertextSpeichern(apiModel, adminUuid);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals("Es gibt bereits einen Mustertext in der Kategorie MAIL mit dem Namen [Browsercache]",
				messagePayload.getMessage());

			assertNull(responsePayload.getData());

			verify(domainEventHandler, never()).handleEvent(any(MustertextSavedEvent.class));
			verify(mustertexteRepository, never()).addOrUpdate(any(Mustertext.class));
			verify(mustertexteRepository).loadMustertexteByKategorie(Mustertextkategorie.MAIL);
		}
	}

	@Nested
	class UpdateTests {

		@Test
		void shouldUpdateWork() {

			// Arrange
			Identifier identifier = new Identifier("id-1");
			String adminUuid = "admin-uuid";

			Mustertext mustertext = new Mustertext(identifier)
				.withKategorie(Mustertextkategorie.MAIL).withName("Browsercache")
				.withText("Veraltete Version, bitte Browsercache leeren");

			MustertextAPIModel apiModel = new MustertextAPIModel();
			apiModel.setKategorie(mustertext.getKategorie());
			apiModel.setName(mustertext.getName());
			apiModel.setText("Neuer Text zum Browsercache");
			apiModel.setUuid(identifier.identifier());

			Mustertext result = new Mustertext(identifier)
				.withKategorie(Mustertextkategorie.MAIL).withName("Browsercache")
				.withText("Neuer Text zum Browsercache");

			when(mustertexteRepository.addOrUpdate(any(Mustertext.class)))
				.thenReturn(result);
			doNothing().when(domainEventHandler).handleEvent(any(MustertextSavedEvent.class));
			when(mustertexteRepository.loadMustertexteByKategorie(Mustertextkategorie.MAIL))
				.thenReturn(Collections.singletonList(mustertext));

			// Act
			ResponsePayload responsePayload = service.mustertextSpeichern(apiModel, adminUuid);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals("Mustertext erfolgreich gespeichert", messagePayload.getMessage());

			MustertextAPIModel data = (MustertextAPIModel) responsePayload.getData();
			assertEquals(identifier.identifier(), data.getUuid());
			assertEquals(Mustertextkategorie.MAIL, data.getKategorie());
			assertEquals("Browsercache", data.getName());
			assertEquals("Neuer Text zum Browsercache", data.getText());

			verify(domainEventHandler).handleEvent(any(MustertextSavedEvent.class));
			verify(mustertexteRepository).addOrUpdate(any(Mustertext.class));
			verify(mustertexteRepository).loadMustertexteByKategorie(Mustertextkategorie.MAIL);

		}

	}

	@Nested
	class LoeschenTests {

		@Test
		void should_loeschenWork_when_MustertextVorhanden() {

			// Arrange
			Identifier identifier = new Identifier("id-1");
			String adminUuid = "admin-uuid";

			when(mustertexteRepository.deleteMustertext(identifier)).thenReturn(Boolean.TRUE);
			doNothing().when(domainEventHandler).handleEvent(any(MustertextDeletedEvent.class));

			// Act
			ResponsePayload responsePayload = service.mustertextLoeschen(identifier, adminUuid);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals("Mustertext mit UUID id-1 erfolgreich gelöscht", messagePayload.getMessage());

			verify(domainEventHandler).handleEvent(any(MustertextDeletedEvent.class));
			verify(mustertexteRepository).deleteMustertext(identifier);

		}

		@Test
		void should_loeschenWork_when_MustertextNichtVorhanden() {

			// Arrange
			Identifier identifier = new Identifier("id-1");
			String adminUuid = "admin-uuid";

			when(mustertexteRepository.deleteMustertext(identifier)).thenReturn(Boolean.FALSE);

			// Act
			ResponsePayload responsePayload = service.mustertextLoeschen(identifier, adminUuid);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals("Mehrmals löschen ist sinnlos. Es gibt den Mustertext mit UUID id-1 bereits nicht mehr.",
				messagePayload.getMessage());

			verify(domainEventHandler, never()).handleEvent(any(MustertextDeletedEvent.class));
			verify(mustertexteRepository).deleteMustertext(identifier);

		}

	}

	@Nested
	class DubletteTests {

		@Test
		void should_findByNameReturnEmptyOptional_when_keinMustertextMitGleichemNamenCaseInsensitive() {

			// Arrange
			when(mustertexteRepository.loadMustertexteByKategorie(Mustertextkategorie.MAIL))
				.thenReturn(new ArrayList<>());

			MustertextAPIModel apiModel = new MustertextAPIModel();
			apiModel.setKategorie(Mustertextkategorie.MAIL);
			apiModel.setName("Browsercache");
			apiModel.setText("Veraltete Version, bitte Browsercache leeren");
			apiModel.setUuid(MustertextAPIModel.KEINE_UUID);

			// Act
			Optional<Mustertext> opt = service.findByName(apiModel);

			// Assert
			assertTrue(opt.isEmpty());

			verify(mustertexteRepository).loadMustertexteByKategorie(Mustertextkategorie.MAIL);

		}

		@Test
		void should_findByNameReturnEmptyOptional_when_UuidApiModelGleichIdentifierMustertextAndApiModelNameEqualMustertextName() {

			// Arrange
			Identifier identifier = new Identifier("id-1");

			Mustertext mustertext = new Mustertext(identifier)
				.withKategorie(Mustertextkategorie.MAIL).withName("Browsercache")
				.withText("Veraltete Version, bitte Browsercache leeren");

			when(mustertexteRepository.loadMustertexteByKategorie(Mustertextkategorie.MAIL))
				.thenReturn(Collections.singletonList(mustertext));

			MustertextAPIModel apiModel = new MustertextAPIModel();
			apiModel.setKategorie(mustertext.getKategorie());
			apiModel.setName(mustertext.getName());
			apiModel.setText(mustertext.getText());
			apiModel.setUuid(identifier.identifier());

			// Act
			Optional<Mustertext> opt = service.findByName(apiModel);

			// Assert
			assertTrue(opt.isEmpty());

			verify(mustertexteRepository).loadMustertexteByKategorie(Mustertextkategorie.MAIL);

		}

		@Test
		void should_findByNameReturnOptional_when_UuidApiModelUngleichIdentifierMustertextAndApiModelNameEqualMustertextName() {

			// Arrange
			Identifier identifier = new Identifier("id-1");

			Mustertext mustertext = new Mustertext(identifier)
				.withKategorie(Mustertextkategorie.MAIL).withName("BrowserCache")
				.withText("Veraltete Version, bitte Browsercache leeren");

			MustertextAPIModel apiModel = new MustertextAPIModel();
			apiModel.setKategorie(mustertext.getKategorie());
			apiModel.setName(mustertext.getName());
			apiModel.setText(mustertext.getText());
			apiModel.setUuid(MustertextAPIModel.KEINE_UUID);

			when(mustertexteRepository.loadMustertexteByKategorie(Mustertextkategorie.MAIL))
				.thenReturn(Collections.singletonList(mustertext));

			assertFalse(identifier.identifier().equals(apiModel.getUuid()));
			assertTrue(apiModel.getName().equalsIgnoreCase(mustertext.getName()));

			// Act
			Optional<Mustertext> opt = service.findByName(apiModel);

			// Assert
			assertTrue(opt.isPresent());

			verify(mustertexteRepository).loadMustertexteByKategorie(Mustertextkategorie.MAIL);
		}

		@Test
		void should_findByNameReturnEmptyOptional_when_UuidApiModelGleichIdentifierMustertextAndApiModelNameDiffersFromMustertextName() {

			// Arrange
			Identifier identifier = new Identifier("id-1");

			Mustertext mustertext = new Mustertext(identifier)
				.withKategorie(Mustertextkategorie.MAIL).withName("BrowserCache")
				.withText("Veraltete Version, bitte Browsercache leeren");

			MustertextAPIModel apiModel = new MustertextAPIModel();
			apiModel.setKategorie(mustertext.getKategorie());
			apiModel.setName("Passwort vergessen");
			apiModel.setText(mustertext.getText());
			apiModel.setUuid(identifier.identifier());

			when(mustertexteRepository.loadMustertexteByKategorie(Mustertextkategorie.MAIL))
				.thenReturn(Collections.singletonList(mustertext));

			assertTrue(identifier.identifier().equals(apiModel.getUuid()));
			assertFalse(apiModel.getName().equalsIgnoreCase(mustertext.getName()));

			// Act
			Optional<Mustertext> opt = service.findByName(apiModel);

			// Assert
			assertTrue(opt.isEmpty());

			verify(mustertexteRepository).loadMustertexteByKategorie(Mustertextkategorie.MAIL);
		}

		@Test
		void should_findByNameReturnEmptyOptional_when_UuidApiModelGleichIdentifierMustertextAndApiModelNameEqualsMustertextName() {

			// Arrange
			Identifier identifier = new Identifier("id-1");

			Mustertext mustertext = new Mustertext(identifier)
				.withKategorie(Mustertextkategorie.MAIL).withName("BrowserCache")
				.withText("Veraltete Version, bitte Browsercache leeren");

			MustertextAPIModel apiModel = new MustertextAPIModel();
			apiModel.setKategorie(mustertext.getKategorie());
			apiModel.setName(mustertext.getName());
			apiModel.setText(mustertext.getText());
			apiModel.setUuid(identifier.identifier());

			when(mustertexteRepository.loadMustertexteByKategorie(Mustertextkategorie.MAIL))
				.thenReturn(Collections.singletonList(mustertext));

			assertTrue(identifier.identifier().equals(apiModel.getUuid()));
			assertTrue(apiModel.getName().equalsIgnoreCase(mustertext.getName()));

			// Act
			Optional<Mustertext> opt = service.findByName(apiModel);

			// Assert
			assertTrue(opt.isEmpty());

			verify(mustertexteRepository).loadMustertexteByKategorie(Mustertextkategorie.MAIL);
		}

		@Test
		void should_findByNameReturnEmptyOptional_when_bothUUIDAndNameDifferent() {

			// Arrange
			Identifier identifier = new Identifier("id-1");

			Mustertext mustertext = new Mustertext(identifier)
				.withKategorie(Mustertextkategorie.MAIL).withName("BrowserCache")
				.withText("Veraltete Version, bitte Browsercache leeren");

			MustertextAPIModel apiModel = new MustertextAPIModel();
			apiModel.setKategorie(mustertext.getKategorie());
			apiModel.setName("Passwort vergessen");
			apiModel.setText(mustertext.getText());
			apiModel.setUuid(MustertextAPIModel.KEINE_UUID);

			assertFalse(identifier.identifier().equals(apiModel.getUuid()));
			assertFalse(apiModel.getName().equalsIgnoreCase(mustertext.getName()));

			when(mustertexteRepository.loadMustertexteByKategorie(Mustertextkategorie.MAIL))
				.thenReturn(Collections.singletonList(mustertext));

			// Act
			Optional<Mustertext> opt = service.findByName(apiModel);

			// Assert
			assertTrue(opt.isEmpty());

			verify(mustertexteRepository).loadMustertexteByKategorie(Mustertextkategorie.MAIL);
		}
	}
}
