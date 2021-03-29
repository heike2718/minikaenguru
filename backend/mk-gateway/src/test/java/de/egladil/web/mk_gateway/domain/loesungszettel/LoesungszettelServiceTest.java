// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.LoesungszettelNonIdentifiingAttributesMapper;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * LoesungszettelServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class LoesungszettelServiceTest {

	@Mock
	private KinderRepository kinderRepository;

	@Mock
	private LoesungszettelRepository loesungszettelRepository;

	@Mock
	private AuthorizationService authService;

	@Mock
	private WettbewerbService wettbewerbService;

	@InjectMocks
	private LoesungszettelService service;

	@Nested
	class LoesungszettelAnlegenTests {

		@Test
		void should_loesungszettelAnlegenReturnTheConcurrentlySavedEntity_when_concurrentInsert() {

			// Arrange
			String kindID = "ahdgqug";
			Identifier loesungszettelID = new Identifier("asdqgg");
			Identifier veranstalterID = new Identifier("ldjdjw-wjofjp");

			Wettbewerb aktuellerWettbewerb = new Wettbewerb(new WettbewerbID(2021));

			Kind kind = new Kind(new Identifier(kindID)).withKlassenstufe(Klassenstufe.EINS).withLandkuerzel("DE-HE")
				.withSprache(Sprache.de).withLoesungszettelID(loesungszettelID)
				.withTeilnahmeIdentifier(TeilnahmeIdentifierAktuellerWettbewerb.createForSchulteilnahme("JUJHUTR"))
				.withKlasseID(new Identifier("dqguiguq"));

			LoesungszettelAPIModel loesungszettelDaten = new LoesungszettelAPIModel().withKindID(kindID)
				.withKlassenstufe(Klassenstufe.EINS).withUuid("neu");

			LoesungszettelRohdaten rohdaten = new LoesungszettelRohdaten().withNutzereingabe("EBCACCBDBNBN");

			Loesungszettel vorhandener = new Loesungszettel(loesungszettelID).withKindID(new Identifier(kindID)).withPunkte(5000)
				.withLaengeKaengurusprung(5).withRohdaten(rohdaten).withKlassenstufe(Klassenstufe.EINS);
			when(kinderRepository.ofId(new Identifier(kindID))).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.ofID(loesungszettelID)).thenReturn(Optional.of(vorhandener));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));

			// Act
			ResponsePayload responsePayload = service.loesungszettelAnlegen(loesungszettelDaten, veranstalterID);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Ein Lösungszettel für dieses Kind wurde bereits durch eine andere Person gespeichert. Bitte prüfen Sie die neuen Daten. Punkte 50,00, Länge Kängurusprung 5.",
				messagePayload.getMessage());

			assertNotNull(responsePayload.getData());

			verify(kinderRepository, times(1)).ofId(new Identifier(kindID));
			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
			verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
			verify(kinderRepository, times(0)).changeKind(any());
			verify(wettbewerbService, times(1)).aktuellerWettbewerb();

			assertNull(service.getKindChanged());
			assertNull(service.getLoesungszettelCreated());
			assertNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());

		}

		@Test
		void should_loesungszettelAnlegenThrowNotFoundException_when_noKindPresent() {

			// Arrange
			String kindID = "ahdgqug";
			Identifier veranstalterID = new Identifier("ldjdjw-wjofjp");

			LoesungszettelAPIModel loesungszettelDaten = new LoesungszettelAPIModel().withKindID(kindID)
				.withKlassenstufe(Klassenstufe.EINS).withUuid("neu");

			when(kinderRepository.ofId(new Identifier(kindID))).thenReturn(Optional.empty());

			// Act
			try {

				service.loesungszettelAnlegen(loesungszettelDaten, veranstalterID);
				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				verify(loesungszettelRepository, times(0)).ofID(any());
				verify(authService, times(0)).checkPermissionForTeilnahmenummer(any(), any(), any());
				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());
				verify(wettbewerbService, times(0)).aktuellerWettbewerb();

				assertNull(service.getKindChanged());
				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());
			}

		}

		@Test
		void should_loesungszettelAnlegenUpdateKindAndReturnErrorAndNullData_when_kindLoesungszettelIdNotNullButLoesungszettelAbsent() {

			// Arrange
			String kindID = "ahdgqug";
			Identifier loesungszettelID = new Identifier("asdqgg");
			Identifier veranstalterID = new Identifier("ldjdjw-wjofjp");

			Wettbewerb aktuellerWettbewerb = new Wettbewerb(new WettbewerbID(2021));

			Kind kind = new Kind(new Identifier(kindID)).withKlassenstufe(Klassenstufe.EINS).withLandkuerzel("DE-HE")
				.withSprache(Sprache.de).withLoesungszettelID(loesungszettelID)
				.withTeilnahmeIdentifier(TeilnahmeIdentifierAktuellerWettbewerb.createForSchulteilnahme("JUJHUTR"))
				.withKlasseID(new Identifier("dqguiguq"));

			LoesungszettelAPIModel loesungszettelDaten = new LoesungszettelAPIModel().withKindID(kindID)
				.withKlassenstufe(Klassenstufe.EINS).withUuid("neu");

			when(kinderRepository.ofId(new Identifier(kindID))).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.ofID(loesungszettelID)).thenReturn(Optional.empty());
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
			when(kinderRepository.changeKind(kind)).thenReturn(Boolean.TRUE);
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));

			// Act
			ResponsePayload result = service.loesungszettelAnlegen(loesungszettelDaten, veranstalterID);

			// Assert
			verify(kinderRepository, times(1)).ofId(new Identifier(kindID));
			verify(loesungszettelRepository, times(1)).ofID(loesungszettelID);
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
			verify(kinderRepository, times(1)).changeKind(any());
			verify(wettbewerbService, times(1)).aktuellerWettbewerb();

			MessagePayload messagePayload = result.getMessage();
			assertEquals("Der Lösungszettel konnte leider nicht gespeichert werden. Bitte erfassen Sie den Löungszettel neu.",
				messagePayload.getMessage());
			assertEquals("WARN", messagePayload.getLevel());
			assertNull(result.getData());

			assertNotNull(service.getKindChanged());
			assertNull(service.getLoesungszettelCreated());
			assertNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());

		}

		@Test
		void should_loesungszettelAnlegenWork() {

			// Arrange
			String kindID = "ahdgqug";
			Identifier neueLoesungszettelID = new Identifier("ahhqhiohqi");
			Identifier veranstalterID = new Identifier("ldjdjw-wjofjp");

			Wettbewerb aktuellerWettbewerb = new Wettbewerb(new WettbewerbID(2021)).withLoesungsbuchstabenKlasse1("CEBE-DDEC-BCAE");

			Kind kind = new Kind(new Identifier(kindID)).withKlassenstufe(Klassenstufe.EINS).withLandkuerzel("DE-HE")
				.withSprache(Sprache.de)
				.withTeilnahmeIdentifier(TeilnahmeIdentifierAktuellerWettbewerb.createForSchulteilnahme("JUJHUTR"))
				.withKlasseID(new Identifier("dqguiguq"));

			List<LoesungszettelZeileAPIModel> zeilen = createLoesungszettelZeilen();

			LoesungszettelAPIModel loesungszettelDaten = new LoesungszettelAPIModel().withKindID(kindID)
				.withKlassenstufe(Klassenstufe.EINS).withUuid("neu").withZeilen(zeilen);

			when(kinderRepository.ofId(new Identifier(kindID))).thenReturn(Optional.of(kind));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(kinderRepository.changeKind(kind)).thenReturn(Boolean.TRUE);
			when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(neueLoesungszettelID);

			// Act
			ResponsePayload responsePayload = service.loesungszettelAnlegen(loesungszettelDaten, veranstalterID);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Der Lösungszettel wurde erfolgreich gespeichert: Punkte 6,25, Länge Kängurusprung 1.",
				messagePayload.getMessage());

			assertNotNull(responsePayload.getData());

			LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) responsePayload.getData();

			assertEquals(neueLoesungszettelID.identifier(), result.loesungszettelId());
			List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

			assertEquals(zeilen.size(), actualZeilen.size());

			for (int i = 0; i < zeilen.size(); i++) {

				assertEquals("Fehler bei Index= " + i, zeilen.get(i), actualZeilen.get(i));
			}

			verify(kinderRepository, times(1)).ofId(new Identifier(kindID));
			verify(loesungszettelRepository, times(0)).ofID(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
			verify(loesungszettelRepository, times(1)).addLoesungszettel(any());
			verify(kinderRepository, times(1)).changeKind(any());
			verify(wettbewerbService, times(1)).aktuellerWettbewerb();

			assertNotNull(service.getKindChanged());
			assertNotNull(service.getLoesungszettelCreated());
			assertNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());
		}

	}

	@Nested
	class LoesungszettelAendernTests {

		@Test
		void should_loesungszettelAendernThrowNotFoundException_when_noLoesungszettelPresent() {

			// Arrange
			String kindID = "ahdgqug";
			String loesungszettelUuid = "skkojqo";
			Identifier veranstalterID = new Identifier("ldjdjw-wjofjp");

			LoesungszettelAPIModel loesungszettelDaten = new LoesungszettelAPIModel().withKindID(kindID)
				.withKlassenstufe(Klassenstufe.EINS).withUuid(loesungszettelUuid).withUuid(loesungszettelUuid);

			when(loesungszettelRepository.ofID(new Identifier(loesungszettelUuid))).thenReturn(Optional.empty());

			// Act
			try {

				service.loesungszettelAendern(loesungszettelDaten, veranstalterID);
				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				verify(loesungszettelRepository, times(1)).ofID(any());
				verify(kinderRepository, times(0)).ofId(any());
				verify(authService, times(0)).checkPermissionForTeilnahmenummer(any(), any(), any());
				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());
				verify(wettbewerbService, times(0)).aktuellerWettbewerb();

				assertNull(service.getKindChanged());
				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());
			}

		}

		@Test
		void should_loesungszettelAendernCallAuthService() {

			// Arrange
			String kindID = "ahdgqug";
			String loesungszettelUuid = "skkojqo";
			Identifier veranstalterID = new Identifier("ldjdjw-wjofjp");
			String teilnahmenummer = "FFFUFGFT76";

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.PRIVAT)
				.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(new WettbewerbID(2020));

			LoesungszettelAPIModel loesungszettelDaten = new LoesungszettelAPIModel().withKindID(kindID)
				.withKlassenstufe(Klassenstufe.EINS).withUuid(loesungszettelUuid).withUuid(loesungszettelUuid);

			Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.ONLINE)
				.withIdentifier(new Identifier(loesungszettelUuid))
				.withKindID(new Identifier(kindID)).withKlassenstufe(Klassenstufe.EINS)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			when(loesungszettelRepository.ofID(new Identifier(loesungszettelUuid))).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenThrow(new AccessDeniedException("nö"));

			// Act
			try {

				service.loesungszettelAendern(loesungszettelDaten, veranstalterID);
				fail("keine AccessDeniedException");
			} catch (AccessDeniedException e) {

				assertEquals("nö", e.getMessage());

				verify(loesungszettelRepository, times(1)).ofID(any());
				verify(kinderRepository, times(0)).ofId(any());
				verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());
				verify(wettbewerbService, times(0)).aktuellerWettbewerb();

				assertNull(service.getKindChanged());
				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());
			}

		}

		@Test
		void should_loesungszettelAendernThrowNotFoundException_when_noKindPresent() {

			// Arrange
			String kindID = "ahdgqug";
			String loesungszettelUuid = "skkojqo";
			Identifier veranstalterID = new Identifier("ldjdjw-wjofjp");
			String teilnahmenummer = "FFFUFGFT76";

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.PRIVAT)
				.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(new WettbewerbID(2020));

			LoesungszettelAPIModel loesungszettelDaten = new LoesungszettelAPIModel().withKindID(kindID)
				.withKlassenstufe(Klassenstufe.EINS).withUuid(loesungszettelUuid).withUuid(loesungszettelUuid);

			Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.ONLINE)
				.withIdentifier(new Identifier(loesungszettelUuid))
				.withKindID(new Identifier(kindID)).withKlassenstufe(Klassenstufe.EINS)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			when(loesungszettelRepository.ofID(new Identifier(loesungszettelUuid))).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
			when(kinderRepository.ofId(new Identifier(kindID))).thenReturn(Optional.empty());

			// Act
			try {

				service.loesungszettelAendern(loesungszettelDaten, veranstalterID);
				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				verify(loesungszettelRepository, times(1)).ofID(any());
				verify(kinderRepository, times(1)).ofId(any());
				verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());
				verify(wettbewerbService, times(0)).aktuellerWettbewerb();

				assertNull(service.getKindChanged());
				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());
			}

		}

		@Test
		void should_loesungszettelAendernThrowServerException_when_kindLoesungszettelAndLoesungszettelDiffer() {

			// Arrange
			String kindID = "ahdgqug";
			String loesungszettelUuid = "skkojqo";
			Identifier veranstalterID = new Identifier("ldjdjw-wjofjp");
			String teilnahmenummer = "FFFUFGFT76";

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.PRIVAT)
				.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(new WettbewerbID(2020));

			LoesungszettelAPIModel loesungszettelDaten = new LoesungszettelAPIModel().withKindID(kindID)
				.withKlassenstufe(Klassenstufe.EINS).withUuid(loesungszettelUuid).withUuid(loesungszettelUuid);

			Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.ONLINE)
				.withIdentifier(new Identifier(loesungszettelUuid))
				.withKindID(new Identifier(kindID)).withKlassenstufe(Klassenstufe.EINS)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			Kind kind = new Kind(new Identifier(kindID)).withLoesungszettelID(new Identifier("hallo"));

			when(loesungszettelRepository.ofID(new Identifier(loesungszettelUuid))).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
			when(kinderRepository.ofId(new Identifier(kindID))).thenReturn(Optional.of(kind));

			// Act
			try {

				service.loesungszettelAendern(loesungszettelDaten, veranstalterID);
				fail("keine MkGatewayRuntimeException");
			} catch (MkGatewayRuntimeException e) {

				assertEquals(
					"Für Kind mit UUID=ahdgqug passen kind.loesungszettelID=hallo und loesungszettel.uuid=skkojqo nicht zueinander. Triggering user=ldjdjw-w...",
					e.getMessage());

				verify(loesungszettelRepository, times(1)).ofID(any());
				verify(kinderRepository, times(1)).ofId(any());
				verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());
				verify(wettbewerbService, times(0)).aktuellerWettbewerb();

				assertNull(service.getKindChanged());
				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());
			}

		}

		@Test
		void should_loesungszettelAendernWork() {

			// Arrange
			String kindID = "ahdgqug";
			String loesungszettelUuid = "skkojqo";
			Identifier veranstalterID = new Identifier("ldjdjw-wjofjp");
			String teilnahmenummer = "JUJHUTR";

			Wettbewerb aktuellerWettbewerb = new Wettbewerb(new WettbewerbID(2021)).withLoesungsbuchstabenKlasse1("CEBE-DDEC-BCAE");

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(new WettbewerbID(2020));

			List<LoesungszettelZeileAPIModel> zeilen = createLoesungszettelZeilen();

			LoesungszettelAPIModel loesungszettelDaten = new LoesungszettelAPIModel().withKindID(kindID)
				.withKlassenstufe(Klassenstufe.EINS).withUuid(loesungszettelUuid).withUuid(loesungszettelUuid).withZeilen(zeilen);

			Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.ONLINE)
				.withIdentifier(new Identifier(loesungszettelUuid))
				.withKindID(new Identifier(kindID)).withKlassenstufe(Klassenstufe.EINS)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			Kind kind = new Kind(new Identifier(kindID)).withKlassenstufe(Klassenstufe.EINS).withLandkuerzel("DE-HE")
				.withSprache(Sprache.de)
				.withTeilnahmeIdentifier(TeilnahmeIdentifierAktuellerWettbewerb.createForSchulteilnahme("JUJHUTR"))
				.withKlasseID(new Identifier("dqguiguq"))
				.withLoesungszettelID(new Identifier(loesungszettelUuid));

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(new Identifier(loesungszettelUuid))).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
			when(kinderRepository.ofId(new Identifier(kindID))).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.updateLoesungszettel(loesungszettel)).thenReturn(Boolean.TRUE);

			// Act
			LoesungszettelpunkteAPIModel result = service.loesungszettelAendern(loesungszettelDaten, veranstalterID);

			assertEquals(1, result.laengeKaengurusprung());
			assertEquals("6,25", result.punkte());
			assertEquals(loesungszettelUuid, result.loesungszettelId());
			List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

			assertEquals(zeilen.size(), actualZeilen.size());

			for (int i = 0; i < zeilen.size(); i++) {

				assertEquals("Fehler bei Index= " + i, zeilen.get(i), actualZeilen.get(i));
			}

			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
			verify(loesungszettelRepository, times(1)).updateLoesungszettel(any());
			verify(kinderRepository, times(0)).changeKind(any());
			verify(wettbewerbService, times(1)).aktuellerWettbewerb();

			assertNull(service.getKindChanged());
			assertNull(service.getLoesungszettelCreated());
			assertNotNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());

		}
	}

	@Nested
	class LoesungszettelLoeschenTests {

		@Test
		void should_loesungszettelLoeschenWithAuthorizationCheckReturnFalse_when_keinLoesungszettel() {

			// Arrange
			String loesungszettelUuid = "skkojqo";
			Identifier loesungszettelID = new Identifier(loesungszettelUuid);
			Identifier veranstalterID = new Identifier("ldjdjw-wjofjp");

			when(loesungszettelRepository.ofID(loesungszettelID)).thenReturn(Optional.empty());

			// Act
			service.loesungszettelLoeschenWithAuthorizationCheck(loesungszettelID, veranstalterID);

			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(kinderRepository, times(0)).ofId(any());
			verify(authService, times(0)).checkPermissionForTeilnahmenummer(any(), any(), any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(loesungszettelID, veranstalterID.identifier());
			verify(kinderRepository, times(0)).changeKind(any());
			verify(wettbewerbService, times(0)).aktuellerWettbewerb();

			assertNull(service.getKindChanged());
			assertNull(service.getLoesungszettelCreated());
			assertNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());

		}

		@Test
		void should_loesungszettelLoeschenWithAuthorizationCheckThrowAccessDenied_when_notAuthorized() {

			// Arrange
			String kindID = "ahdgqug";
			String loesungszettelUuid = "skkojqo";
			Identifier loesungszettelID = new Identifier(loesungszettelUuid);
			Identifier veranstalterID = new Identifier("ldjdjw-wjofjp");
			String teilnahmenummer = "G7HUIFTF";

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(new WettbewerbID(2020));

			Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.ONLINE)
				.withIdentifier(new Identifier(loesungszettelUuid))
				.withKindID(new Identifier(kindID)).withKlassenstufe(Klassenstufe.EINS)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			when(loesungszettelRepository.ofID(new Identifier(loesungszettelUuid))).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenThrow(new AccessDeniedException("nö"));

			// Act
			try {

				service.loesungszettelLoeschenWithAuthorizationCheck(loesungszettelID, veranstalterID);
				fail("keine AccessDeniedException");
			} catch (AccessDeniedException e) {

				assertEquals("nö", e.getMessage());

				verify(loesungszettelRepository, times(1)).ofID(any());
				verify(kinderRepository, times(0)).ofId(any());
				verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(loesungszettelID, veranstalterID.identifier());
				verify(kinderRepository, times(0)).changeKind(any());
				verify(wettbewerbService, times(0)).aktuellerWettbewerb();

				assertNull(service.getKindChanged());
				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());

			}

		}

		@Test
		void should_loesungszettelLoeschenWithAuthorizationCheckPropagateEvent_when_LoesungszettelVorhanden() {

			// Arrange
			String loesungszettelUuid = "skkojqo";
			Identifier kindID = new Identifier("lshdqh");
			Identifier loesungszettelID = new Identifier(loesungszettelUuid);
			Identifier veranstalterID = new Identifier("ldjdjw-wjofjp");
			String teilnahmenummer = "G7HUIFTF";

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(new WettbewerbID(2020));

			Wettbewerb aktuellerWettbewerb = new Wettbewerb(new WettbewerbID(2021)).withLoesungsbuchstabenKlasse1("CEBE-DDEC-BCAE");

			Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.ONLINE)
				.withIdentifier(new Identifier(loesungszettelUuid))
				.withKindID(kindID).withKlassenstufe(Klassenstufe.EINS)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			Kind kind = new Kind(kindID).withKlassenstufe(Klassenstufe.EINS).withLandkuerzel("DE-HE")
				.withSprache(Sprache.de)
				.withTeilnahmeIdentifier(TeilnahmeIdentifierAktuellerWettbewerb.createForSchulteilnahme("JUJHUTR"))
				.withKlasseID(new Identifier("dqguiguq"))
				.withLoesungszettelID(new Identifier(loesungszettelUuid));

			PersistenterLoesungszettel persistenter = new PersistenterLoesungszettel();
			new LoesungszettelNonIdentifiingAttributesMapper().copyAllAttributesButIdentifier(persistenter, loesungszettel);
			persistenter.setUuid(loesungszettelUuid);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(new Identifier(loesungszettelUuid))).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
			when(kinderRepository.ofId(kindID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.removeLoesungszettel(loesungszettelID, veranstalterID.identifier()))
				.thenReturn(Optional.of(persistenter));

			// Act
			service.loesungszettelLoeschenWithAuthorizationCheck(loesungszettelID, veranstalterID);

			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
			verify(loesungszettelRepository, times(1)).removeLoesungszettel(loesungszettelID, veranstalterID.identifier());
			verify(kinderRepository, times(1)).changeKind(any());
			verify(wettbewerbService, times(1)).aktuellerWettbewerb();

			assertNotNull(service.getKindChanged());
			assertNull(service.getLoesungszettelCreated());
			assertNull(service.getLoesungszettelChanged());
			assertNotNull(service.getLoesungszettelDeleted());

		}

	}

	/**
	 * @return
	 */
	private List<LoesungszettelZeileAPIModel> createLoesungszettelZeilen() {

		List<LoesungszettelZeileAPIModel> zeilen = new ArrayList<>();

		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.A).withIndex(0).withName("A-1"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.A).withIndex(1).withName("A-2"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.A).withIndex(2).withName("A-3"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.A).withIndex(3).withName("A-4"));

		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.B).withIndex(4).withName("B-1"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.B).withIndex(5).withName("B-2"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.B).withIndex(6).withName("B-3"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.B).withIndex(7).withName("B-4"));

		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.C).withIndex(8).withName("C-1"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.C).withIndex(9).withName("C-2"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.C).withIndex(10).withName("C-3"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.C).withIndex(11).withName("C-4"));
		return zeilen;
	}

}
