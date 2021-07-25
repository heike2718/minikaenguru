// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

/**
 * UploadAuthorizationServiceImplTest
 */
@ExtendWith(MockitoExtension.class)
public class UploadAuthorizationServiceImplTest {

	@Mock
	DomainEventHandler domainEventHandler;

	@Mock
	VeranstalterRepository veranstalterRepository;

	@Mock
	WettbewerbService wettbewerbService;

	@Mock
	TeilnahmenRepository teilnahmenRepository;

	@Mock
	LoesungszettelRepository loesungszettelRepository;

	@InjectMocks
	UploadAuthorizationServiceImpl service;

	@Nested
	class UploadKlassenlisteTests {

		private final UploadType uploadType = UploadType.KLASSENLISTE;

		@Test
		void should_authorizeUploadReturnTrue_when_WettbewerbBegonnenAndSchuleAngemeldet() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);
			List<Identifier> schulen = Collections.singletonList(schuleID);
			Veranstalter veranstalter = new Lehrer(new Person("suquwdq", "Flora Fauna"), false, schulen);

			Teilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), schuleID, "Blümchenschule", veranstalterID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));

			// Act
			boolean result = service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);

			// Assert
			assertTrue(result);
			verify(domainEventHandler, never()).handleEvent(any());
			verify(wettbewerbService).aktuellerWettbewerb();
			verify(veranstalterRepository).ofId(veranstalterID);
			verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_keinAktuellerWettbewerb() {

			// Arrange
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.empty());

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Klassenlisten können noch nicht hochgeladen werden, da der Anmeldezeitraum hat noch nicht begonnen hat.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_aktuellerWettbewerbErfasst() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
			assertEquals(WettbewerbStatus.ERFASST, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Klassenlisten können noch nicht hochgeladen werden, da der Anmeldezeitraum hat noch nicht begonnen hat.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAll(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_aktuellerWettbewerbBeendet() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));

			for (int i = 0; i < 4; i++) {

				wettbewerb.naechsterStatus();
			}
			assertEquals(WettbewerbStatus.BEENDET, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Klassenlisten können nicht mehr hochgeladen werden, da der Wettbewerb beendet ist.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAll(schulkuerzel, wettbewerb.id());
			}

		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_veranstalterUnbekannt() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			doNothing().when(domainEventHandler).handleEvent(any());
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.empty());

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie haben keine Berechtigung für diese Aktion.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAll(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_keinLehrer() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			doNothing().when(domainEventHandler).handleEvent(any());

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.PRIVAT);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie haben keine Berechtigung für diese Aktion.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService, never()).aktuellerWettbewerb();
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAll(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_SchuleNichtAngemeldet() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);
			List<Identifier> schulen = Collections.singletonList(schuleID);
			Veranstalter veranstalter = new Lehrer(new Person("suquwdq", "Flora Fauna"), false, schulen);

			doNothing().when(domainEventHandler).handleEvent(any());
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(new ArrayList<>());

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie können keine Klassenliste für diese Schule hochladen, da sie noch nicht zum aktuellen Wettbewerb angemeldet ist.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAll(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadReturnFalse_when_WettbewerbBegonnenAndSchuleAngemeldetUndLoesungszettelMitQuelleUpload() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
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

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAll(schulkuerzel, wettbewerb.id()))
				.thenReturn(Collections.singletonList(onlineZettel));

			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				assertEquals(
					"Sie können keine Klassenliste für diese Schule hochladen, da bereits eine Auswertungstabelle hochgeladen wurde.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository).loadAll(schulkuerzel, wettbewerb.id());

			}
		}
	}

	@Nested
	class UploadAuswertungTests {

		private final UploadType uploadType = UploadType.AUSWERTUNG;

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_keinLehrer() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			doNothing().when(domainEventHandler).handleEvent(any());

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.PRIVAT);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie haben keine Berechtigung für diese Aktion.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService, never()).aktuellerWettbewerb();
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAll(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadReturnTrue_when_WettbewerbBegonnenAndSchuleAngemeldetUndKeineLoesungszettelMitQuelleOnline() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
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

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAll(schulkuerzel, wettbewerb.id())).thenReturn(new ArrayList<>());

			// Act
			boolean result = service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);

			// Assert
			assertTrue(result);
			verify(domainEventHandler, never()).handleEvent(any());
			verify(wettbewerbService).aktuellerWettbewerb();
			verify(veranstalterRepository).ofId(veranstalterID);
			verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
			verify(loesungszettelRepository).loadAll(schulkuerzel, wettbewerb.id());
		}

		@Test
		void should_authorizeUploadReturnTrue_when_AdminAndWettbewerbBegonnenAndSchuleAngemeldetUndKeineLoesungszettelMitQuelleOnline() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);

			Teilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), schuleID, "Blümchenschule", veranstalterID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAll(schulkuerzel, wettbewerb.id())).thenReturn(new ArrayList<>());

			// Act
			boolean result = service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.ADMIN);

			// Assert
			assertTrue(result);
			verify(domainEventHandler, never()).handleEvent(any());
			verify(wettbewerbService).aktuellerWettbewerb();
			verify(veranstalterRepository, never()).ofId(veranstalterID);
			verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
			verify(loesungszettelRepository).loadAll(schulkuerzel, wettbewerb.id());
		}

		@Test
		void should_authorizeUploadReturnFalse_when_WettbewerbBegonnenAndSchuleAngemeldetUndLoesungszettelMitQuelleOnline() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
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

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAll(schulkuerzel, wettbewerb.id()))
				.thenReturn(Collections.singletonList(onlineZettel));

			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				assertEquals(
					"Sie können keine Schulauswertungen für diese Schule hochladen, da bereits Lösungszettel online erfasst wurden.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository).loadAll(schulkuerzel, wettbewerb.id());

			}
		}

		@Test
		void should_authorizeUploadReturnTrue_when_aktuellerWettbewerbsstatusAnmeldungLehrerZugangErteiltKeineOnlineloesungszettel() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
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

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAll(schulkuerzel, wettbewerb.id())).thenReturn(new ArrayList<>());

			// Act + Assert
			assertTrue(service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER));

			verify(domainEventHandler, never()).handleEvent(any());
			verify(wettbewerbService).aktuellerWettbewerb();
			verify(veranstalterRepository).ofId(veranstalterID);
			verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
			verify(loesungszettelRepository).loadAll(schulkuerzel, wettbewerb.id());

		}

		@Test
		void should_authorizeUploadReturnFalse_when_AdminWettbewerbBegonnenAndSchuleAngemeldetUndLoesungszettelMitQuelleOnline() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
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

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAll(schulkuerzel, wettbewerb.id()))
				.thenReturn(Collections.singletonList(onlineZettel));

			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.ADMIN);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				assertEquals(
					"Sie können keine Schulauswertungen für diese Schule hochladen, da bereits Lösungszettel online erfasst wurden.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository).loadAll(schulkuerzel, wettbewerb.id());

			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_keinAktuellerWettbewerb() {

			// Arrange
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.empty());

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Schulauswertungen können noch nicht hochgeladen werden, da der Anmeldezeitraum noch nicht begonnen hat.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAll(any(), any());
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_aktuellerWettbewerbErfasst() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
			assertEquals(WettbewerbStatus.ERFASST, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Schulauswertungen können noch nicht hochgeladen werden, da der Anmeldezeitraum noch nicht begonnen hat.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAll(schulkuerzel, wettbewerb.id());

			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_aktuellerWettbewerbsstatusAnmeldungAndZugangsstatusDefault() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			Identifier schuleID = new Identifier(schulkuerzel);
			List<Identifier> schulen = Collections.singletonList(schuleID);
			Veranstalter veranstalter = new Lehrer(new Person("suquwdq", "Flora Fauna"), false, schulen);

			Teilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), schuleID, "Blümchenschule", veranstalterID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie können keine Schulauswertungen für diese Schule hochladen, da die Aufgaben noch nicht zur Verfügung gestellt wurden.",
					e.getMessage());

				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAll(schulkuerzel, wettbewerb.id());

			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_aktuellerWettbewerbBeendet() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));

			for (int i = 0; i < 4; i++) {

				wettbewerb.naechsterStatus();
			}
			assertEquals(WettbewerbStatus.BEENDET, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Schulauswertungen können nicht mehr hochgeladen werden, da der Wettbewerb beendet ist.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository, never()).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAll(schulkuerzel, wettbewerb.id());

			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_veranstalterUnbekannt() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, wettbewerb.status());
			String veranstalterUuid = UUID.randomUUID().toString();
			Identifier veranstalterID = new Identifier(veranstalterUuid);
			String schulkuerzel = "JGJGJLGL";

			doNothing().when(domainEventHandler).handleEvent(any());
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.empty());

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie haben keine Berechtigung für diese Aktion.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAll(schulkuerzel, wettbewerb.id());

			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_SchuleNichtAngemeldet() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
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
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(new ArrayList<>());

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie können keine Schulauswertungen für diese Schule hochladen, da sie noch nicht zum aktuellen Wettbewerb angemeldet ist.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAll(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_LehrerZugangUnterlagenEntzogen() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
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
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie haben keine Berechtigung für diese Aktion.",
					e.getMessage());

				verify(domainEventHandler).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository, never()).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository, never()).loadAll(schulkuerzel, wettbewerb.id());
			}
		}

		@Test
		void should_authorizeUploadThrowActionNotAuthorizedException_when_OnlineauswertungenGefunden() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2021));
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

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(veranstalterRepository.ofId(veranstalterID)).thenReturn(Optional.of(veranstalter));
			when(teilnahmenRepository.ofTeilnahmenummer(schulkuerzel)).thenReturn(Collections.singletonList(teilnahme));
			when(loesungszettelRepository.loadAll(schulkuerzel, wettbewerb.id()))
				.thenReturn(Collections.singletonList(loesungszettel));

			// Act
			try {

				service.authorizeUpload(veranstalterID, schulkuerzel, uploadType, Rolle.LEHRER);
				fail("keine ActionNotAuthorizedException");
			} catch (ActionNotAuthorizedException e) {

				// Assert
				assertEquals(
					"Sie können keine Schulauswertungen für diese Schule hochladen, da bereits Lösungszettel online erfasst wurden.",
					e.getMessage());

				verify(domainEventHandler, never()).handleEvent(any());
				verify(wettbewerbService).aktuellerWettbewerb();
				verify(veranstalterRepository).ofId(veranstalterID);
				verify(teilnahmenRepository).ofTeilnahmenummer(schulkuerzel);
				verify(loesungszettelRepository).loadAll(schulkuerzel, wettbewerb.id());
			}
		}
	}
}
