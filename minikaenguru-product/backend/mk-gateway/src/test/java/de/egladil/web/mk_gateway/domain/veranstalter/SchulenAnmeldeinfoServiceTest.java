// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.statistik.AuswertungsmodusInfoService;
import de.egladil.web.mk_gateway.domain.teilnahmen.AktuelleTeilnahmeService;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchuleDetailsService;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchulenOverviewService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.veranstalter.api.Auswertungsmodus;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleDetails;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.dao.KatalogeRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Schule;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * SchulenAnmeldeinfoServiceTest
 */
@QuarkusTest
public class SchulenAnmeldeinfoServiceTest {

	private static final String LEHRER_UUID = "jahflhwl";

	@InjectMock
	AuthorizationService authorizationService;

	@InjectMock
	SchulenOverviewService schulenOverviewService;

	@InjectMock
	SchuleDetailsService schuleDetailsService;

	@InjectMock
	AuswertungsmodusInfoService auswertungsmodusInfoService;

	@InjectMock
	AktuelleTeilnahmeService aktuelleTeilnahmeService;

	@InjectMock
	KatalogeRepository katalogeRepository;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@Inject
	SchulenAnmeldeinfoService service;

	@Test
	void should_FindSchulenMitAnmeldeinfo_work() {

		// Arrange
		List<SchuleAPIModel> schulenWettbewerb = new ArrayList<>();
		schulenWettbewerb.add(new SchuleAPIModel().withKuerzel("12345").withAktuellAngemeldet(true));
		schulenWettbewerb.add(new SchuleAPIModel().withKuerzel("98765").withAktuellAngemeldet(false));

		when(schulenOverviewService.ermittleAnmeldedatenFuerSchulen(new Identifier(LEHRER_UUID))).thenReturn(schulenWettbewerb);

		List<Schule> schulenAusKatalog = new ArrayList<>();

		{
			Schule schule = new Schule();
			schule.setKuerzel("12345");
			schule.setLandKuerzel("DE-HE");
			schule.setLandName("Hessen");
			schule.setOrtName("Darmstadt");
			schule.setName("Schule 12345");

			schulenAusKatalog.add(schule);
		}

		{
			Schule schule = new Schule();
			schule.setKuerzel("98765");
			schule.setLandKuerzel("DE-HE");
			schule.setLandName("Hessen");
			schule.setOrtName("Darmstadt");
			schule.setName("Schule 98765");

			schulenAusKatalog.add(schule);
		}

		when(katalogeRepository.findSchulenWithKuerzeln(anyList())).thenReturn(schulenAusKatalog);

		// Act
		List<SchuleAPIModel> result = service.findSchulenMitAnmeldeinfo(LEHRER_UUID);

		assertEquals(2, result.size());

		{

			SchuleAPIModel schule = result.get(0);
			assertAll(() -> assertEquals("12345", schule.kuerzel()), () -> assertEquals("Schule 12345", schule.name()),
				() -> assertEquals("Darmstadt", schule.ort()), () -> assertEquals("Hessen", schule.land()),
				() -> assertEquals("DE-HE", schule.kuerzelLand()), () -> assertEquals(true, schule.aktuellAngemeldet()));
		}

		{

			SchuleAPIModel schule = result.get(1);
			assertAll(() -> assertEquals("98765", schule.kuerzel()), () -> assertEquals("Schule 98765", schule.name()),
				() -> assertEquals("Darmstadt", schule.ort()), () -> assertEquals("Hessen", schule.land()),
				() -> assertEquals("DE-HE", schule.kuerzelLand()), () -> assertEquals(false, schule.aktuellAngemeldet()));
		}

	}

	@Test
	void should_mergeDataFromSchulenOfLehrer_work_when_passt() {

		List<SchuleAPIModel> schulenWettbewerb = new ArrayList<>();
		schulenWettbewerb.add(new SchuleAPIModel().withKuerzel("12345").withAktuellAngemeldet(true));
		schulenWettbewerb.add(new SchuleAPIModel().withKuerzel("98765").withAktuellAngemeldet(false));

		List<SchuleAPIModel> schulenKataloge = new ArrayList<>();
		schulenKataloge.add(new SchuleAPIModel().withKuerzel("12345").withName("Schule 12345").withOrt("Darmstadt")
			.withKuerzelLand("DE-HE").withLand("Hessen"));
		schulenKataloge.add(new SchuleAPIModel().withKuerzel("98765").withName("Schule 98765").withOrt("Darmstadt")
			.withKuerzelLand("DE-HE").withLand("Hessen"));

		// Act
		List<SchuleAPIModel> result = service.mergeDataFromSchulenOfLehrer(schulenKataloge, schulenWettbewerb);

		// Assert
		assertEquals(2, result.size());

		{

			SchuleAPIModel schule = result.get(0);
			assertAll(() -> assertEquals("12345", schule.kuerzel()), () -> assertEquals("Schule 12345", schule.name()),
				() -> assertEquals("Darmstadt", schule.ort()), () -> assertEquals("Hessen", schule.land()),
				() -> assertEquals("DE-HE", schule.kuerzelLand()), () -> assertEquals(true, schule.aktuellAngemeldet()));
		}

		{

			SchuleAPIModel schule = result.get(1);
			assertAll(() -> assertEquals("98765", schule.kuerzel()), () -> assertEquals("Schule 98765", schule.name()),
				() -> assertEquals("Darmstadt", schule.ort()), () -> assertEquals("Hessen", schule.land()),
				() -> assertEquals("DE-HE", schule.kuerzelLand()), () -> assertEquals(false, schule.aktuellAngemeldet()));
		}

	}

