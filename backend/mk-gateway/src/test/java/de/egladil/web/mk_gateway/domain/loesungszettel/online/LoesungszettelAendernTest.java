// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.online;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.ConcurrentModificationType;
import de.egladil.web.mk_gateway.domain.error.EntityConcurrentlyModifiedException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.events.LoesungszettelChanged;
import de.egladil.web.mk_gateway.domain.kinder.events.LoesungszettelCreated;
import de.egladil.web.mk_gateway.domain.kinder.events.LoesungszettelDeleted;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;

/**
 * LoesungszettelAendernTest
 */
@QuarkusTest
public class LoesungszettelAendernTest extends AbstractLoesungszettelServiceTest {

	@InjectMock
	KinderRepository kinderRepository;

	@InjectMock
	LoesungszettelRepository loesungszettelRepository;

	@InjectMock
	AuthorizationService authService;

	@InjectMock
	WettbewerbService wettbewerbService;

	@InjectMock
	LoggableEventDelegate eventDelegate;

	@InjectMock
	DomainEventHandler domainEventHandler;

	@Inject
	OnlineLoesungszettelService service;

	@BeforeEach
	void setUp() {

		super.init();
	}

	@Nested
	class IllegalArgumentsTests {

		@Test
		@DisplayName("requestDaten.uuid null => Abbruch mit 422")
		void should_loesungszettelAendernThrowInvalidInputException_when_loesungszettelIDNull() {

			// Arrange
			LoesungszettelAPIModel datenOhneLoesungszettelUuid = new LoesungszettelAPIModel()
				.withKindID(REQUEST_KIND_ID.identifier());

			// Act
			try {

				service.loesungszettelAendern(datenOhneLoesungszettelUuid, VERANSTALTER_ID);
				fail("keine MkGatewayRuntimeException");
			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();

				MessagePayload messagePayload = responsePayload.getMessage();
				assertEquals("ERROR", messagePayload.getLevel());
				assertEquals(
					"Der Lösungszettel konnte leider nicht gespeichert werden: uuid null. Bitte wenden Sie sich per Mail an info@egladil.de.",
					messagePayload.getMessage());

				assertNull(responsePayload.getData());

				assertNull(e.getMessage());

				verify(wettbewerbService, times(0)).aktuellerWettbewerb();
				verify(loesungszettelRepository, times(0)).ofID(any());
				verify(kinderRepository, times(0)).ofId(any());
				verify(authService, times(0)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());

				verify(domainEventHandler, never()).handleEvent(any());
			}
		}

		@Test
		@DisplayName("requestDaten.kindID null => Abbruch mit 422")
		void should_loesungszettelAendernThrowInvalidInputException_when_kindIDNull() {

			// Arrange
			LoesungszettelAPIModel datenOhneKindUuid = new LoesungszettelAPIModel()
				.withUuid(REQUEST_LOESUNGSZETTEL_ID.identifier());

			// Act
			try {

				service.loesungszettelAendern(datenOhneKindUuid, VERANSTALTER_ID);
				fail("keine MkGatewayRuntimeException");
			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();

				MessagePayload messagePayload = responsePayload.getMessage();
				assertEquals("ERROR", messagePayload.getLevel());
				assertEquals(
					"Der Lösungszettel konnte leider nicht gespeichert werden: kindID null. Bitte wenden Sie sich per Mail an info@egladil.de.",
					messagePayload.getMessage());

				assertNull(responsePayload.getData());

				assertNull(e.getMessage());

				verify(wettbewerbService, times(0)).aktuellerWettbewerb();
				verify(loesungszettelRepository, times(0)).ofID(any());
				verify(kinderRepository, times(0)).ofId(any());
				verify(authService, times(0)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());

				verify(domainEventHandler, never()).handleEvent(any());
			}
		}
	}

	@Nested
	class KindExistiertNichtTests {

