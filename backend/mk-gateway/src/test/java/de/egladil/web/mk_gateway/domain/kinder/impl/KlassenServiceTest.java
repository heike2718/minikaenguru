// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.TestConstants;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenRepository;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseRequestData;
import de.egladil.web.mk_gateway.domain.kinder.events.KlasseChanged;
import de.egladil.web.mk_gateway.domain.kinder.events.KlasseCreated;
import de.egladil.web.mk_gateway.domain.kinder.events.KlasseDeleted;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.OnlineLoesungszettelService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.Lehrer;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.InMemoryKinderRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

/**
 * KlassenServiceTest
 */
@QuarkusTest
public class KlassenServiceTest {

	@InjectMock
	KlassenRepository klassenRepository;

	@InjectMock
	AuthorizationService authService;

	@InjectMock
	VeranstalterRepository veranstalterRepository;

	@InjectMock
	TeilnahmenRepository teilnahmenRepository;

	@InjectMock
	KinderServiceImpl kinderService;

	@InjectMock
	OnlineLoesungszettelService loesungszettelService;

	@InjectMock
	WettbewerbService wettbewerbService;

	@InjectMock
	DomainEventHandler domainEventHandler;

	@Inject
	KlassenServiceImpl klassenService;

	@BeforeEach
	public void setUp() {

		klassenService.resetWettbewerbIDForTest();
	}

