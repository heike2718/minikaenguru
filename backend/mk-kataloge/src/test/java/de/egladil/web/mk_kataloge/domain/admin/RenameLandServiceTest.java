// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.apimodel.LandPayload;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Land;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * RenameLandServiceTest
 */
public class RenameLandServiceTest {

	private SchuleRepository schuleRepository;

	private RenameLandService service;

	@BeforeEach
	void setUp() {

		schuleRepository = Mockito.mock(SchuleRepository.class);
		service = RenameLandService.createForTest(schuleRepository);
	}

	@Test
	void should_ThrowNotFoundException_when_NichtVorhanden() {

		// Arrange
		LandPayload landPayload = ChangeKatalogTestUtils.createLandPayloadForTest();

		Mockito.when(schuleRepository.loadLaender()).thenReturn(new ArrayList<>());

		// Act + Assert
		try {

			service.landUmbenennen(landPayload);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

			Response response = e.getResponse();
			assertEquals(404, response.getStatus());

			ResponsePayload responsePayload = (ResponsePayload) response.getEntity();
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals("Dieses Land gibt es nicht.", messagePayload.getMessage());
			assertEquals(landPayload, responsePayload.getData());

			Mockito.verify(schuleRepository, Mockito.times(0)).replaceSchulen(new ArrayList<>());
		}

	}

	@Test
	void should_NotRename_when_AnderesLandMitGleichemNamen() {

		// Arrange
		LandPayload landPayload = ChangeKatalogTestUtils.createLandPayloadForTest();
		Land dasLand = ChangeKatalogTestUtils.mapFromLandPayload(landPayload);

		Land anderesLand = ChangeKatalogTestUtils.mapFromLandPayload(landPayload);
		anderesLand.setKuerzel("HALLI-HALLO");

		List<Land> laender = new ArrayList<>();
		laender.add(dasLand);
		laender.add(anderesLand);

		Mockito.when(schuleRepository.loadLaender()).thenReturn(laender);

		// Act
		ResponsePayload responsePayload = service.landUmbenennen(landPayload);

		// Assert
		Mockito.verify(schuleRepository, Mockito.times(0)).replaceSchulen(new ArrayList<>());

		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals(
			"Umbenennung abgelehnt: Es gibt bereits ein anderes Land mit dem Namen Brasilien. Dieses wurde zurückgegeben.",
			messagePayload.getMessage());

		LandPayload result = (LandPayload) responsePayload.getData();
		assertEquals("HALLI-HALLO", result.kuerzel());

	}