		@Test
		@DisplayName("3) loesungszettel existiert: loesungszettel.jahr == aktuelles Jahr, loesungszettel.kindID != null => deleteLösungszettel und 404")
		void should_loesungszettelAendernTriggerDeleteEventAndThrowNotFoundException_when_kindAbsentAndLoesungszettelPresent() {

			// Arrange
			Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.ONLINE)
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID).withKlassenstufe(Klassenstufe.EINS)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(loesungszettelRepository.removeLoesungszettel(REQUEST_LOESUNGSZETTEL_ID))
				.thenReturn(Optional.of(persistenterLoesungszettel));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.empty());

			// Act
			ResponsePayload result = service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);

			MessagePayload messagePayload = result.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals("Das Kind wurde in der Zwischenzeit gelöscht. Bitte klären Sie dies im Kollegium.",
				messagePayload.getMessage());

			assertNotNull(result.getData());

			LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) result.getData();
			assertEquals(REQUEST_LOESUNGSZETTEL_ID.identifier(), responseData.loesungszettelId());
			assertEquals(ConcurrentModificationType.DETETED, responseData.getConcurrentModificationType());

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(0)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

			verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(1)).removeLoesungszettel(any());
			verify(kinderRepository, times(0)).changeKind(any());

			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelCreated.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
			verify(domainEventHandler, times(1)).handleEvent(any(LoesungszettelDeleted.class));

		}

		@Test
		@DisplayName("2) loesungszettel existiert: loesungszettel.jahr == vorjahr Jahr, loesungszettel.kindID != null => Abbruch und 422")
		void should_loesungszettelAendernTriggerThrowInvalidInputException_when_kindAbsentAndLoesungszettelAusVorjahr() {

			// Arrange
			TeilnahmeIdentifier teilnahmeId2027 = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(teilnahmeIdentifier.teilnahmenummer()).withWettbewerbID(new WettbewerbID(2017));

			Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.ONLINE)
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID).withKlassenstufe(Klassenstufe.EINS)
				.withTeilnahmeIdentifier(teilnahmeId2027);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.empty());

			// Act
			try {

				service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
				fail("keine InvalidInputException");
			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();
				MessagePayload messagePayload = responsePayload.getMessage();
				assertEquals("WARN", messagePayload.getLevel());
				assertEquals("Dieser Lösungszettel ist aus dem Jahr 2017. Er kann nicht geändert werden.",
					messagePayload.getMessage());

				assertNull(responsePayload.getData());

				verify(wettbewerbService, times(1)).aktuellerWettbewerb();
				verify(loesungszettelRepository, times(1)).ofID(any());
				verify(kinderRepository, times(1)).ofId(any());
				verify(authService, times(0)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());

				verify(domainEventHandler, never()).handleEvent(any());
			}

		}

		@Test
		@DisplayName("1) kind und loesungszettel existieren nicht => 404")
		void should_loesungszettelAendernThrowNotFoundException_when_noKindPresent() {

			// Arrange
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.empty());

			// Act
			ResponsePayload result = service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);

			MessagePayload messagePayload = result.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals("Das Kind wurde in der Zwischenzeit gelöscht. Bitte klären Sie dies im Kollegium.",
				messagePayload.getMessage());

			assertNotNull(result.getData());

			LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) result.getData();
			assertEquals(REQUEST_LOESUNGSZETTEL_ID.identifier(), responseData.loesungszettelId());
			assertEquals(ConcurrentModificationType.DETETED, responseData.getConcurrentModificationType());

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(0)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

			verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
			verify(kinderRepository, times(0)).changeKind(any());

			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelCreated.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));

		}
	}

	@Nested
	class KindExistiertLoesungszettelExistiertNichtTests {

		@Test
		@DisplayName("1) konkurrierend gelöscht: kind.lzID null, anderer loesungszettel existiert nicht => anlegen")
		void should_loesungszettelAendernSwitchToAnlegen_when_kindPresentLoesungszettelAbsent_andKindOhneLoesungszettelreferenz() {

			// Arrange
			Identifier neueLoesungszettelID = new Identifier("neueLoesungszettelID");
			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID);

			Loesungszettel neuerLoesungszettel = new Loesungszettel().withIdentifier(neueLoesungszettelID).withVersion(0)
				.withKindID(REQUEST_KIND_ID).withKlassenstufe(Klassenstufe.EINS).withRohdaten(createRohdatenKlasseEINS())
				.withPunkte(625).withLaengeKaengurusprung(1);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());
			when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(neuerLoesungszettel);

			// Act
			ResponsePayload responsePayload = service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Der Lösungszettel wurde erfolgreich gespeichert: Punkte 6,25, Länge Kängurusprung 1.",
				messagePayload.getMessage());

			LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) responsePayload.getData();

			assertEquals(1, result.laengeKaengurusprung());
			assertEquals("6,25", result.punkte());
			assertEquals(neueLoesungszettelID.identifier(), result.loesungszettelId());
			List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

			assertEquals(zeilen.size(), actualZeilen.size());

			for (int i = 0; i < zeilen.size(); i++) {

				assertEquals(actualZeilen.get(i), zeilen.get(i), "Fehler bei Index= " + i);
			}

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(1)).ofID(REQUEST_LOESUNGSZETTEL_ID);
			verify(loesungszettelRepository, times(1)).ofID(null);
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

			verify(loesungszettelRepository, times(1)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
			verify(kinderRepository, times(1)).changeKind(any());

			verify(domainEventHandler).handleEvent(any(LoesungszettelCreated.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));
		}

		@Test
		@DisplayName("2) kindLz != null, loesungszettelMitKind existiert nicht, loesungszettel mit kind.lzID existiert nicht => anlegen")
		void should_loesungszettelAendernSwitchToAnlegen_when_keinLoesungszettel() {

			// Arrange
			Identifier kindLoesungszettelID = new Identifier("kind-loesungszettel-uuid");
			Identifier neueLoesungszettelID = new Identifier("neueLoesungszettelID");

			Loesungszettel neuerLoesungszettel = new Loesungszettel().withIdentifier(neueLoesungszettelID).withVersion(0)
				.withKindID(REQUEST_KIND_ID).withKlassenstufe(Klassenstufe.EINS).withRohdaten(createRohdatenKlasseEINS())
				.withPunkte(625).withLaengeKaengurusprung(1);

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(kindLoesungszettelID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());
			when(loesungszettelRepository.findLoesungszettelWithKindID(kind.identifier()))
				.thenReturn(Optional.empty());
			when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(neuerLoesungszettel);
			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);

			// Act
			ResponsePayload responsePayload = service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Der Lösungszettel wurde erfolgreich gespeichert: Punkte 6,25, Länge Kängurusprung 1.",
				messagePayload.getMessage());

			LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) responsePayload.getData();

			assertEquals(1, result.laengeKaengurusprung());
			assertEquals("6,25", result.punkte());
			assertEquals(neueLoesungszettelID.identifier(), result.loesungszettelId());
			List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

			assertEquals(zeilen.size(), actualZeilen.size());

			for (int i = 0; i < zeilen.size(); i++) {

				assertEquals(zeilen.get(i), actualZeilen.get(i), "Fehler bei Index= " + i);
			}

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(2)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

			verify(loesungszettelRepository, times(1)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
			verify(kinderRepository, times(1)).changeKind(any());

			verify(domainEventHandler).handleEvent(any(LoesungszettelCreated.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));

		}

		@Test
		@DisplayName("3) kindLz != null, referencedLZ null, requestedLz == kindLz, exists lz with kindId = kind, lz.jahr = aktuellerWettbewerb => loeschen und neu anlegen")
		void should_loesungszettelAendernLoeschtToteReferenzUndLegtLoesungszettelNeuAn_when_referencedLoesungszettelAbsentRequestLoesungszettelAbsentLoesungszettelMitKindIDExistsActual() {

			// Arrange
			Identifier kindLoesungszettelID = new Identifier("kind-loesungszettel-uuid");
			Identifier neueLoesungszettelID = new Identifier("neueLoesungszettelID");

			Loesungszettel andererLoesungszettel = new Loesungszettel()
				.withIdentifier(kindLoesungszettelID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier).withRohdaten(createRohdatenKlasseEINS()).withPunkte(625)
				.withLaengeKaengurusprung(1).withKlassenstufe(Klassenstufe.EINS);

			Loesungszettel neuerLoesungszettel = new Loesungszettel().withIdentifier(neueLoesungszettelID).withVersion(0)
				.withKindID(REQUEST_KIND_ID).withKlassenstufe(Klassenstufe.EINS).withRohdaten(createRohdatenKlasseEINS())
				.withPunkte(625).withLaengeKaengurusprung(1);

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(kindLoesungszettelID);

			PersistenterLoesungszettel persistenter = new PersistenterLoesungszettel();
			persistenter.setUuid(kindLoesungszettelID.identifier());
			persistenter.setKindID(kind.identifier().identifier());
			persistenter.setNutzereingabe("NNNNNNNNNNNN");
			persistenter.setAntwortcode("NNNNNNNNNNNN");
			persistenter.setWertungscode("nnnnnnnnnnnn");
			persistenter.setSprache(Sprache.de);
			persistenter.setWettbewerbUuid("2020");
			persistenter.setTeilnahmenummer(kind.teilnahmeIdentifier().teilnahmenummer());
			persistenter.setTeilnahmeart(Teilnahmeart.SCHULE);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());
			when(loesungszettelRepository.findLoesungszettelWithKindID(kind.identifier()))
				.thenReturn(Optional.of(andererLoesungszettel));
			when(loesungszettelRepository.removeLoesungszettel(kindLoesungszettelID)).thenReturn(Optional.of(persistenter));
			when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(neuerLoesungszettel);
			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);

			// Act
			ResponsePayload responsePayload = service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Der Lösungszettel wurde erfolgreich gespeichert: Punkte 6,25, Länge Kängurusprung 1.",
				messagePayload.getMessage());

			LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) responsePayload.getData();

			assertEquals(1, result.laengeKaengurusprung());
			assertEquals("6,25", result.punkte());
			assertEquals(neueLoesungszettelID.identifier(), result.loesungszettelId());
			List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

			assertEquals(zeilen.size(), actualZeilen.size());

			for (int i = 0; i < zeilen.size(); i++) {

				assertEquals(zeilen.get(i), actualZeilen.get(i), "Fehler bei Index= " + i);
			}

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(2)).ofID(any());
			verify(loesungszettelRepository, times(1)).removeLoesungszettel(kindLoesungszettelID);
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

			verify(loesungszettelRepository, times(1)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(1)).removeLoesungszettel(any());
			verify(kinderRepository, times(1)).changeKind(any());

			verify(domainEventHandler).handleEvent(any(LoesungszettelCreated.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
			verify(domainEventHandler).handleEvent(any(LoesungszettelDeleted.class));
		}

		@Test
		@DisplayName("4) kindLz != null, referencedLZ null, requestedLz == kindLz, exists lz with kindId = kind, lz.jahr = aktuellerWettbewerb => Abbruch 422 inkonsistente Daten")
		void should_loesungszettelAendernThrowInvalidInputException_when_referencedLoesungszettelAbsentRequestLoesungszettelAbsentLoesungszettelMitKindIDExistsVorjahr() {

			// Arrange
			Identifier kindLoesungszettelID = new Identifier("kind-loesungszettel-uuid");

			TeilnahmeIdentifier otherTeinahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(TEILNAHMENUMMER).withWettbewerbID(new WettbewerbID(2017));

			Loesungszettel andererLoesungszettel = new Loesungszettel()
				.withIdentifier(kindLoesungszettelID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(otherTeinahmeIdentifier).withRohdaten(createRohdatenKlasseEINS()).withPunkte(625)
				.withLaengeKaengurusprung(1).withKlassenstufe(Klassenstufe.EINS);

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(kindLoesungszettelID);

			PersistenterLoesungszettel persistenter = new PersistenterLoesungszettel();
			persistenter.setUuid(kindLoesungszettelID.identifier());
			persistenter.setKindID(kind.identifier().identifier());
			persistenter.setNutzereingabe("NNNNNNNNNNNN");
			persistenter.setAntwortcode("NNNNNNNNNNNN");
			persistenter.setWertungscode("nnnnnnnnnnnn");
			persistenter.setSprache(Sprache.de);
			persistenter.setWettbewerbUuid("2020");
			persistenter.setTeilnahmenummer(kind.teilnahmeIdentifier().teilnahmenummer());
			persistenter.setTeilnahmeart(Teilnahmeart.SCHULE);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());
			when(loesungszettelRepository.findLoesungszettelWithKindID(kind.identifier()))
				.thenReturn(Optional.of(andererLoesungszettel));
			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);

			// Act
			try {

				service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
				fail("keine InvalidInputException");
			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();
				MessagePayload messagePayload = responsePayload.getMessage();
				assertEquals("WARN", messagePayload.getLevel());
				assertEquals(
					"Der Lösungszettel konnte leider nicht gespeichert werden: es gibt inkonsistente Daten in der Datenbank. Bitte wenden Sie sich per Mail an info@egladil.de.",
					messagePayload.getMessage());

				assertNull(responsePayload.getData());

				verify(wettbewerbService, times(1)).aktuellerWettbewerb();
				verify(loesungszettelRepository, times(2)).ofID(any());
				verify(kinderRepository, times(1)).ofId(any());
				verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());

				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));
			}
		}

		@Test
		@DisplayName("5) kindLz != null, loesungszettelMitKind existiert, loesungszettel mit kind.lzID existiert nicht => 422 inkonsistente Daten")
		void should_loesungszettelAendernNotAendern_when_LoesungszettelMitKindExists() {

			// Arrange
			Identifier kindLoesungszettelID = new Identifier("kind-loesungszettel-uuid");
			Identifier neueLoesungszettelID = new Identifier("neueLoesungszettelID");

			Loesungszettel neuerLoesungszettel = new Loesungszettel().withIdentifier(neueLoesungszettelID).withVersion(0)
				.withKindID(REQUEST_KIND_ID).withKlassenstufe(Klassenstufe.EINS).withRohdaten(createRohdatenKlasseEINS())
				.withPunkte(625).withLaengeKaengurusprung(1);

			Loesungszettel vorhandenerLoesungszettel = new Loesungszettel().withIdentifier(REQUEST_KIND_ID).withVersion(0)
				.withKindID(REQUEST_KIND_ID).withKlassenstufe(Klassenstufe.EINS).withRohdaten(createRohdatenKlasseEINS())
				.withPunkte(625).withLaengeKaengurusprung(1).withTeilnahmeIdentifier(teilnahmeIdentifier);

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(kindLoesungszettelID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());
			when(loesungszettelRepository.findLoesungszettelWithKindID(kind.identifier()))
				.thenReturn(Optional.of(vorhandenerLoesungszettel));
			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);
			when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(neuerLoesungszettel);

			// Act
			ResponsePayload responsePayload = service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Der Lösungszettel wurde erfolgreich gespeichert: Punkte 6,25, Länge Kängurusprung 1.",
				messagePayload.getMessage());

			LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) responsePayload.getData();

			assertEquals(1, result.laengeKaengurusprung());
			assertEquals("6,25", result.punkte());
			assertEquals(neueLoesungszettelID.identifier(), result.loesungszettelId());
			List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

			assertEquals(zeilen.size(), actualZeilen.size());

			for (int i = 0; i < zeilen.size(); i++) {

				assertEquals(zeilen.get(i), actualZeilen.get(i), "Fehler bei Index= " + i);
			}

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(2)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

			verify(loesungszettelRepository, times(1)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(1)).removeLoesungszettel(any());
			verify(kinderRepository, times(1)).changeKind(any());

			verify(domainEventHandler).handleEvent(any(LoesungszettelCreated.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));

		}

		@Test
		@DisplayName("6) kindLz != null, loesungszettel mit kind.lzID existiert => 422 inkonsistente Daten")
		void should_loesungszettelAendernThrowInvalidInpitException_when_VonKindReferenzierterLoesungszettelExistiert() {

			// Arrange
			Identifier kindLoesungszettelID = new Identifier("kind-loesungszettel-uuid");
			Identifier neueLoesungszettelID = new Identifier("neueLoesungszettelID");

			Loesungszettel neuerLoesungszettel = new Loesungszettel().withIdentifier(neueLoesungszettelID).withVersion(0)
				.withKindID(REQUEST_KIND_ID).withKlassenstufe(Klassenstufe.EINS).withRohdaten(createRohdatenKlasseEINS())
				.withPunkte(625).withLaengeKaengurusprung(1);

			Loesungszettel vonKindReferenzierterLoesungszettel = new Loesungszettel().withIdentifier(REQUEST_KIND_ID).withVersion(0)
				.withKindID(REQUEST_KIND_ID).withKlassenstufe(Klassenstufe.EINS).withRohdaten(createRohdatenKlasseEINS())
				.withPunkte(625).withLaengeKaengurusprung(1).withTeilnahmeIdentifier(teilnahmeIdentifier);

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(kindLoesungszettelID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());
			when(loesungszettelRepository.ofID(kind.loesungszettelID()))
				.thenReturn(Optional.of(vonKindReferenzierterLoesungszettel));
			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);

			// Act
			try {

				service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();

				MessagePayload messagePayload = responsePayload.getMessage();
				assertEquals("ERROR", messagePayload.getLevel());
				assertEquals(
					"Der Lösungszettel konnte leider nicht gespeichert werden: es gibt inkonsistente Daten in der Datenbank. Bitte wenden Sie sich per Mail an info@egladil.de.",
					messagePayload.getMessage());

				assertNull(responsePayload.getData());

				verify(wettbewerbService, times(1)).aktuellerWettbewerb();
				verify(loesungszettelRepository, times(2)).ofID(any());
				verify(kinderRepository, times(1)).ofId(any());
				verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());

				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));
			}

		}
	}

	@Nested
	class KindUndLoesungszettelExistierenTests {
		@Test
		@DisplayName("Veranstalter nicht berechtigt => 401")
		void should_loesungszettelAendernThrowAccessDeniedException_when_notPermitted() {

			// Arrange
			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(REQUEST_LOESUNGSZETTEL_ID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any()))
				.thenThrow(new AccessDeniedException("nö"));

			requestDaten = requestDaten.withVersion(3);

			// Act
			try {

				service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
				fail("keine AccessDeniedException");
			} catch (AccessDeniedException e) {

				assertEquals("nö", e.getMessage());

				verify(wettbewerbService, times(1)).aktuellerWettbewerb();
				verify(loesungszettelRepository, times(1)).ofID(any());
				verify(kinderRepository, times(1)).ofId(any());
				verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());

				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));
			}

		}

		@Test
		@DisplayName("(IT erforderlich) kind.lzID == null, loesungszettel existiert, und zeigt auf gleiches Kind wie der Request, kein konkurrierender Update => ändern und update kind")
		void should_loesungszettelAendernUpdateKindToo_when_kindLoesungszettelIDNullAndNotInkonsistenteDaten() {

			// Arrange

			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier).withVersion(3).withKlassenstufe(Klassenstufe.EINS)
				.withRohdaten(createRohdatenKlasseEINS()).withPunkte(625).withLaengeKaengurusprung(1)
				.withRohdaten(createRohdatenKlasseEINS());

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(loesungszettelRepository.updateLoesungszettel(loesungszettel)).thenReturn(loesungszettel);
			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(kinderRepository.changeKind(kind)).thenReturn(Boolean.TRUE);

			requestDaten = requestDaten.withVersion(3);

			// Act
			ResponsePayload responsePayload = service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);

			// Assert
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals(
				"Der Lösungszettel wurde erfolgreich gespeichert: Punkte 6,25, Länge Kängurusprung 1.",
				messagePayload.getMessage());

			LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) responsePayload.getData();

			assertEquals(1, result.laengeKaengurusprung());
			assertEquals("6,25", result.punkte());
			assertEquals(REQUEST_LOESUNGSZETTEL_ID.identifier(), result.loesungszettelId());
			List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

			assertEquals(zeilen.size(), actualZeilen.size());

			for (int i = 0; i < zeilen.size(); i++) {

				assertEquals(zeilen.get(i), actualZeilen.get(i), "Fehler bei Index= " + i);
			}

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

			verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(1)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
			verify(kinderRepository, times(1)).changeKind(any());

			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelCreated.class));
			verify(domainEventHandler).handleEvent(any(LoesungszettelChanged.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));

			// verify kind.loesungszettelID = request.loesungszettelID

		}

		@Test
		@DisplayName("(IT erforderlich) kind.lzID == null, loesungszettel existiert, und zeigt auf gleiches Kind wie der Request, konkurrierendes Update => update kind, aber nicht Lösungszettel")
		void should_loesungszettelAendernUpdateKindButNotChangeLoesungszettel_when_kindLoesungszettelIDNullAndReferencedLoesungszettelAktueller() {

			// Arrange

			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier).withVersion(3).withKlassenstufe(Klassenstufe.EINS)
				.withRohdaten(createRohdatenKlasseEINS()).withPunkte(625).withLaengeKaengurusprung(1);

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(loesungszettelRepository.updateLoesungszettel(loesungszettel))
				.thenThrow(new EntityConcurrentlyModifiedException(ConcurrentModificationType.UPDATED, loesungszettel));
			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(kinderRepository.changeKind(kind)).thenReturn(Boolean.TRUE);

			requestDaten = requestDaten.withVersion(2);

			// Act
			ResponsePayload responsePayload = service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);

			// Assert
			LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) responsePayload.getData();

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Ein Lösungszettel für dieses Kind wurde bereits durch eine andere Person gespeichert. Bitte prüfen Sie die neuen Daten. Punkte 6,25, Länge Kängurusprung 1.",
				messagePayload.getMessage());

			assertEquals(1, result.laengeKaengurusprung());
			assertEquals("6,25", result.punkte());
			assertEquals(REQUEST_LOESUNGSZETTEL_ID.identifier(), result.loesungszettelId());
			assertEquals(ConcurrentModificationType.UPDATED, result.getConcurrentModificationType());
			List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

			assertEquals(zeilen.size(), actualZeilen.size());

			for (int i = 0; i < zeilen.size(); i++) {

				assertEquals(zeilen.get(i), actualZeilen.get(i), "Fehler bei Index= " + i);
			}

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

			verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(1)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
			verify(kinderRepository, times(1)).changeKind(any());

			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelCreated.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));
		}

		@Test
		@DisplayName("kind.lzID == null, loesungszettel.kindID != null, zeigt aber auf anaderes Kind als der Request => 422 inkonsistente Daten")
		void should_loesungszettelAendernThrowInkonsistentInputException_when_kindLoesungszettelIDNullAndInkonsistenteDaten() {

			// Arrange
			Identifier anderesKindID = new Identifier("anderes-kind-uuid");

			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(anderesKindID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			Kind kind = new Kind(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(TeilnahmeIdentifierAktuellerWettbewerb.createForSchulteilnahme(TEILNAHMENUMMER));

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));

			// Act
			try {

				service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
				fail("keine InvalidInputException");
			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();
				MessagePayload messagePayload = responsePayload.getMessage();
				assertEquals("ERROR", messagePayload.getLevel());
				assertEquals(
					"Der Lösungszettel konnte leider nicht gespeichert werden: es gibt inkonsistente Daten in der Datenbank. Bitte wenden Sie sich per Mail an info@egladil.de.",
					messagePayload.getMessage());

				assertNull(responsePayload.getData());

				verify(wettbewerbService, times(1)).aktuellerWettbewerb();
				verify(loesungszettelRepository, times(1)).ofID(any());
				verify(kinderRepository, times(1)).ofId(any());
				verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());

				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));
			}

		}

		@Test
		@DisplayName("kind.lzID == null, loesungszettel.kindID == null => 422 inkonsistente Daten")
		void should_loesungszettelAendernThrowInkonsistentInputException_when_kindLoesungszettelIDNullAndLoesungszettelOhneKind() {

			// Arrange
			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			Kind kind = new Kind(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(TeilnahmeIdentifierAktuellerWettbewerb.createForSchulteilnahme(TEILNAHMENUMMER));

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));

			// Act
			try {

				service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
				fail("keine InvalidInputException");
			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();
				MessagePayload messagePayload = responsePayload.getMessage();
				assertEquals("ERROR", messagePayload.getLevel());
				assertEquals(
					"Der Lösungszettel konnte leider nicht gespeichert werden: es gibt inkonsistente Daten in der Datenbank. Bitte wenden Sie sich per Mail an info@egladil.de.",
					messagePayload.getMessage());

				assertNull(responsePayload.getData());

				verify(wettbewerbService, times(1)).aktuellerWettbewerb();
				verify(loesungszettelRepository, times(1)).ofID(any());
				verify(kinderRepository, times(1)).ofId(any());
				verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());

				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));
			}

		}

		@Test
		@DisplayName("kind.lzID != null, anderer loesungszettel mit kind.lzId existiert, lz.kindID != kindID => Abbruch mit 422 inkonsistente Daten")
		void should_loesungszettelAendernThrowInvalidInputException_when_kindPresentLoesungszettelAbsent_andKindMitReferenzAufAnderenLZ() {

			// Arrange
			Identifier kindLoesungszettelID = new Identifier("kind-loesungszettel-uuid");
			Identifier loesungszettelKindID = new Identifier("loesungszettel-kind-uuid");

			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(kindLoesungszettelID)
				.withKindID(loesungszettelKindID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(kindLoesungszettelID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));

			// Act
			try {

				service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
				fail("keine InvalidInputException");
			} catch (InvalidInputException e) {

				ResponsePayload responsePayload = e.getResponsePayload();

				MessagePayload messagePayload = responsePayload.getMessage();
				assertEquals("ERROR", messagePayload.getLevel());
				assertEquals(
					"Der Lösungszettel konnte leider nicht gespeichert werden: es gibt inkonsistente Daten in der Datenbank. Bitte wenden Sie sich per Mail an info@egladil.de.",
					messagePayload.getMessage());

				assertNull(responsePayload.getData());

				assertNull(e.getMessage());

				verify(wettbewerbService, times(1)).aktuellerWettbewerb();
				verify(loesungszettelRepository, times(1)).ofID(any());
				verify(kinderRepository, times(1)).ofId(any());
				verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
				verify(kinderRepository, times(0)).changeKind(any());

				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelCreated.class));
				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
				verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));
			}
		}

		@Test
		@DisplayName("kind.lzID == requestData.loesungszettel == loesungszettel.uuid, loesungszettel.kindID = kind.uuid => ändern")
		void should_loesungszettelAendernWork() {

			// Arrange
			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier).withVersion(2);

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(REQUEST_LOESUNGSZETTEL_ID);

			Loesungszettel geaenderter = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier).withVersion(3).withKlassenstufe(Klassenstufe.EINS)
				.withRohdaten(createRohdatenKlasseEINS()).withPunkte(625).withLaengeKaengurusprung(1);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.updateLoesungszettel(loesungszettel)).thenReturn(geaenderter);

			// Act
			ResponsePayload responsePayload = service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("INFO", messagePayload.getLevel());
			assertEquals("Der Lösungszettel wurde erfolgreich gespeichert: Punkte 6,25, Länge Kängurusprung 1.",
				messagePayload.getMessage());

			LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) responsePayload.getData();

			assertEquals(1, result.laengeKaengurusprung());
			assertEquals("6,25", result.punkte());
			assertEquals(REQUEST_LOESUNGSZETTEL_ID.identifier(), result.loesungszettelId());
			assertNull(result.getConcurrentModificationType());
			List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

			assertEquals(zeilen.size(), actualZeilen.size());

			for (int i = 0; i < zeilen.size(); i++) {

				assertEquals(zeilen.get(i), actualZeilen.get(i), "Fehler bei Index= " + i);
			}

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

			verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(1)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
			verify(kinderRepository, times(1)).changeKind(any());

			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelCreated.class));
			verify(domainEventHandler).handleEvent(any(LoesungszettelChanged.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));
		}

		@Test
		@DisplayName("kind.lzID == requestData.loesungszettel == loesungszettel.uuid, loesungszettel.kindID = kind.uuid, loesungszettel.version neuer => nicht ändern")
		void should_loesungszettelNichtAendern_when_konkurrierendesUpdate() {

			// Arrange
			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier).withVersion(2);

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(REQUEST_LOESUNGSZETTEL_ID);

			Loesungszettel neuerer = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier).withVersion(3).withKlassenstufe(Klassenstufe.EINS)
				.withRohdaten(createRohdatenKlasseEINS()).withPunkte(625).withLaengeKaengurusprung(1);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.updateLoesungszettel(loesungszettel))
				.thenThrow(new EntityConcurrentlyModifiedException(ConcurrentModificationType.UPDATED, neuerer));

			// Act
			ResponsePayload responsePayload = service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
			LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) responsePayload.getData();

			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals(
				"Ein Lösungszettel für dieses Kind wurde bereits durch eine andere Person gespeichert. Bitte prüfen Sie die neuen Daten. Punkte 6,25, Länge Kängurusprung 1.",
				messagePayload.getMessage());

			assertEquals(1, result.laengeKaengurusprung());
			assertEquals("6,25", result.punkte());
			assertEquals(REQUEST_LOESUNGSZETTEL_ID.identifier(), result.loesungszettelId());
			assertEquals(ConcurrentModificationType.UPDATED, result.getConcurrentModificationType());
			List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

			assertEquals(zeilen.size(), actualZeilen.size());

			for (int i = 0; i < zeilen.size(); i++) {

				assertEquals(zeilen.get(i), actualZeilen.get(i), "Fehler bei Index= " + i);
			}

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

			verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(1)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
			verify(kinderRepository, times(0)).changeKind(any());

			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelCreated.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelChanged.class));
			verify(domainEventHandler, never()).handleEvent(any(LoesungszettelDeleted.class));
		}

	}

}
