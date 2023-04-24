// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
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
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.OnlineLoesungszettelService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.veranstalter.Lehrer;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

/**
 * KlassenServiceTest
 */
@QuarkusTest
public class KlassenServiceTest extends AbstractDomainServiceTest {

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

	@Test
	void should_klassenZuSchuleLaden_callTheAuthorizationService() {

		// Arrange
		doThrow(new AccessDeniedException()).when(authService).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

		// Act
		try {

			klassenService.klassenZuSchuleLaden(SCHULKUERZEL_1, UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AccessDeniedException");

		} catch (AccessDeniedException e) {

		}
	}

	@Test
	void should_klassenZuSchuleLadenThrowNotFound_when_nichtAngemeldet() {

		// Arrange
		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_1, NEWSLETTER_LEHRER_UUID), false,
			Collections.singletonList(new Identifier(SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		WettbewerbID wettbewerbID = new WettbewerbID(2020);
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
			.withTeilnahmenummer(SCHULKUERZEL_1).withWettbewerbID(wettbewerbID);

		when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.empty());
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(new Wettbewerb(wettbewerbID)));

		// Act
		try {

			klassenService.klassenZuSchuleLaden(SCHULKUERZEL_2, UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

		}
	}

	@Test
	void should_klassenZuSchuleLadenReturnTheKlassen_when_schuleAngemeldet() {

		// Arrange
		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_1, NEWSLETTER_LEHRER_UUID), false,
			Collections.singletonList(new Identifier(SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		WettbewerbID wettbewerbID = new WettbewerbID(2020);
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
			.withTeilnahmenummer(SCHULKUERZEL_1).withWettbewerbID(wettbewerbID);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(new Wettbewerb(wettbewerbID)));

		Schulteilnahme teilnahme = new Schulteilnahme(wettbewerbID, new Identifier(SCHULKUERZEL_1), "Schule",
			new Identifier(UUID_LEHRER_1));

		when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(teilnahme));

		List<Klasse> klassenDB = new ArrayList<>();
		Klasse klasse = new Klasse(new Identifier(TEILNAHMENUMMER_PRIVAT)).withName("2a")
			.withSchuleID(new Identifier(SCHULKUERZEL_1));
		klassenDB.add(klasse);

		when(klassenRepository.findKlassenWithSchule(new Identifier(SCHULKUERZEL_1))).thenReturn(klassenDB);

		Map<Identifier, Pair<Long, Long>> anzahlenKinder = new HashMap<>();
		anzahlenKinder.put(new Identifier(TEILNAHMENUMMER_PRIVAT), Pair.of(12L, 1L));

		Map<Identifier, Long> anzahlenLoesungszettel = new HashMap<>();
		anzahlenLoesungszettel.put(new Identifier(TEILNAHMENUMMER_PRIVAT), 5L);

		when(kinderService.countKinder(any())).thenReturn(anzahlenKinder);
		when(kinderService.countLoesungszettel(any())).thenReturn(anzahlenLoesungszettel);

		// Act
		List<KlasseAPIModel> klassen = klassenService.klassenZuSchuleLaden(SCHULKUERZEL_1, UUID_LEHRER_1);

		// Assert
		assertEquals(1, klassen.size());
	}

	@Test
	void should_pruefeDuplikat_callTheAuthorizationService() {

		// Arrange
		doThrow(new AccessDeniedException()).when(authService).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.pruefeDuplikat(data, UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AuthException");

		} catch (AccessDeniedException e) {

		}
	}

	@Test
	void should_pruefeDuplikatWithoutAuthorizationReturnFalse_when_KeineUeberschneidungWegenUUID() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		List<Klasse> klassen = new ArrayList<>();
		Klasse klasse = new Klasse(new Identifier("SCHULKUERZEL_1_KLASSE_2A")).withName("2a")
			.withSchuleID(new Identifier(SCHULKUERZEL_1));
		klassen.add(klasse);

		when(klassenRepository.findKlassenWithSchule(new Identifier(SCHULKUERZEL_1))).thenReturn(klassen);

		// Act
		boolean result = klassenService.pruefeDuplikatWithoutAuthorization(data);

