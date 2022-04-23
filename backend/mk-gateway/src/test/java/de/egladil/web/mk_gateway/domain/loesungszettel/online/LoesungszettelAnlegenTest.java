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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import de.egladil.web.mk_gateway.domain.error.ConcurrentModificationType;
import de.egladil.web.mk_gateway.domain.error.EntityConcurrentlyModifiedException;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.AuswertungsmodusInfoService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.api.Auswertungsmodus;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * LoesungszettelAnlegenTest
 */
@ExtendWith(MockitoExtension.class)
public class LoesungszettelAnlegenTest extends AbstractLoesungszettelServiceTest {

	/**
	 *
	 */
	private static final String NEU = "neu";

	@Mock
	private KinderRepository kinderRepository;

	@Mock
	private LoesungszettelRepository loesungszettelRepository;

	@Mock
	private AuthorizationService authService;

	@Mock
	private WettbewerbService wettbewerbService;

	@Mock
	private AuswertungsmodusInfoService auswertungsmodusInfoService;

	@InjectMocks
	private OnlineLoesungszettelService service;

	@BeforeEach
	void setUp() {

		super.init();

		this.requestDaten = new LoesungszettelAPIModel().withKindID(REQUEST_KIND_ID.identifier()).withUuid(NEU)
			.withKlassenstufe(Klassenstufe.EINS).withZeilen(zeilen);
	}

	@Test
	@DisplayName("5) kind mit kindID existiert, hat lzID, LZ mit lzID existiert und hat gleiche kindID => concurrent insert")
	void should_loesungszettelAnlegenReturnTheConcurrentlySavedEntity_when_concurrentInsert() {

		// Arrange
		Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(REQUEST_LOESUNGSZETTEL_ID);

		Loesungszettel vorhandener = vorhandenerLoesungszettelOhneIDs.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
			.withKindID(REQUEST_KIND_ID).withVersion(0).withRohdaten(createRohdatenKlasseEINS()).withPunkte(5000)
			.withLaengeKaengurusprung(5);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
		when(loesungszettelRepository.addLoesungszettel(any()))
			.thenThrow(new EntityConcurrentlyModifiedException(ConcurrentModificationType.INSERTED, vorhandener));

		when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);

		requestDaten = requestDaten.withUuid(NEU);

		// Act
		ResponsePayload responsePayload = service.loesungszettelAnlegen(requestDaten, VERANSTALTER_ID);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals(
			"Ein Lösungszettel für dieses Kind wurde bereits durch eine andere Person gespeichert. Bitte prüfen Sie die neuen Daten. Punkte 50,00, Länge Kängurusprung 5.",
			messagePayload.getMessage());

		assertNotNull(responsePayload.getData());

		LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) responsePayload.getData();
		assertEquals(REQUEST_LOESUNGSZETTEL_ID.identifier(), responseData.loesungszettelId());
		assertEquals(ConcurrentModificationType.INSERTED, responseData.getConcurrentModificationType());

		verify(wettbewerbService, times(1)).aktuellerWettbewerb();
		verify(loesungszettelRepository, times(0)).ofID(any());
		verify(kinderRepository, times(1)).ofId(any());
		verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

		verify(loesungszettelRepository, times(1)).addLoesungszettel(any());
		verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
		verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
		verify(kinderRepository, times(0)).changeKind(any());

		assertNull(service.getLoesungszettelCreated());
		assertNull(service.getLoesungszettelChanged());
		assertNull(service.getLoesungszettelDeleted());

	}

	@Test
	@DisplayName("2) kind mit kindID existiert nicht => 404")
	void should_loesungszettelAnlegenThrowNotFoundException_when_noKindPresent() {

		// Arrange
		when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.empty());

		requestDaten = requestDaten.withUuid(NEU);

		// Act
		try {

			service.loesungszettelAnlegen(requestDaten, VERANSTALTER_ID);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			verify(wettbewerbService, times(0)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(0)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(0)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

			verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
			verify(kinderRepository, times(0)).changeKind(any());

			assertNull(service.getLoesungszettelCreated());
			assertNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());
		}

	}

	@Test
	@DisplayName("0) Es gibt bereits hochgeladene Auswertung")
	void should_loesungszettelAnlegenThrowActionNotAuthorized_when_AuswertungHochgeladen() {

		// Arrange
		Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withTeilnahmeIdentifier(teilnahmeIdentifierKind);
		requestDaten = requestDaten.withUuid(NEU);

		// Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.UPLOAD);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
		when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);
		// when(loesungszettelRepository.loadAll(any())).thenReturn(Collections.singletonList(loesungszettel));

		when(auswertungsmodusInfoService.ermittleAuswertungsmodusFuerTeilnahme(teilnahmeIdentifier))
			.thenReturn(Auswertungsmodus.OFFLINE);

		ResponsePayload responsePayload = service.loesungszettelAnlegen(requestDaten, VERANSTALTER_ID);

		MessagePayload messagePayload = responsePayload.getMessage();

		assertEquals("Der Lösungszettel konnte leider nicht gespeichert werden, da bereits Auswertungen hochgeladen wurden.",
			messagePayload.getMessage());
		assertEquals("WARN", messagePayload.getLevel());

		verify(wettbewerbService, times(1)).aktuellerWettbewerb();
		verify(loesungszettelRepository, times(0)).ofID(any());
		verify(kinderRepository, times(1)).ofId(any());
		verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

		verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
		verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
		verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
		verify(kinderRepository, times(0)).changeKind(any());

		assertNull(service.getLoesungszettelCreated());
		assertNull(service.getLoesungszettelChanged());
		assertNull(service.getLoesungszettelDeleted());

	}

	@Test
	@DisplayName("1) Veranstalter nicht berechtigt => 401")
	void should_loesungszettelAnlegenThrowAccessDeniedException_when_notPermitted() {

		// Arrange
		Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID);
		requestDaten = requestDaten.withUuid(NEU);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
		when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any()))
			.thenThrow(new AccessDeniedException("nö"));

		// Act
		try {

			service.loesungszettelAnlegen(requestDaten, VERANSTALTER_ID);
			fail("keine NotFoundException");
		} catch (AccessDeniedException e) {

			assertEquals("nö", e.getMessage());

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(0)).ofID(any());
			verify(kinderRepository, times(1)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

			verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
			verify(kinderRepository, times(0)).changeKind(any());

			assertNull(service.getLoesungszettelCreated());
			assertNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());
		}

	}

	@Test
	@DisplayName("6) kind mit kindID existiert, hat lzID, LZ mit lzID existiert nicht => anlegen")
	void should_loesungszettelAnlegenInsertTheLoesungszettel_when_kindLoesungszettelIdNotNullButLoesungszettelAbsent() {

		// Arrange

		String neueLoesungszettelUuid = "ashdihqo";
		requestDaten = requestDaten.withUuid(NEU).withVersion(0);

		Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(REQUEST_LOESUNGSZETTEL_ID);

		Loesungszettel neuerLoesungszettel = new Loesungszettel().withIdentifier(new Identifier(neueLoesungszettelUuid))
			.withVersion(0)
			.withKindID(kind.identifier()).withRohdaten(createRohdatenKlasseEINS()).withKlassenstufe(Klassenstufe.EINS)
			.withPunkte(625).withLaengeKaengurusprung(1);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
		when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);
		when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(neuerLoesungszettel);

		// Act
		ResponsePayload result = service.loesungszettelAnlegen(requestDaten, VERANSTALTER_ID);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals(
			"Der Lösungszettel wurde erfolgreich gespeichert: Punkte 6,25, Länge Kängurusprung 1.",
			messagePayload.getMessage());
		assertEquals("INFO", messagePayload.getLevel());
		assertNotNull(result.getData());

		LoesungszettelpunkteAPIModel responseData = (LoesungszettelpunkteAPIModel) result.getData();
		assertNull(responseData.getConcurrentModificationType());

		assertEquals(neueLoesungszettelUuid, responseData.loesungszettelId());
		List<LoesungszettelZeileAPIModel> actualZeilen = responseData.zeilen();

		assertEquals(zeilen.size(), actualZeilen.size());

		for (int i = 0; i < zeilen.size(); i++) {

			assertEquals(zeilen.get(i), actualZeilen.get(i), "Fehler bei Index= " + i);
		}

		verify(wettbewerbService, times(1)).aktuellerWettbewerb();
		verify(loesungszettelRepository, times(0)).ofID(any());
		verify(kinderRepository, times(1)).ofId(any());
		verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

		verify(loesungszettelRepository, times(1)).addLoesungszettel(any());
		verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
		verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
		verify(kinderRepository, times(1)).changeKind(any());

		assertNotNull(service.getLoesungszettelCreated());
		assertNull(service.getLoesungszettelChanged());
		assertNull(service.getLoesungszettelDeleted());

	}

	@Test
	@DisplayName("requestDaten.kindID null => Abbruch mit 422")
	void should_loesungszettelAnlegenThrowInvalidInputException_when_kindIDNull() {

		// Arrange
		LoesungszettelAPIModel datenOhneKindId = new LoesungszettelAPIModel().withUuid(NEU);

		// Act
		try {

			service.loesungszettelAnlegen(datenOhneKindId, VERANSTALTER_ID);
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

			assertNull(service.getLoesungszettelCreated());
			assertNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());
		}

	}

	@Test
	@DisplayName("3) kind mit kindID existiert, hat keine lzID => anlegen")
	void should_loesungszettelAnlegenWork() {

		// Arrange
		Identifier neueLoesungszettelID = new Identifier("ahhqhiohqi");
		requestDaten = requestDaten.withUuid(NEU);

		Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID);

		Loesungszettel neuerLoesungszettel = new Loesungszettel().withIdentifier(neueLoesungszettelID).withVersion(0)
			.withKindID(kind.identifier()).withRohdaten(createRohdatenKlasseEINS()).withKlassenstufe(Klassenstufe.EINS)
			.withPunkte(625).withLaengeKaengurusprung(1);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
		when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any())).thenReturn(Rolle.LEHRER);

		when(kinderRepository.changeKind(kind)).thenReturn(Boolean.TRUE);
		when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(neuerLoesungszettel);

		// Act
		ResponsePayload responsePayload = service.loesungszettelAnlegen(requestDaten, VERANSTALTER_ID);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("INFO", messagePayload.getLevel());
		assertEquals(
			"Der Lösungszettel wurde erfolgreich gespeichert: Punkte 6,25, Länge Kängurusprung 1.",
			messagePayload.getMessage());

		assertNotNull(responsePayload.getData());

		LoesungszettelpunkteAPIModel result = (LoesungszettelpunkteAPIModel) responsePayload.getData();
		assertNull(result.getConcurrentModificationType());

		assertEquals(neueLoesungszettelID.identifier(), result.loesungszettelId());
		List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

		assertEquals(zeilen.size(), actualZeilen.size());

		for (int i = 0; i < zeilen.size(); i++) {

			assertEquals(zeilen.get(i), actualZeilen.get(i), "Fehler bei Index= " + i);
		}

		verify(wettbewerbService, times(1)).aktuellerWettbewerb();
		verify(loesungszettelRepository, times(0)).ofID(any());
		verify(kinderRepository, times(1)).ofId(any());
		verify(authService, times(1)).checkPermissionForTeilnahmenummerAndReturnRolle(any(), any(), any());

		verify(loesungszettelRepository, times(1)).addLoesungszettel(any());
		verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
		verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
		verify(kinderRepository, times(1)).changeKind(any());

		assertNotNull(service.getLoesungszettelCreated());
		assertNull(service.getLoesungszettelChanged());
		assertNull(service.getLoesungszettelDeleted());
	}

}
