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
import de.egladil.web.mk_kataloge.domain.apimodel.OrtPayload;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Ort;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * RenameOrtServiceTest
 */
public class RenameOrtServiceTest {

	private SchuleRepository schuleRepository;

	private RenameOrtService service;

	@BeforeEach
	void setUp() {

		schuleRepository = Mockito.mock(SchuleRepository.class);
		service = RenameOrtService.createForTest(schuleRepository);
	}

	@Test
	void should_ThrowNotFoundException_when_NichtVorhanden() {

		// Arrange
		OrtPayload ortPayload = ChangeKatalogTestUtils.createOrtPayloadForTest();

		Mockito.when(schuleRepository.getOrt(ortPayload.kuerzel())).thenReturn(Optional.empty());

		// Act + Assert
		try {

			service.ortUmbenennen(ortPayload);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

			assertEquals(404, e.getResponse().getStatus());

			ResponsePayload responsePayload = (ResponsePayload) e.getResponse().getEntity();
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals("Diesen Ort gibt es nicht.", messagePayload.getMessage());
			assertEquals(ortPayload, responsePayload.getData());
		}
	}

	@Test
	void should_throwWebApplicationException_when_LandkuerzelFalsch() {

		// Arrange
		OrtPayload ortPayload = ChangeKatalogTestUtils.createOrtPayloadForTest();

		Ort ort = ChangeKatalogTestUtils.mapFromOrtPayload(ortPayload);
		ort.setLandKuerzel("HALLI-HALLO");

		assertFalse(ortPayload.kuerzelLand().equals(ort.getLandKuerzel()));

		Mockito.when(schuleRepository.getOrt(ortPayload.kuerzel())).thenReturn(Optional.of(ort));

		// Act + Assert
		try {

			service.ortUmbenennen(ortPayload);
			fail("keine WebApplicationException");

		} catch (WebApplicationException e) {

			assertEquals(412, e.getResponse().getStatus());

			ResponsePayload responsePayload = (ResponsePayload) e.getResponse().getEntity();
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals("Umbenennung abgelehnt: Land passt nicht.", messagePayload.getMessage());
			assertNull(responsePayload.getData());
		}
	}

	@Test
	void should_notPersistOrt_when_AndererGleichenNamensVorhanden() {

		// Arrange
		OrtPayload ortPayload = ChangeKatalogTestUtils.createOrtPayloadForTest();

		Ort dieserOrt = ChangeKatalogTestUtils.mapFromOrtPayload(ortPayload);
		Ort andererOrt = ChangeKatalogTestUtils.mapFromOrtPayload(ortPayload);
		andererOrt.setKuerzel("HALLI-HALLO");

		List<Ort> trefferliste = new ArrayList<>();
		trefferliste.add(andererOrt);
		trefferliste.add(dieserOrt);

		Mockito.when(schuleRepository.getOrt(ortPayload.kuerzel())).thenReturn(Optional.of(dieserOrt));
		Mockito.when(schuleRepository.findOrteInLand(ortPayload.kuerzelLand())).thenReturn(trefferliste);

		List<Schule> schulen = new ArrayList<Schule>();
		schulen.add(ChangeKatalogTestUtils.mapFromSchulePayload(ChangeKatalogTestUtils.createSchulePayloadForTest()));

		Mockito.when(schuleRepository.findSchulenInOrt(ortPayload.kuerzel())).thenReturn(schulen);

		// Act
		ResponsePayload responsePayload = service.ortUmbenennen(ortPayload);

		// Assert
		Mockito.verify(schuleRepository, Mockito.times(0)).replaceSchulen(schulen);

		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals(
			"Umbenennung abgelehnt: Es gibt im gleichen Land bereits einen anderen Ort mit dem Namen Brasilia. Dieser wurde zurückgegeben.",
			messagePayload.getMessage());

		OrtPayload result = (OrtPayload) responsePayload.getData();
		assertEquals("HALLI-HALLO", result.kuerzel());

	}

