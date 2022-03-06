// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertext;
import de.egladil.web.mk_gateway.domain.mustertexte.MustertexteRepository;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertextkategorie;
import de.egladil.web.mk_gateway.domain.mustertexte.api.MustertextAPIModel;
import de.egladil.web.mk_gateway.domain.mustertexte.events.MustertextSavedEvent;
import de.egladil.web.mk_gateway.domain.mustertexte.impl.MustertextServiceImpl;

/**
 * MustertextServiceImplTest
 */
@ExtendWith(MockitoExtension.class)
public class MustertextServiceImplTest {

	@Mock
	MustertexteRepository mustertexteRepository;

	@Mock
	DomainEventHandler domainEventHandler;

	@InjectMocks
	MustertextServiceImpl service;

	@Nested
	class LoadMustertexteTests {

		@Test
		void should_getMustertexteByKategorieReturnList_when_mustertexteArePresent() {

			// Arrange
			Mustertextkategorie kategorie = Mustertextkategorie.MAIL;

			List<Mustertext> trefferliste = new ArrayList<>();
			trefferliste.add(new Mustertext(new Identifier("id-1")).withKategorie(kategorie).withName("Browsercache"));
			trefferliste.add(new Mustertext(new Identifier("id-2")).withKategorie(kategorie).withName("Passwort vergessen"));

			when(mustertexteRepository.loadMustertexteByKategorie(kategorie)).thenReturn(trefferliste);

			// Act
			ResponsePayload result = service.getMustertexteByKategorie(kategorie);

			// Assert
			assertTrue(result.isOk());

			@SuppressWarnings("unchecked")
			List<MustertextAPIModel> liste = (List<MustertextAPIModel>) result.getData();

			assertEquals(2, liste.size());

			{

				MustertextAPIModel apiModel = liste.get(0);
				assertEquals("id-1", apiModel.getUuid());
				assertEquals(kategorie, apiModel.getKategorie());
				assertEquals("Browsercache", apiModel.getName());
				assertNull(apiModel.getText());
			}

			{

				MustertextAPIModel apiModel = liste.get(1);
				assertEquals("id-2", apiModel.getUuid());
				assertEquals(kategorie, apiModel.getKategorie());
				assertEquals("Passwort vergessen", apiModel.getName());
				assertNull(apiModel.getText());
			}

			verify(mustertexteRepository).loadMustertexteByKategorie(kategorie);

		}

		@Test
		void should_getMustertexteByKategorieReturnAnEmptyList_when_noMustertexteArePresent() {

			// Arrange
			Mustertextkategorie kategorie = Mustertextkategorie.MAIL;

			when(mustertexteRepository.loadMustertexteByKategorie(kategorie)).thenReturn(new ArrayList<>());

			// Act
			ResponsePayload result = service.getMustertexteByKategorie(kategorie);

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

			Mustertext mustertext = new Mustertext(new Identifier(MustertextAPIModel.KEINE_UUID))
				.withKategorie(Mustertextkategorie.MAIL).withName("Browsercache")
				.withText("Veraltete Version, bitte Browsercache leeren");

			MustertextAPIModel apiModel = new MustertextAPIModel();
			apiModel.setKategorie(mustertext.getKategorie());
			apiModel.setName(mustertext.getName());
			apiModel.setText(mustertext.getText());
			apiModel.setUuid(mustertext.getIdentifier().identifier());

			Mustertext result = new Mustertext(identifier)
				.withKategorie(Mustertextkategorie.MAIL).withName("Browsercache")
				.withText("Veraltete Version, bitte Browsercache leeren");

			when(mustertexteRepository.addOrUpdate(any(Mustertext.class)))
				.thenReturn(result);
			doNothing().when(domainEventHandler).handleEvent(any(MustertextSavedEvent.class));

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
		}
	}
}
