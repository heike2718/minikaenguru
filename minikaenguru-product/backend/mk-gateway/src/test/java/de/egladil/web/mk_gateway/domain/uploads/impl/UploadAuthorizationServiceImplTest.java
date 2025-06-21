// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.ActionNotAuthorizedException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.Lehrer;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;

/**
 * UploadAuthorizationServiceImplTest
 */
@QuarkusTest
public class UploadAuthorizationServiceImplTest {

	@InjectMock
	DomainEventHandler domainEventHandler;

	@InjectMock
	VeranstalterRepository veranstalterRepository;

	@InjectMock
	WettbewerbService wettbewerbService;

	@InjectMock
	TeilnahmenRepository teilnahmenRepository;

	@InjectMock
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	UploadAuthorizationServiceImpl service;

	@Nested
	class UploadKlassenlisteTests {

		private final UploadType uploadType = UploadType.KLASSENLISTE;

		@Test
		void should_authorizeUploadReturnTrue_when_WettbewerbBegonnenAndSchuleAngemeldet() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);
			List<Identifier> schulen = Collections.singletonList(schuleID);
			Veranstalter veranstalter = new Lehrer(new Person("suquwdq", "Flora Fauna"), false, schulen);

			Teilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), schuleID, "Blümchenschule", veranstalterID);

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));

			// Act
			Wettbewerb theWettbewerb = service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType,
				Rolle.LEHRER, wettbewerb.id());

			// Assert
			assertEquals(wettbewerb, theWettbewerb);
			verify(domainEventHandler, never()).handleEvent(any());
			verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
			verify(veranstalterRepository).ofId(veranstalterID);
			verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_keinAktuellerWettbewerb() {

			// Arrange
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";
			WettbewerbID wettbewerbID = new WettbewerbID(2011);

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.empty());

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Klassenlisten können noch nicht hochgeladen werden, da der Anmeldezeitraum hat noch nicht begonnen hat.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_aktuellerWettbewerbErfasst() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			assertEquals(WettbewerbStatus.ERFASST, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Klassenlisten können noch nicht hochgeladen werden, da der Anmeldezeitraum hat noch nicht begonnen hat.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_aktuellerWettbewerbBeendet() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);

			for (int i = 0; i < 4; i++) {

				wettbewerb.naechsterStatus();
			}
			assertEquals(WettbewerbStatus.BEENDET, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Klassenlisten können nicht mehr hochgeladen werden, da der Wettbewerb beendet ist.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());
			}

		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_veranstalterUnbekannt() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			doNothing().when(domainEventHandler).handleEvent(any());
			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.empty());

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie haben keine Berechtigung für diese Aktion.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_keinLehrer() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			doNothing().when(domainEventHandler).handleEvent(any());

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.PRIVAT, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie haben keine Berechtigung für diese Aktion.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService, never()).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_SchuleNichtAngemeldet() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);
			List<Identifier> schulen = Collections.singletonList(schuleID);
			Veranstalter veranstalter = new Lehrer(new Person("suquwdq", "Flora Fauna"), false, schulen);

			doNothing().when(domainEventHandler).handleEvent(any());
			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(new ArrayList<>());

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie können keine Klassenliste für diese Schule hochladen, da sie noch nicht zum aktuellen Wettbewerb angemeldet ist.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadReturnFalse_when_WettbewerbBegonnenAndSchuleAngemeldetUndLoesungszettelMitQuelleUpload() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);
			List<Identifier> schulen = Collections.singletonList(schuleID);
			Veranstalter veranstalter = new Lehrer(new Person("suquwdq", "Flora Fauna"), false, schulen);

			Teilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), schuleID, "Blümchenschule", veranstalterID);
			TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier.createFromTeilnahme(teilnahme);
			Loesungszettel onlineZettel = new Loesungszettel().withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withAuswertungsquelle(Auswertungsquelle.UPLOAD);

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id()))
				.thenReturn(Collections.singletonList(onlineZettel));

			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				assertEquals(
					"Sie können keine Klassenliste für diese Schule hochladen, da bereits eine Auswertungstabelle hochgeladen wurde.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());

			}
		}
	}

	@Nested
	class UploadAuswertungTests {

		private final UploadType uploadType = UploadType.AUSWERTUNG;

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_keinLehrer() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			doNothing().when(domainEventHandler).handleEvent(any());

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.PRIVAT, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie haben keine Berechtigung für diese Aktion.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService, never()).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadReturnTrue_when_WettbewerbBegonnenAndSchuleAngemeldetUndKeineLoesungszettelMitQuelleOnline() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);
			List<Identifier> schulen = Collections.singletonList(schuleID);
			Veranstalter veranstalter = new Lehrer(new Person("suquwdq", "Flora Fauna"), false, schulen);

			Teilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), schuleID, "Blümchenschule", veranstalterID);

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id()))
				.thenReturn(new ArrayList<>());

			// Act
			Wettbewerb result = service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType,
				Rolle.LEHRER,
				wettbewerbID);

			// Assert
			assertEquals(wettbewerb, result);
			verify(domainEventHandler, never()).handleEvent(any());
			verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
			verify(veranstalterRepository).ofId(veranstalterID);
			verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
			verify(loesungszettelRepository).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());
		}

		@Test
		void should_authorizeUploadReturnTrue_when_AdminAndWettbewerbBegonnenAndSchuleAngemeldetUndKeineLoesungszettelMitQuelleOnline() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);

			Teilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), schuleID, "Blümchenschule", veranstalterID);

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id()))
				.thenReturn(new ArrayList<>());

			// Act
			Wettbewerb result = service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.ADMIN,
				wettbewerbID);

			// Assert
			assertEquals(wettbewerb, result);
			verify(domainEventHandler, never()).handleEvent(any());
			verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
			verify(veranstalterRepository, never()).ofId(veranstalterID);
			verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
			verify(loesungszettelRepository).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());
		}

		@Test
		void should_authorizeUploadReturnFalse_when_WettbewerbBegonnenAndSchuleAngemeldetUndLoesungszettelMitQuelleOnline() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);
			List<Identifier> schulen = Collections.singletonList(schuleID);
			Veranstalter veranstalter = new Lehrer(new Person("suquwdq", "Flora Fauna"), false, schulen);

			Teilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), schuleID, "Blümchenschule", veranstalterID);
			TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier.createFromTeilnahme(teilnahme);
			Loesungszettel onlineZettel = new Loesungszettel().withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withAuswertungsquelle(Auswertungsquelle.ONLINE);

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id()))
				.thenReturn(Collections.singletonList(onlineZettel));

			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				assertEquals(
					"Sie können keine Auswertungen für diese Schule hochladen, da bereits Lösungszettel online erfasst wurden.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());

			}
		}

		@Test
		void should_authorizeUploadReturnTrue_when_aktuellerWettbewerbsstatusAnmeldungLehrerZugangErteiltKeineOnlineloesungszettel() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);
			List<Identifier> schulen = Collections.singletonList(schuleID);
			Veranstalter veranstalter = new Lehrer(new Person("suquwdq", "Flora Fauna"), false, schulen);
			veranstalter.erlaubeZugangUnterlagen();

			Teilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), schuleID, "Blümchenschule", veranstalterID);

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id()))
				.thenReturn(new ArrayList<>());

			// Act + Assert
			assertEquals(wettbewerb,
				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID));

			verify(domainEventHandler, never()).handleEvent(any());
			verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
			verify(veranstalterRepository).ofId(veranstalterID);
			verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
			verify(loesungszettelRepository).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());

		}

		@Test
		void should_authorizeUploadReturnFalse_when_AdminWettbewerbBegonnenAndSchuleAngemeldetUndLoesungszettelMitQuelleOnline() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);

			Teilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), schuleID, "Blümchenschule", veranstalterID);
			TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier.createFromTeilnahme(teilnahme);
			Loesungszettel onlineZettel = new Loesungszettel().withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withAuswertungsquelle(Auswertungsquelle.ONLINE);

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id()))
				.thenReturn(Collections.singletonList(onlineZettel));

			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.ADMIN, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				assertEquals(
					"Sie können keine Auswertungen für diese Schule hochladen, da bereits Lösungszettel online erfasst wurden.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());

			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_keinAktuellerWettbewerb() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2018);
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.empty());

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Auswertungen können noch nicht hochgeladen werden, da der Anmeldezeitraum noch nicht begonnen hat.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAllWithTeilnahmenummerForWettbewerb(any(), any());
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_aktuellerWettbewerbErfasst() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			assertEquals(WettbewerbStatus.ERFASST, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Auswertungen können noch nicht hochgeladen werden, da der Anmeldezeitraum noch nicht begonnen hat.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());

			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_aktuellerWettbewerbsstatusAnmeldungAndZugangsstatusDefault() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);
			List<Identifier> schulen = Collections.singletonList(schuleID);
			Veranstalter veranstalter = new Lehrer(new Person("suquwdq", "Flora Fauna"), false, schulen);

			Teilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), schuleID, "Blümchenschule", veranstalterID);

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie können keine Auswertungen für diese Schule hochladen, da die Aufgaben noch nicht zur Verfügung gestellt wurden.",
					e.getMessage());

				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());

			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_aktuellerWettbewerbBeendet() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);

			for (int i = 0; i < 4; i++) {

				wettbewerb.naechsterStatus();
			}
			assertEquals(WettbewerbStatus.BEENDET, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Auswertungen können nicht mehr hochgeladen werden, da der Wettbewerb beendet ist. Bitte senden Sie Ihre Auswertungen per Mail an minikaenguru@egladil.de.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());

			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_veranstalterUnbekannt() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			doNothing().when(domainEventHandler).handleEvent(any());
			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.empty());

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie haben keine Berechtigung für diese Aktion.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());

			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_SchuleNichtAngemeldet() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);
			List<Identifier> schulen = Collections.singletonList(schuleID);
			Veranstalter veranstalter = new Lehrer(new Person("suquwdq", "Flora Fauna"), false, schulen);

			doNothing().when(domainEventHandler).handleEvent(any());
			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(new ArrayList<>());

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie können keine Auswertungen für diese Schule hochladen, da sie noch nicht zum aktuellen Wettbewerb angemeldet ist.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_LehrerZugangUnterlagenEntzogen() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);
			List<Identifier> schulen = Collections.singletonList(schuleID);
			Veranstalter veranstalter = new Lehrer(new Person("suquwdq", "Flora Fauna"), false, schulen);
			veranstalter.verwehreZugangUnterlagen();

			doNothing().when(domainEventHandler).handleEvent(any());
			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie haben keine Berechtigung für diese Aktion.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_OnlineauswertungenGefunden() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(2021);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);
			List<Identifier> schulen = Collections.singletonList(schuleID);
			Veranstalter veranstalter = new Lehrer(new Person("suquwdq", "Flora Fauna"), false, schulen);

			Teilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), schuleID, "Blümchenschule", veranstalterID);

			Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.ONLINE);

			when(wettbewerbService.findWettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id()))
				.thenReturn(Collections.singletonList(loesungszettel));

			// Act
			try {

				service.authorizeUploadAndReturnWettbewerb(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER, wettbewerbID);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie können keine Auswertungen für diese Schule hochladen, da bereits Lösungszettel online erfasst wurden.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).findWettbewerbMitID(wettbewerbID);
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository).loadAllWithTeilnahmenummerForWettbewerb(schulkuerzel, wettbewerb.id());
			}
		}
	}
}