	@Test
	void should_ChangeAllSchulen_when_UmbenennungGestattet() {

		// Arrange
		OrtPayload ortPayload = ChangeKatalogTestUtils.createOrtPayloadForTest();

		Ort dieserOrt = ChangeKatalogTestUtils.mapFromOrtPayload(ortPayload);

		List<Ort> trefferliste = new ArrayList<>();

		Mockito.when(schuleRepository.getOrt(ortPayload.kuerzel())).thenReturn(Optional.of(dieserOrt));
		Mockito.when(schuleRepository.findOrteInLand(ortPayload.kuerzelLand())).thenReturn(trefferliste);

		List<Schule> schulen = new ArrayList<Schule>();

		{

			Schule vorhandene = new Schule();
			vorhandene.setKuerzel("SCHULE-1");
			vorhandene.setName("Erste Schule");
			vorhandene.setOrtKuerzel("TFFFVHVH");
			vorhandene.setOrtName("Alter Name");
			vorhandene.setLandKuerzel("BR");
			vorhandene.setLandName("Brasilien");
			schulen.add(vorhandene);
		}

		{

			Schule vorhandene = new Schule();
			vorhandene.setKuerzel("SCHULE-2");
			vorhandene.setName("Zweite Schule");
			vorhandene.setOrtKuerzel("TFFFVHVH");
			vorhandene.setOrtName("Alter Name");
			vorhandene.setLandKuerzel("BR");
			vorhandene.setLandName("Brasilien");
			schulen.add(vorhandene);
		}

		assertFalse(schulen.get(0).equals(schulen.get(1)));

		Mockito.when(schuleRepository.findSchulenInOrt(ortPayload.kuerzel())).thenReturn(schulen);

		// Act
		ResponsePayload responsePayload = service.ortUmbenennen(ortPayload);

		// Assert
		Mockito.verify(schuleRepository, Mockito.times(1)).replaceSchulen(schulen);

		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals(
			"Der Ort wurde erfolgreich umbenannt. Anzahl geänderter Schulen: 2",
			messagePayload.getMessage());

		OrtPayload result = (OrtPayload) responsePayload.getData();
		assertEquals(result, ortPayload);

		{

			Schule schule = schulen.get(0);
			assertEquals("BR", schule.getLandKuerzel());
			assertEquals("Brasilien", schule.getLandName());
			assertEquals("Erste Schule", schule.getName());
			assertEquals("SCHULE-1", schule.getKuerzel());
			assertEquals("Brasilia", schule.getOrtName());
			assertEquals("TFFFVHVH", schule.getOrtKuerzel());

		}

		{

			Schule schule = schulen.get(1);
			assertEquals("BR", schule.getLandKuerzel());
			assertEquals("Brasilien", schule.getLandName());
			assertEquals("Zweite Schule", schule.getName());
			assertEquals("SCHULE-2", schule.getKuerzel());
			assertEquals("Brasilia", schule.getOrtName());
			assertEquals("TFFFVHVH", schule.getOrtKuerzel());

		}

	}

	@Test
	void should_throwKatalogAPIException_when_PersistenceException() {

		// Arrange
		OrtPayload ortPayload = ChangeKatalogTestUtils.createOrtPayloadForTest();

		Ort dieserOrt = ChangeKatalogTestUtils.mapFromOrtPayload(ortPayload);

		List<Ort> trefferliste = new ArrayList<>();

		Mockito.when(schuleRepository.getOrt(ortPayload.kuerzel())).thenReturn(Optional.of(dieserOrt));
		Mockito.when(schuleRepository.findOrteInLand(ortPayload.kuerzelLand())).thenReturn(trefferliste);

		List<Schule> schulen = new ArrayList<Schule>();

		{

			Schule vorhandene = new Schule();
			vorhandene.setKuerzel("SCHULE-1");
			vorhandene.setName("Erste Schule");
			vorhandene.setOrtKuerzel(ortPayload.kuerzel());
			vorhandene.setOrtName("Alter Name");
			vorhandene.setLandKuerzel(ortPayload.kuerzelLand());
			vorhandene.setLandName(ortPayload.nameLand());
			vorhandene.setLandName("Hessen");
			schulen.add(vorhandene);
		}

		{

			Schule vorhandene = new Schule();
			vorhandene.setKuerzel("SCHULE-2");
			vorhandene.setName("Zweite Schule");
			vorhandene.setOrtKuerzel(ortPayload.kuerzel());
			vorhandene.setOrtName("Alter Name");
			vorhandene.setLandKuerzel(ortPayload.kuerzelLand());
			vorhandene.setLandName(ortPayload.nameLand());
			vorhandene.setLandName("Hessen");
			schulen.add(vorhandene);
		}

		assertFalse(schulen.get(0).equals(schulen.get(1)));

		Mockito.when(schuleRepository.findSchulenInOrt(ortPayload.kuerzel())).thenReturn(schulen);

		Mockito.when(schuleRepository.replaceSchulen(schulen))
			.thenThrow(new PersistenceException("DB-Fehler beim Speichern von Schulen"));

		// Act + Assert
		try {

			service.ortUmbenennen(ortPayload);

			fail("keine KatalogAPIException");
		} catch (KatalogAPIException e) {

			Mockito.verify(schuleRepository, Mockito.times(1)).replaceSchulen(schulen);

			assertEquals("Der Ort konnte wegen eines Serverfehlers nicht umbenannt werden.", e.getMessage());
		}
	}
}