	@Test
	void should_ChangeAllSchulen_when_UmbenennungErlaubt() {

		// Arrange
		LandPayload landPayload = ChangeKatalogTestUtils.createLandPayloadForTest();

		Land dasLand = ChangeKatalogTestUtils.mapFromLandPayload(landPayload);

		Land anderesLand = ChangeKatalogTestUtils.mapFromLandPayload(landPayload);
		anderesLand.setKuerzel("HALLI-HALLO");
		anderesLand.setName("Bayern");

		List<Land> laender = new ArrayList<>();
		laender.add(dasLand);
		laender.add(anderesLand);

		Mockito.when(schuleRepository.loadLaender()).thenReturn(laender);

		List<Schule> schulen = new ArrayList<Schule>();

		{

			Schule vorhandene = new Schule();
			vorhandene.setKuerzel("SCHULE-1");
			vorhandene.setName("Erste Schule");
			vorhandene.setOrtKuerzel("ORT-1");
			vorhandene.setOrtName("Erster Ort");
			vorhandene.setLandKuerzel(landPayload.kuerzel());
			vorhandene.setLandName("Alter Name");
			schulen.add(vorhandene);
		}

		{

			Schule vorhandene = new Schule();
			vorhandene.setKuerzel("SCHULE-2");
			vorhandene.setName("Zweite Schule");
			vorhandene.setOrtKuerzel("ORT-2");
			vorhandene.setOrtName("Zweiter Ort");
			vorhandene.setLandKuerzel(landPayload.kuerzel());
			vorhandene.setLandName("Alter Name");
			schulen.add(vorhandene);
		}

		assertFalse(schulen.get(0).equals(schulen.get(1)));

		Mockito.when(schuleRepository.findSchulenInLand(landPayload.kuerzel())).thenReturn(schulen);

		Mockito.when(schuleRepository.replaceSchulen(schulen)).thenReturn(Boolean.TRUE);

		// Act
		ResponsePayload responsePayload = service.landUmbenennen(landPayload);

		Mockito.verify(schuleRepository, Mockito.times(1)).replaceSchulen(schulen);

		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals(
			"Das Land wurde erfolgreich umbenannt. Anzahl geänderter Schulen: 2",
			messagePayload.getMessage());

		LandPayload result = (LandPayload) responsePayload.getData();
		assertEquals(result, landPayload);

		{

			Schule schule = schulen.get(0);
			assertEquals("BR", schule.getLandKuerzel());
			assertEquals("Brasilien", schule.getLandName());
			assertEquals("Erste Schule", schule.getName());
			assertEquals("SCHULE-1", schule.getKuerzel());
			assertEquals("Erster Ort", schule.getOrtName());
			assertEquals("ORT-1", schule.getOrtKuerzel());

		}

		{

			Schule schule = schulen.get(1);
			assertEquals("BR", schule.getLandKuerzel());
			assertEquals("Brasilien", schule.getLandName());
			assertEquals("Zweite Schule", schule.getName());
			assertEquals("SCHULE-2", schule.getKuerzel());
			assertEquals("Zweiter Ort", schule.getOrtName());
			assertEquals("ORT-2", schule.getOrtKuerzel());

		}
	}

	@Test
	void should_throwKatalogAPIException_when_UmbenennungErlaubtAberPersistenceException() {

		// Arrange
		LandPayload landPayload = ChangeKatalogTestUtils.createLandPayloadForTest();

		Land dasLand = ChangeKatalogTestUtils.mapFromLandPayload(landPayload);

		Land anderesLand = ChangeKatalogTestUtils.mapFromLandPayload(landPayload);
		anderesLand.setKuerzel("HALLI-HALLO");
		anderesLand.setName("Bayern");

		List<Land> laender = new ArrayList<>();
		laender.add(dasLand);
		laender.add(anderesLand);

		Mockito.when(schuleRepository.loadLaender()).thenReturn(laender);

		List<Schule> schulen = new ArrayList<Schule>();

		{

			Schule vorhandene = new Schule();
			vorhandene.setKuerzel("SCHULE-1");
			vorhandene.setName("Erste Schule");
			vorhandene.setOrtKuerzel("ORT-1");
			vorhandene.setOrtName("Erster Ort");
			vorhandene.setLandKuerzel(landPayload.kuerzel());
			vorhandene.setLandName("Alter Name");
			schulen.add(vorhandene);
		}

		{

			Schule vorhandene = new Schule();
			vorhandene.setKuerzel("SCHULE-2");
			vorhandene.setName("Zweite Schule");
			vorhandene.setOrtKuerzel("ORT-2");
			vorhandene.setOrtName("Zweiter Ort");
			vorhandene.setLandKuerzel(landPayload.kuerzel());
			vorhandene.setLandName("Alter Name");
			schulen.add(vorhandene);
		}

		assertFalse(schulen.get(0).equals(schulen.get(1)));

		Mockito.when(schuleRepository.findSchulenInLand(landPayload.kuerzel())).thenReturn(schulen);

		Mockito.when(schuleRepository.replaceSchulen(schulen))
			.thenThrow(new PersistenceException("DB-Fehler beim Speichern von Schulen"));

		// Act + Assert
		try {

			service.landUmbenennen(landPayload);

			fail("keine KatalogAPIException");
		} catch (KatalogAPIException e) {

			Mockito.verify(schuleRepository, Mockito.times(1)).replaceSchulen(schulen);
			assertEquals("Das Land konnte wegen eines Serverfehlers nicht umbenannt werden.", e.getMessage());
		}
	}

}
