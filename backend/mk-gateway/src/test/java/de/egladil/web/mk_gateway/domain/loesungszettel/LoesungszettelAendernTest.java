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

import java.util.List;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * LoesungszettelAendernTest
 */
@ExtendWith(MockitoExtension.class)
public class LoesungszettelAendernTest extends AbstractLoesungszettelServiceTest {

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
				verify(authService, times(0)).checkPermissionForTeilnahmenummer(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any(), any());
				verify(kinderRepository, times(0)).changeKind(any());

				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());
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
				verify(authService, times(0)).checkPermissionForTeilnahmenummer(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any(), any());
				verify(kinderRepository, times(0)).changeKind(any());

				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());
			}
		}
	}

	@Nested
	class KindExistiertNichtTests {

		@Test
		@DisplayName("3) loesungszettel existiert: loesungszettel.jahr == aktuelles Jahr, loesungszettel.kindID != null =>  => deleteLösungszettel und 404")
		void should_loesungszettelAendernTriggerDeleteEventAndThrowNotFoundException_when_kindAbsentAndLoesungszettelPresent() {

			// Arrange

			Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.ONLINE)
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID).withKlassenstufe(Klassenstufe.EINS)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(loesungszettelRepository.removeLoesungszettel(REQUEST_LOESUNGSZETTEL_ID,
				VERANSTALTER_ID.identifier())).thenReturn(Optional.of(persistenterLoesungszettel));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.empty());

			// Act
			try {

				service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				verify(wettbewerbService, times(1)).aktuellerWettbewerb();
				verify(loesungszettelRepository, times(1)).ofID(any());
				verify(kinderRepository, times(1)).ofId(any());
				verify(authService, times(0)).checkPermissionForTeilnahmenummer(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(1)).removeLoesungszettel(any(), any());
				verify(kinderRepository, times(0)).changeKind(any());

				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNotNull(service.getLoesungszettelDeleted());
			}

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
				verify(authService, times(0)).checkPermissionForTeilnahmenummer(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any(), any());
				verify(kinderRepository, times(0)).changeKind(any());

				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());
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
			try {

				service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				verify(wettbewerbService, times(1)).aktuellerWettbewerb();
				verify(loesungszettelRepository, times(1)).ofID(any());
				verify(kinderRepository, times(1)).ofId(any());
				verify(authService, times(0)).checkPermissionForTeilnahmenummer(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any(), any());
				verify(kinderRepository, times(0)).changeKind(any());

				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());
			}

		}
	}

	@Nested
	class KindExistiertLoesungszettelExistiertNichtTests {

		@Test
		@DisplayName("2) kind.lzID null, anderer loesungszettel existiert nicht => anlegen")
		void should_loesungszettelAendernSwitchToAnlegen_when_kindPresentLoesungszettelAbsent_andKindOhneLoesungszettelreferenz() {

			// Arrange
			Identifier neueLoesungszettelID = new Identifier("neueLoesungszettelID");
			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());
			when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(neueLoesungszettelID);

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

				assertEquals("Fehler bei Index= " + i, zeilen.get(i), actualZeilen.get(i));
			}

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(1)).ofID(REQUEST_LOESUNGSZETTEL_ID);
			verify(loesungszettelRepository, times(1)).ofID(null);
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

			verify(loesungszettelRepository, times(1)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any(), any());
			verify(kinderRepository, times(1)).changeKind(any());

			assertNotNull(service.getLoesungszettelCreated());
			assertNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());
		}

		@Test
		@DisplayName("3) kindLz != null, referencedLZ null, requestedLz == kindLz, exists lz with kindId = kind => existing loesungszettel ändern und kind.lzID ändern")
		void should_loesungszettelAendernAendertReferenziertenLoesungszettel_when_referencedLoesungszettelAbsentReuestLoesungszettelAbsentLoesungszettelMitKindIDExists() {

			// Arrange
			Identifier kindLoesungszettelID = new Identifier("kind-loesungszettel-uuid");

			Loesungszettel andererLoesungszettel = new Loesungszettel()
				.withIdentifier(kindLoesungszettelID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(kindLoesungszettelID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());
			when(loesungszettelRepository.ofID(kindLoesungszettelID)).thenReturn(Optional.empty());
			when(loesungszettelRepository.findLoesungszettelWithKindID(kind.identifier()))
				.thenReturn(Optional.of(andererLoesungszettel));
			when(loesungszettelRepository.updateLoesungszettel(any())).thenReturn(Boolean.TRUE);

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
			assertEquals(kindLoesungszettelID.identifier(), result.loesungszettelId());
			List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

			assertEquals(zeilen.size(), actualZeilen.size());

			for (int i = 0; i < zeilen.size(); i++) {

				assertEquals("Fehler bei Index= " + i, zeilen.get(i), actualZeilen.get(i));
			}

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(2)).ofID(any());
			verify(loesungszettelRepository, times(1)).findLoesungszettelWithKindID(kind.identifier());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

			verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(1)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any(), any());
			verify(kinderRepository, times(1)).changeKind(any());

			assertNull(service.getLoesungszettelCreated());
			assertNotNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());
		}

		@Test
		@DisplayName("1) kind.lzID != null, anderer loesungszettel mit kind.lzId existiert nicht => anlegen")
		void should_loesungszettelAendernSwitchToAnlegen_when_kindPresentLoesungszettelAbsent_andLoesungszettelMitkindLzIDAbsent() {

			// Arrange

			Identifier kindLoesungszettelID = new Identifier("kind-loesungszettel-uuid");

			Identifier neueLoesungszettelID = new Identifier("neueLoesungszettelID");

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(kindLoesungszettelID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());
			when(loesungszettelRepository.ofID(kindLoesungszettelID)).thenReturn(Optional.empty());
			when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(neueLoesungszettelID);

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

				assertEquals("Fehler bei Index= " + i, zeilen.get(i), actualZeilen.get(i));
			}

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(2)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

			verify(loesungszettelRepository, times(1)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any(), any());
			verify(kinderRepository, times(1)).changeKind(any());

			assertNotNull(service.getLoesungszettelCreated());
			assertNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());

			// verify kind.loesungszettelID = neueLoesungszettelID
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
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenThrow(new AccessDeniedException("nö"));

			// Act
			try {

				service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
				fail("keine AccessDeniedException");
			} catch (AccessDeniedException e) {

				assertEquals("nö", e.getMessage());

				verify(wettbewerbService, times(1)).aktuellerWettbewerb();
				verify(loesungszettelRepository, times(1)).ofID(any());
				verify(kinderRepository, times(1)).ofId(any());
				verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any(), any());
				verify(kinderRepository, times(0)).changeKind(any());

				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());
			}

		}

		@Test
		@DisplayName("(IT erforderlich) kind.lzID == null, loesungszettel existiert, und zeigt auf gleiches Kind wie der Request => ändern und update kind")
		void should_loesungszettelAendernUpdateKindToo_when_kindLoesungszettelIDNullAndNotInkonsistenteDaten() {

			// Arrange

			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(kinderRepository.changeKind(kind)).thenReturn(Boolean.TRUE);

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

				assertEquals("Fehler bei Index= " + i, zeilen.get(i), actualZeilen.get(i));
			}

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

			verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(1)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any(), any());
			verify(kinderRepository, times(1)).changeKind(any());

			assertNull(service.getLoesungszettelCreated());
			assertNotNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());

			// verify kind.loesungszettelID = request.loesungszettelID

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
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
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
				verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any(), any());
				verify(kinderRepository, times(0)).changeKind(any());

				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());
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
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
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
				verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any(), any());
				verify(kinderRepository, times(0)).changeKind(any());

				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());
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
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());
			when(loesungszettelRepository.ofID(kindLoesungszettelID)).thenReturn(Optional.of(loesungszettel));

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
				verify(loesungszettelRepository, times(2)).ofID(any());
				verify(kinderRepository, times(1)).ofId(any());
				verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

				verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(loesungszettelRepository, times(0)).removeLoesungszettel(any(), any());
				verify(kinderRepository, times(0)).changeKind(any());

				assertNull(service.getLoesungszettelCreated());
				assertNull(service.getLoesungszettelChanged());
				assertNull(service.getLoesungszettelDeleted());
			}
		}

		@Test
		@DisplayName("kind.lzID == requestData.loesungszettel == loesungszettel.uuid, loesungszettel.kindID = kind.uuid => ändern")
		void should_loesungszettelAendernWork() {

			// Arrange
			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

			Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(REQUEST_LOESUNGSZETTEL_ID);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
			when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
			when(loesungszettelRepository.updateLoesungszettel(loesungszettel)).thenReturn(Boolean.TRUE);

			// Act
			ResponsePayload responsePayload = service.loesungszettelAendern(requestDaten, VERANSTALTER_ID);
			LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) responsePayload.getData();

			assertEquals(1, result.laengeKaengurusprung());
			assertEquals("6,25", result.punkte());
			assertEquals(REQUEST_LOESUNGSZETTEL_ID.identifier(), result.loesungszettelId());
			List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

			assertEquals(zeilen.size(), actualZeilen.size());

			for (int i = 0; i < zeilen.size(); i++) {

				assertEquals("Fehler bei Index= " + i, zeilen.get(i), actualZeilen.get(i));
			}

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

			verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(1)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any(), any());
			verify(kinderRepository, times(1)).changeKind(any());

			assertNull(service.getLoesungszettelCreated());
			assertNotNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());
		}

	}

}