	@Test
	void should_mergeDataFromSchulenOfLehrer_work_when_LehrerWenigerSchulen() {

		List<SchuleAPIModel> schulenWettbewerb = new ArrayList<>();
		schulenWettbewerb.add(new SchuleAPIModel().withKuerzel("12345").withAktuellAngemeldet(true));

		List<SchuleAPIModel> schulenKataloge = new ArrayList<>();
		schulenKataloge.add(new SchuleAPIModel().withKuerzel("12345").withName("Schule 98765").withOrt("Darmstadt")
			.withKuerzelLand("DE-HE").withLand("Hessen"));
		schulenKataloge.add(new SchuleAPIModel().withKuerzel("98765").withName("Schule 98765").withOrt("Darmstadt")
			.withKuerzelLand("DE-HE").withLand("Hessen"));

		// Act
		List<SchuleAPIModel> result = service.mergeDataFromSchulenOfLehrer(schulenKataloge, schulenWettbewerb);

		// Assert
		assertEquals(1, result.size());
		SchuleAPIModel schule = result.get(0);
		assertAll(() -> assertEquals("12345", schule.kuerzel()), () -> assertEquals("Schule 98765", schule.name()),
			() -> assertEquals("Darmstadt", schule.ort()), () -> assertEquals("Hessen", schule.land()),
			() -> assertEquals("DE-HE", schule.kuerzelLand()), () -> assertEquals(true, schule.aktuellAngemeldet()));
	}

	@Test
	void should_mergeDataFromSchulenOfLehrer_work_when_LehrerMehrSchulen() {

		List<SchuleAPIModel> schulenWettbewerb = new ArrayList<>();

		schulenWettbewerb.add(new SchuleAPIModel().withKuerzel("12345").withAktuellAngemeldet(true));
		schulenWettbewerb.add(new SchuleAPIModel().withKuerzel("98765").withAktuellAngemeldet(false));

		List<SchuleAPIModel> schulenKataloge = new ArrayList<>();
		schulenKataloge.add(new SchuleAPIModel().withKuerzel("98765").withName("Schule 98765").withOrt("Darmstadt")
			.withKuerzelLand("DE-HE").withLand("Hessen"));

		// Act
		List<SchuleAPIModel> result = service.mergeDataFromSchulenOfLehrer(schulenKataloge, schulenWettbewerb);

		// Assert
		assertEquals(2, result.size());

		{

			SchuleAPIModel schule = result.get(0);

			assertAll(() -> assertEquals("98765", schule.kuerzel()), () -> assertEquals("Schule 98765", schule.name()),
				() -> assertEquals("Darmstadt", schule.ort()), () -> assertEquals("Hessen", schule.land()),
				() -> assertEquals("DE-HE", schule.kuerzelLand()), () -> assertEquals(false, schule.aktuellAngemeldet()));
		}

		{

			SchuleAPIModel schule = result.get(1);

			assertAll(() -> assertEquals("12345", schule.kuerzel()), () -> assertEquals("unbekannter Schulname", schule.name()),
				() -> assertEquals("unbekannter Ort", schule.ort()),
				() -> assertEquals("unbekanntes Land / Bundesland", schule.land()),
				() -> assertEquals(true, schule.aktuellAngemeldet()));
		}

		verify(eventDelegate).fireDataInconsistencyEvent(any(), any());

	}