	@Test
	void should_klassenZuSchuleLaden_callTheAuthorizationService() {

		// Arrange
		doThrow(new AccessDeniedException()).when(authService).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

		// Act
		try {

			klassenService.klassenZuSchuleLaden(TestConstants.SCHULKUERZEL_1, TestConstants.UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AccessDeniedException");

		} catch (AccessDeniedException e) {

		}
	}

	@Test
	void should_klassenZuSchuleLadenThrowNotFound_when_nichtAngemeldet() {

		// Arrange
		Veranstalter veranstalter = new Lehrer(new Person(TestConstants.UUID_LEHRER_1, TestConstants.NEWSLETTER_LEHRER_UUID), false,
			Collections.singletonList(new Identifier(TestConstants.SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		WettbewerbID wettbewerbID = new WettbewerbID(2020);
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
			.withTeilnahmenummer(TestConstants.SCHULKUERZEL_1).withWettbewerbID(wettbewerbID);

		when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.empty());
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(new Wettbewerb(wettbewerbID)));

		// Act
		try {

			klassenService.klassenZuSchuleLaden(TestConstants.SCHULKUERZEL_2, TestConstants.UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

		}
	}

	@Test
	void should_klassenZuSchuleLadenReturnTheKlassen_when_schuleAngemeldet() {

		// Arrange
		Veranstalter veranstalter = new Lehrer(new Person(TestConstants.UUID_LEHRER_1, TestConstants.NEWSLETTER_LEHRER_UUID), false,
			Collections.singletonList(new Identifier(TestConstants.SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		WettbewerbID wettbewerbID = new WettbewerbID(2020);
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
			.withTeilnahmenummer(TestConstants.SCHULKUERZEL_1).withWettbewerbID(wettbewerbID);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(new Wettbewerb(wettbewerbID)));

		Schulteilnahme teilnahme = new Schulteilnahme(wettbewerbID, new Identifier(TestConstants.SCHULKUERZEL_1), "Schule",
			new Identifier(TestConstants.UUID_LEHRER_1));

		when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(teilnahme));

		List<Klasse> klassenDB = new ArrayList<>();
		Klasse klasse = new Klasse(new Identifier(TestConstants.TEILNAHMENUMMER_PRIVAT)).withName("2a")
			.withSchuleID(new Identifier(TestConstants.SCHULKUERZEL_1));
		klassenDB.add(klasse);

		when(klassenRepository.findKlassenWithSchule(new Identifier(TestConstants.SCHULKUERZEL_1))).thenReturn(klassenDB);

		Map<Identifier, Pair<Long, Long>> anzahlenKinder = new HashMap<>();
		anzahlenKinder.put(new Identifier(TestConstants.TEILNAHMENUMMER_PRIVAT), Pair.of(12L, 1L));

		Map<Identifier, Long> anzahlenLoesungszettel = new HashMap<>();
		anzahlenLoesungszettel.put(new Identifier(TestConstants.TEILNAHMENUMMER_PRIVAT), 5L);

		when(kinderService.countKinder(any())).thenReturn(anzahlenKinder);
		when(kinderService.countLoesungszettel(any())).thenReturn(anzahlenLoesungszettel);

		// Act
		List<KlasseAPIModel> klassen = klassenService.klassenZuSchuleLaden(TestConstants.SCHULKUERZEL_1,
			TestConstants.UUID_LEHRER_1);

		// Assert
		assertEquals(1, klassen.size());
	}

	@Test
	void should_pruefeDuplikat_callTheAuthorizationService() {

		// Arrange

		doThrow(new AccessDeniedException()).when(authService).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(TestConstants.SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.pruefeDuplikat(data, TestConstants.UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AuthException");

		} catch (AccessDeniedException e) {

		}
	}

	@Test
	void should_pruefeDuplikatWithoutAuthorizationReturnFalse_when_KeineUeberschneidungWegenUUID() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A")
			.withSchulkuerzel(TestConstants.SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		List<Klasse> klassen = new ArrayList<>();
		Klasse klasse = new Klasse(new Identifier("SCHULKUERZEL_1_KLASSE_2A")).withName("2a")
			.withSchuleID(new Identifier(TestConstants.SCHULKUERZEL_1));
		klassen.add(klasse);

		when(klassenRepository.findKlassenWithSchule(new Identifier(TestConstants.SCHULKUERZEL_1))).thenReturn(klassen);

		// Act
		boolean result = klassenService.pruefeDuplikatWithoutAuthorization(data);

		// Assert
		assertFalse(result);

	}

	@Test
	void should_pruefeDuplikatWithoutAuthorizationReturnFalse_when_KeineUeberschneidungWegenName() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(TestConstants.SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		List<Klasse> klassen = new ArrayList<>();
		Klasse klasse = new Klasse(new Identifier("hjlah")).withName("2a")
			.withSchuleID(new Identifier(TestConstants.SCHULKUERZEL_1));
		klassen.add(klasse);

		when(klassenRepository.findKlassenWithSchule(new Identifier(TestConstants.SCHULKUERZEL_1))).thenReturn(klassen);

		// Act
		boolean result = klassenService.pruefeDuplikatWithoutAuthorization(data);

		// Assert
		assertFalse(result);

	}

	@Test
	void should_pruefeDuplikatWithoutAuthorizationReturnTrue_when_UeberschneidungNeueKlasse() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(TestConstants.SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2b"));

		List<Klasse> klassen = new ArrayList<>();
		Klasse klasse = new Klasse(new Identifier("hjlah")).withName("2b")
			.withSchuleID(new Identifier(TestConstants.SCHULKUERZEL_1));
		klassen.add(klasse);

		when(klassenRepository.findKlassenWithSchule(new Identifier(TestConstants.SCHULKUERZEL_1))).thenReturn(klassen);

		// Act
		boolean result = klassenService.pruefeDuplikatWithoutAuthorization(data);

		// Assert
		assertTrue(result);

	}

	@Test
	void should_pruefeDuplikatWithoutAuthorizationTrue_when_UeberschneidungUnzulaessigerNamenswechsel() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A")
			.withSchulkuerzel(TestConstants.SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2B"));

		List<Klasse> klassen = new ArrayList<>();
		Klasse klasse = new Klasse(new Identifier("hjlah")).withName("2b")
			.withSchuleID(new Identifier(TestConstants.SCHULKUERZEL_1));
		klassen.add(klasse);

		when(klassenRepository.findKlassenWithSchule(new Identifier(TestConstants.SCHULKUERZEL_1))).thenReturn(klassen);

		// Act
		boolean result = klassenService.pruefeDuplikatWithoutAuthorization(data);

		// Assert
		assertTrue(result);

	}

	@Test
	void should_klasseAnlegen_callTheAuthorizationService() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(TestConstants.SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any()))
			.thenThrow(new AccessDeniedException());

		// Act
		try {

			klassenService.klasseAnlegen(data, TestConstants.UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AuthException");

		} catch (AccessDeniedException e) {

			verify(authService).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

		}
	}

	@Test
	void should_klasseAnlegenThrowNotFound_when_SchuleNichtAngemeldet() {

		// Arrange
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(getAktuellenWettbewerb()));

		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(TestConstants.SCHULKUERZEL_2)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.klasseAnlegen(data, TestConstants.UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

			assertTrue(e.getMessage().endsWith(
				"klasseAnlegen(...): Schule mit UUID=SCHULKUERZEL_2 ist nicht zum aktuellen Wettbewerb (2020) angemeldet"));
		}
	}

	@Test
	void should_klasseAnlegen_triggerEvent() {

		// Arrange
		Veranstalter veranstalter = new Lehrer(new Person(TestConstants.UUID_LEHRER_1, TestConstants.NEWSLETTER_LEHRER_UUID), false,
			Collections.singletonList(new Identifier(TestConstants.SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		Wettbewerb aktueller = getAktuellenWettbewerb();
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
			.withTeilnahmenummer(TestConstants.SCHULKUERZEL_1).withWettbewerbID(aktueller.id());

		Schulteilnahme teilnahme = new Schulteilnahme(aktueller.id(), new Identifier(TestConstants.SCHULKUERZEL_1), "Schule",
			new Identifier(TestConstants.UUID_LEHRER_1));

		when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(teilnahme));
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktueller));

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(TestConstants.SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		Klasse neueKlasse = new Klasse(new Identifier("hjshdkqh")).withName("2c")
			.withSchuleID(new Identifier(TestConstants.SCHULKUERZEL_1));

		when(klassenRepository.addKlasse(any())).thenReturn(neueKlasse);

		// Act
		KlasseAPIModel klasse = klassenService.klasseAnlegen(data, TestConstants.UUID_LEHRER_1);

		// Assert
		assertEquals("2c", klasse.name());
		assertNotNull(klasse.uuid());

		verify(domainEventHandler).handleEvent(any(KlasseCreated.class));
	}

	@Test
	void should_klasseUmbenennen_callTheAuthorizationService() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A")
			.withSchulkuerzel(TestConstants.SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any()))
			.thenThrow(new AccessDeniedException());

		// Act
		try {

			klassenService.klasseUmbenennen(data, TestConstants.UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AuthException");

		} catch (AccessDeniedException e) {

			verify(authService).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());
		}
	}

	@Test
	void should_klasseUmbenennenThrowNotFound_when_SchuleNichtAngemeldet() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A")
			.withSchulkuerzel(TestConstants.SCHULKUERZEL_2)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		Veranstalter veranstalter = new Lehrer(new Person(TestConstants.UUID_LEHRER_1, TestConstants.NEWSLETTER_LEHRER_UUID), false,
			Collections.singletonList(new Identifier(TestConstants.SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		WettbewerbID wettbewerbID = new WettbewerbID(2020);
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
			.withTeilnahmenummer(TestConstants.SCHULKUERZEL_1).withWettbewerbID(wettbewerbID);

		when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.empty());
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(new Wettbewerb(wettbewerbID)));

		// Act
		try {

			klassenService.klasseUmbenennen(data, TestConstants.UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

			System.out.println(e.getMessage());

			assertTrue(e.getMessage().endsWith(
				".klasseUmbenennen(...): Schule mit UUID=SCHULKUERZEL_2 ist nicht zum aktuellen Wettbewerb (2020) angemeldet"));

		}
	}

	@Test
	void should_klasseUmbenennenThrowNotFound_when_KlasseNichtVorhanden() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2C")
			.withSchulkuerzel(TestConstants.SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		Veranstalter veranstalter = new Lehrer(new Person(TestConstants.UUID_LEHRER_1, TestConstants.NEWSLETTER_LEHRER_UUID), false,
			Collections.singletonList(new Identifier(TestConstants.SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(TestConstants.UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		WettbewerbID wettbewerbID = new WettbewerbID(2020);
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
			.withTeilnahmenummer(TestConstants.SCHULKUERZEL_1).withWettbewerbID(wettbewerbID);

		Schulteilnahme schulteilnahme = new Schulteilnahme(wettbewerbID, new Identifier(TestConstants.SCHULKUERZEL_1), "Baumschule",
			new Identifier(TestConstants.UUID_LEHRER_1));

		when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(schulteilnahme));
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(new Wettbewerb(wettbewerbID)));

		// Act
		try {

			klassenService.klasseUmbenennen(data, TestConstants.UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

		}
	}

	@Test
	void should_klasseUmbenennen_triggerEvent() {

		// Arrange
		String expectedName = "2a - Füchse";

		Klasse geaendert = new Klasse(new Identifier("SCHULKUERZEL_1_KLASSE_2A")).withName(expectedName)
			.withSchuleID(new Identifier(TestConstants.SCHULKUERZEL_1));

		Wettbewerb aktueller = getAktuellenWettbewerb();
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktueller));

		Teilnahme teilnahme = new Schulteilnahme(aktueller.id(), new Identifier(TestConstants.SCHULKUERZEL_1), "Baumschule",
			new Identifier(TestConstants.UUID_LEHRER_1));

		when(teilnahmenRepository.ofTeilnahmeIdentifier(any(TeilnahmeIdentifier.class))).thenReturn(Optional.of(teilnahme));

		when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);

		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A")
			.withSchulkuerzel(TestConstants.SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName(expectedName));

		List<Klasse> klassen = new ArrayList<>();
		Klasse klasseDB = new Klasse(new Identifier("SCHULKUERZEL_1_KLASSE_2A")).withName("2a")
			.withSchuleID(new Identifier(TestConstants.SCHULKUERZEL_1));
		klassen.add(klasseDB);

		when(klassenRepository.ofIdentifier(klasseDB.identifier())).thenReturn(Optional.of(klasseDB));

		when(klassenRepository.findKlassenWithSchule(new Identifier(TestConstants.SCHULKUERZEL_1))).thenReturn(klassen);

		when(klassenRepository.changeKlasse(klasseDB)).thenReturn(geaendert);

		Map<Identifier, Pair<Long, Long>> anzahlenKinder = new HashMap<>();
		anzahlenKinder.put(klasseDB.identifier(), Pair.of(5L, 4L));
		when(kinderService.countKinder(anyList())).thenReturn(anzahlenKinder);

		// Act
		klassenService.klasseUmbenennen(data, TestConstants.UUID_LEHRER_1);

		// Assert
		verify(authService).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());
		verify(wettbewerbService).aktuellerWettbewerb();
		verify(klassenRepository).changeKlasse(any(Klasse.class));
		verify(domainEventHandler).handleEvent(any(KlasseChanged.class));
		verify(kinderService).countKinder(anyList());

	}

	////
	@Test
	void should_klasseLoeschen_callTheAuthorizationService() {

		// Arrange
		Identifier klasseID = new Identifier("KLASSE-ID");

		Klasse klasse = new Klasse(klasseID).withSchuleID(new Identifier(TestConstants.SCHULKUERZEL_1));
		when(klassenRepository.ofIdentifier(any())).thenReturn(Optional.of(klasse));

		when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any()))
			.thenThrow(new AccessDeniedException());

		// Act
		try {

			klassenService.klasseLoeschen("SCHULKUERZEL_1_KLASSE_2A", TestConstants.UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AuthException");

		} catch (AccessDeniedException e) {

			verify(authService).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());
		}
	}