		// Assert
		assertFalse(result);

	}

	@Test
	void should_pruefeDuplikatWithoutAuthorizationReturnFalse_when_KeineUeberschneidungWegenName() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		List<Klasse> klassen = new ArrayList<>();
		Klasse klasse = new Klasse(new Identifier("hjlah")).withName("2a").withSchuleID(new Identifier(SCHULKUERZEL_1));
		klassen.add(klasse);

		when(klassenRepository.findKlassenWithSchule(new Identifier(SCHULKUERZEL_1))).thenReturn(klassen);

		// Act
		boolean result = klassenService.pruefeDuplikatWithoutAuthorization(data);

		// Assert
		assertFalse(result);

	}

	@Test
	void should_pruefeDuplikatWithoutAuthorizationReturnTrue_when_UeberschneidungNeueKlasse() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2b"));

		List<Klasse> klassen = new ArrayList<>();
		Klasse klasse = new Klasse(new Identifier("hjlah")).withName("2b").withSchuleID(new Identifier(SCHULKUERZEL_1));
		klassen.add(klasse);

		when(klassenRepository.findKlassenWithSchule(new Identifier(SCHULKUERZEL_1))).thenReturn(klassen);

		// Act
		boolean result = klassenService.pruefeDuplikatWithoutAuthorization(data);

		// Assert
		assertTrue(result);

	}

	@Test
	void should_pruefeDuplikatWithoutAuthorizationTrue_when_UeberschneidungUnzulaessigerNamenswechsel() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2B"));

		List<Klasse> klassen = new ArrayList<>();
		Klasse klasse = new Klasse(new Identifier("hjlah")).withName("2b").withSchuleID(new Identifier(SCHULKUERZEL_1));
		klassen.add(klasse);

		when(klassenRepository.findKlassenWithSchule(new Identifier(SCHULKUERZEL_1))).thenReturn(klassen);

		// Act
		boolean result = klassenService.pruefeDuplikatWithoutAuthorization(data);

		// Assert
		assertTrue(result);

	}

	@Test
	void should_klasseAnlegen_callTheAuthorizationService() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.klasseAnlegen(data, UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AuthException");

		} catch (AccessDeniedException e) {

		}
	}

	@Test
	void should_klasseAnlegenThrowNotFound_when_SchuleNichtAngemeldet() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(SCHULKUERZEL_2)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.klasseAnlegen(data, UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

			assertTrue(e.getMessage().endsWith(
				"klasseAnlegen(...): Schule mit UUID=SCHULKUERZEL_2 ist nicht zum aktuellen Wettbewerb (2020) angemeldet"));
		}
	}

	@Test
	void should_klasseAnlegen_triggerEvent() {

		// Arrange
		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_1, NEWSLETTER_LEHRER_UUID), false,
			Collections.singletonList(new Identifier(SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		WettbewerbID wettbewerbID = new WettbewerbID(2020);
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
			.withTeilnahmenummer(SCHULKUERZEL_1).withWettbewerbID(wettbewerbID);

		Schulteilnahme teilnahme = new Schulteilnahme(wettbewerbID, new Identifier(SCHULKUERZEL_1), "Schule",
			new Identifier(UUID_LEHRER_1));

		when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(teilnahme));
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(new Wettbewerb(wettbewerbID)));

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		Klasse neueKlasse = new Klasse(new Identifier("hjshdkqh")).withName("2c").withSchuleID(new Identifier(SCHULKUERZEL_1));

		when(klassenRepository.addKlasse(any())).thenReturn(neueKlasse);

		// Act
		KlasseAPIModel klasse = klassenService.klasseAnlegen(data, UUID_LEHRER_1);

		// Assert
		assertEquals("2c", klasse.name());
		assertNotNull(klasse.uuid());

		verify(domainEventHandler).handleEvent(any(KlasseCreated.class));
	}

	@Test
	void should_klasseUmbenennen_callTheAuthorizationService() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.klasseUmbenennen(data, UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AuthException");

		} catch (AccessDeniedException e) {

		}
	}

	@Test
	void should_klasseUmbenennenThrowNotFound_when_SchuleNichtAngemeldet() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A").withSchulkuerzel(SCHULKUERZEL_2)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_1, NEWSLETTER_LEHRER_UUID), false,
			Collections.singletonList(new Identifier(SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		WettbewerbID wettbewerbID = new WettbewerbID(2020);
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
			.withTeilnahmenummer(SCHULKUERZEL_1).withWettbewerbID(wettbewerbID);

		when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.empty());
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(new Wettbewerb(wettbewerbID)));

		// Act
		try {

			klassenService.klasseUmbenennen(data, UUID_LEHRER_1);
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
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2C").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		Veranstalter veranstalter = new Lehrer(new Person(UUID_LEHRER_1, NEWSLETTER_LEHRER_UUID), false,
			Collections.singletonList(new Identifier(SCHULKUERZEL_1)));

		when(veranstalterRepository.ofId(new Identifier(UUID_LEHRER_1))).thenReturn(Optional.of(veranstalter));

		WettbewerbID wettbewerbID = new WettbewerbID(2020);
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
			.withTeilnahmenummer(SCHULKUERZEL_1).withWettbewerbID(wettbewerbID);

		Schulteilnahme schulteilnahme = new Schulteilnahme(wettbewerbID, new Identifier(SCHULKUERZEL_1), "Baumschule",
			new Identifier(UUID_LEHRER_1));

		when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(schulteilnahme));
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(new Wettbewerb(wettbewerbID)));

		// Act
		try {

			klassenService.klasseUmbenennen(data, UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

		}
	}

	@Test
	void should_klasseUmbenennen_triggerEvent() {

		// Arrange
		String expectedName = "2a - Füchse";

		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName(expectedName));

		List<Klasse> klassen = new ArrayList<>();
		Klasse klasseDB = new Klasse(new Identifier("SCHULKUERZEL_1_KLASSE_2A")).withName("2a")
			.withSchuleID(new Identifier(SCHULKUERZEL_1));
		klassen.add(klasseDB);

		when(klassenRepository.findKlassenWithSchule(new Identifier(SCHULKUERZEL_1))).thenReturn(klassen);

		// Act
		klassenService.klasseUmbenennen(data, UUID_LEHRER_1);

		// Assert
		verify(klassenRepository.changeKlasse(any(Klasse.class)));
		verify(domainEventHandler).handleEvent(any(KlasseChanged.class));

	}

	////
	@Test
	void should_klasseLoeschen_callTheAuthorizationService() {

		// Act
		try {

			klassenService.klasseLoeschen("SCHULKUERZEL_1_KLASSE_2A", UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AuthException");

		} catch (AccessDeniedException e) {

		}
	}

	@Test
	void should_klasseLoeschenThrowNotFound_when_schuleNichtAngemeldet() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A").withSchulkuerzel(SCHULKUERZEL_2)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.klasseUmbenennen(data, UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

		}
	}

	@Test
	void should_klasseLoeschenThrowNotFound_when_klasseNichtVorhanden() {

		// Act
		try {

			klassenService.klasseLoeschen("EXISTIERT-NICHT", UUID_LEHRER_1);
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
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer(SCHULKUERZEL_1).withWettbewerbID(new WettbewerbID(WETTBEWERBSJAHR_AKTUELL));

		List<Kind> kinderMitKlasse = getKinderRepository()
			.withTeilnahme(new TeilnahmeIdentifierAktuellerWettbewerb(SCHULKUERZEL_1, Teilnahmeart.SCHULE)).stream()
			.filter(k -> klasseID.equals(k.klasseID())).collect(Collectors.toList());

		final List<Loesungszettel> alleLoesungszettel = getLoesungszettelRepository().loadAll(teilnahmeIdentifier);

		final List<Loesungszettel> zuLoeschendeLoesungszettel = new ArrayList<>();

		for (Kind kind : kinderMitKlasse) {

			Optional<Loesungszettel> optLZ = alleLoesungszettel.stream()
				.filter(lz -> kind.loesungszettelID().equals(lz.identifier())).findAny();

			if (optLZ.isPresent()) {

				zuLoeschendeLoesungszettel.add(optLZ.get());
			}
		}

		// Act
		KlasseAPIModel result = klassenService.klasseLoeschen(klasseUUID, UUID_LEHRER_1);

		assertEquals(klasseUUID, result.uuid());

		List<Kind> kinderHinterher = getKinderRepository()
			.withTeilnahme(new TeilnahmeIdentifierAktuellerWettbewerb(SCHULKUERZEL_1, Teilnahmeart.SCHULE));

		for (Kind kind : kinderMitKlasse) {

			assertNull(kind.loesungszettelID(), "Fehler bei Kind " + kind + ": Lösungszettelreferenz nicht gelöscht");

			Optional<Kind> optKind = kinderHinterher.stream().filter(k -> k.equals(kind)).findAny();
			assertTrue(optKind.isEmpty(), "Fehler bei Kind " + kind + ": Kind nicht gelöscht");

		}

		verify(domainEventHandler).handleEvent(any(KlasseDeleted.class));

	}

}