	@Test
	void should_getSchuleWithWettbewerbsdetails_work() {

		// Arrange
		List<Kollege> kollegen = Arrays.asList(new Kollege[] { new Kollege("ajhdqh", "Herta Meier") });

		SchuleDetails schuleDetails = new SchuleDetails("12345").withAngemeldetDurch(new Kollege("ghagdqg", "John Doe"))
			.withAnzahlTeilnahmen(1).withKollegen(kollegen);

		Map<String, Object> schuleKatalogeMap = new HashMap<>();

		schuleKatalogeMap.put("kuerzel", "12345");
		schuleKatalogeMap.put("name", "Schule 12345");
		schuleKatalogeMap.put("ort", "Darmstadt");
		schuleKatalogeMap.put("land", "Hessen");

		Schule schuleDB = new Schule();
		schuleDB.setKuerzel("12345");
		schuleDB.setLandKuerzel("DE-HE");
		schuleDB.setLandName("Hessen");
		schuleDB.setOrtName("Darmstadt");
		schuleDB.setName("Schule 12345");

		when(schulenOverviewService.ermittleAnmeldedatenFuerSchulen(new Identifier(LEHRER_UUID))).thenReturn(
			Arrays.asList(new SchuleAPIModel[] { new SchuleAPIModel().withKuerzel("12345").withAktuellAngemeldet(true) }));
		when(schuleDetailsService.ermittleSchuldetails(new Identifier("12345"), new Identifier(LEHRER_UUID)))
			.thenReturn(schuleDetails);
		when(katalogeRepository.findSchuleWithKuerzel("12345")).thenReturn(Optional.of(schuleDB));
		when(auswertungsmodusInfoService.ermittleAuswertungsmodusFuerTeilnahme(any())).thenReturn(Auswertungsmodus.INDIFFERENT);

		when(aktuelleTeilnahmeService.aktuelleTeilnahme("12345")).thenReturn(Optional.of(
			new Schulteilnahme(new WettbewerbID(2020), new Identifier("12345"), "Irgendein Name", new Identifier(LEHRER_UUID))));

		// Act
		SchuleAPIModel schule = service.getSchuleWithWettbewerbsdetails("12345", LEHRER_UUID);

		// Assert
		assertTrue(schule.aktuellAngemeldet());
		assertEquals("12345", schule.kuerzel());
		assertEquals("Schule 12345", schule.name());
		assertEquals("Darmstadt", schule.ort());
		assertEquals("Hessen", schule.land());
		assertEquals(Auswertungsmodus.INDIFFERENT, schule.getAuswertungsmodus());

		SchuleDetails details = schule.details();
		assertEquals("John Doe", details.angemeldetDurch());
		assertEquals("Herta Meier", details.kollegen());
		assertEquals("12345", details.kuerzel());

	}

	@Test
	void should_getSchuleWithWettbewerbDetailsThrowAccessDeniedException_when_keineBerechtigung() {

		// Arrange
		String schulkuerzel = "bjkasgca";

		Identifier lehrerId = new Identifier(LEHRER_UUID);
		Identifier teilnahmeId = new Identifier(schulkuerzel);

		when(authorizationService.checkPermissionForTeilnahmenummerAndReturnRolle(lehrerId, teilnahmeId,
			"[getSchuleDetails - " + schulkuerzel + "]")).thenThrow(new AccessDeniedException());

		// Act
		try {

			service.getSchuleWithWettbewerbsdetails(schulkuerzel, LEHRER_UUID);
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			// das Event wird vom authorizationService erzeugt.
			verify(eventDelegate, never()).fireSecurityEvent(any(), any());
			verify(katalogeRepository, never()).findSchulenWithKuerzeln(anyList());
			verify(schuleDetailsService, never()).ermittleSchuldetails(teilnahmeId, lehrerId);
			verify(aktuelleTeilnahmeService, never()).aktuelleTeilnahme(schulkuerzel);
			verify(auswertungsmodusInfoService, never()).ermittleAuswertungsmodusFuerTeilnahme(any());
		}

	}

	@Test
	void should_getSchuleWithWettbewerbsdetailsReturnIncompleteObject_when_KatalogeintragFehlt() {

		// Arrange
		List<Kollege> kollegen = Arrays.asList(new Kollege[] { new Kollege("ajhdqh", "Herta Meier") });

		SchuleDetails schuleDetails = new SchuleDetails("12345").withAngemeldetDurch(new Kollege("ghagdqg", "John Doe"))
			.withAnzahlTeilnahmen(1).withKollegen(kollegen).withNameUrkunde("Schule 12345");

		when(schulenOverviewService.ermittleAnmeldedatenFuerSchulen(new Identifier(LEHRER_UUID))).thenReturn(
			Arrays.asList(new SchuleAPIModel[] { new SchuleAPIModel().withKuerzel("12345").withAktuellAngemeldet(true) }));
		when(schuleDetailsService.ermittleSchuldetails(new Identifier("12345"), new Identifier(LEHRER_UUID)))
			.thenReturn(schuleDetails);

		when(aktuelleTeilnahmeService.aktuelleTeilnahme("12345")).thenReturn(Optional.empty());

		Map<String, Object> schuleKatalogeMap = new HashMap<>();

		schuleKatalogeMap.put("kuerzel", "12345");
		schuleKatalogeMap.put("name", "Schule 12345");
		schuleKatalogeMap.put("ort", "Darmstadt");
		schuleKatalogeMap.put("land", "Hessen");

		when(katalogeRepository.findSchuleWithKuerzel(any(String.class))).thenReturn(Optional.empty());

		// Act
		SchuleAPIModel schule = service.getSchuleWithWettbewerbsdetails("12345", LEHRER_UUID);

		// Assert
		assertTrue(schule.aktuellAngemeldet());
		assertEquals("12345", schule.kuerzel());
		assertEquals("unbekannter Schulname", schule.name());
		assertEquals("unbekannter Ort", schule.ort());
		assertEquals("unbekanntes Land / Bundesland", schule.land());

		SchuleDetails details = schule.details();
		assertEquals("John Doe", details.angemeldetDurch());
		assertEquals("Herta Meier", details.kollegen());
		assertEquals("12345", details.kuerzel());
		assertEquals("Schule 12345", details.nameUrkunde());

	}

}