	@Test
	void should_klasseLoeschenThrowNotFound_when_schuleNichtAngemeldet() {

		// Arrange
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(getAktuellenWettbewerb()));

		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A")
			.withSchulkuerzel(TestConstants.SCHULKUERZEL_2)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.klasseUmbenennen(data, TestConstants.UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

		}
	}

	@Test
	void should_klasseLoeschenThrowNotFound_when_klasseNichtVorhanden() {

		// Act
		try {

			klassenService.klasseLoeschen("EXISTIERT-NICHT", TestConstants.UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

			verify(domainEventHandler, never()).handleEvent(any(KlasseDeleted.class));

		}
	}

	@Test
	void should_klasseLoeschen_triggerEvent() {

		// Arrange
		String klasseUUID = "SCHULKUERZEL_1_KLASSE_2A";
		Identifier klasseID = new Identifier(klasseUUID);
		TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifierAktuellerWettbewerb = new TeilnahmeIdentifierAktuellerWettbewerb(
			TestConstants.SCHULKUERZEL_1, Teilnahmeart.SCHULE);

		List<Kind> kinderMitKlasse = new InMemoryKinderRepository()
			.withTeilnahme(teilnahmeIdentifierAktuellerWettbewerb).stream()
			.filter(k -> klasseID.equals(k.klasseID())).collect(Collectors.toList());

		Wettbewerb aktueller = getAktuellenWettbewerb();
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktueller));

		Teilnahme teilnahme = new Schulteilnahme(aktueller.id(), new Identifier(TestConstants.SCHULKUERZEL_1), "Baumschule",
			new Identifier(TestConstants.UUID_LEHRER_1));

		when(teilnahmenRepository.ofTeilnahmeIdentifier(any(TeilnahmeIdentifier.class))).thenReturn(Optional.of(teilnahme));

		when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);

		Klasse klasse = new Klasse(klasseID).withSchuleID(new Identifier(TestConstants.SCHULKUERZEL_1));
		when(klassenRepository.ofIdentifier(klasseID)).thenReturn(Optional.of(klasse));

		when(kinderService.findKinderMitKlasseWithoutAuthorization(klasseID, teilnahmeIdentifierAktuellerWettbewerb))
			.thenReturn(kinderMitKlasse);

		when(klassenRepository.removeKlasse(any())).thenReturn(Boolean.TRUE);

		// Act
		KlasseAPIModel result = klassenService.klasseLoeschen(klasseUUID, TestConstants.UUID_LEHRER_1);

		// Assert
		assertEquals(klasseUUID, result.uuid());

		verify(authService).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());
		verify(teilnahmenRepository).ofTeilnahmeIdentifier(any(TeilnahmeIdentifier.class));

		verify(domainEventHandler).handleEvent(any(KlasseDeleted.class));
		verify(kinderService, times(kinderMitKlasse.size())).kindLoeschenWithoutAuthorizationCheck(any(), any());
		verify(domainEventHandler).handleEvent(any(KlasseDeleted.class));
	}

	Wettbewerb getAktuellenWettbewerb() {

		Wettbewerb aktueller = new Wettbewerb(new WettbewerbID(2020)).withStatus(WettbewerbStatus.ANMELDUNG);
		return aktueller;
	}

}
