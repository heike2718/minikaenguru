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
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * LoesungszettelAnlegenTest
 */
@ExtendWith(MockitoExtension.class)
public class LoesungszettelAnlegenTest extends AbstractLoesungszettelServiceTest {

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

		this.requestDaten = new LoesungszettelAPIModel().withKindID(REQUEST_KIND_ID.identifier()).withUuid("neu")
			.withKlassenstufe(Klassenstufe.EINS).withZeilen(zeilen);
	}

	@Test
	@DisplayName("kind mit kindID existiert, hat lzID, LZ mit lzID existiert und hat gleiche kindID => concurrent insert")
	void should_loesungszettelAnlegenReturnTheConcurrentlySavedEntity_when_concurrentInsert() {

		// Arrange
		Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(REQUEST_LOESUNGSZETTEL_ID);

		Loesungszettel vorhandener = vorhandenerLoesungszettelOhneIDs.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
			.withKindID(REQUEST_KIND_ID);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
		when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(vorhandener));
		when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);

		// Act
		ResponsePayload responsePayload = service.loesungszettelAnlegen(requestDaten, VERANSTALTER_ID);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals(
			"Ein Lösungszettel für dieses Kind wurde bereits durch eine andere Person gespeichert. Bitte prüfen Sie die neuen Daten. Punkte 50,00, Länge Kängurusprung 5.",
			messagePayload.getMessage());

		assertNotNull(responsePayload.getData());

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

	@Test
	@DisplayName("kind mit kindID existiert nicht => 404")
	void should_loesungszettelAnlegenThrowNotFoundException_when_noKindPresent() {

		// Arrange
		when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.empty());

		// Act
		try {

			service.loesungszettelAnlegen(requestDaten, VERANSTALTER_ID);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			verify(wettbewerbService, times(0)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(0)).ofID(any());
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
	@DisplayName("Veranstalter nicht berechtigt => 401")
	void should_loesungszettelAnlegenThrowAccessDeniedException_when_notPermitted() {

		// Arrange
		Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
		when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenThrow(new AccessDeniedException("nö"));

		// Act
		try {

			service.loesungszettelAnlegen(requestDaten, VERANSTALTER_ID);
			fail("keine NotFoundException");
		} catch (AccessDeniedException e) {

			assertEquals("nö", e.getMessage());

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(0)).ofID(any());
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
	@DisplayName("kind mit kindID existiert, hat lzID, LZ mit lzID existiert nicht => anlegen")
	void should_loesungszettelAnlegenInsertTheLoesungszettel_when_kindLoesungszettelIdNotNullButLoesungszettelAbsent() {

		// Arrange

		String neueLoesungszettelUuid = "ashdihqo";

		Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(REQUEST_LOESUNGSZETTEL_ID);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
		when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());
		when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
		when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(new Identifier(neueLoesungszettelUuid));

		// Act
		ResponsePayload result = service.loesungszettelAnlegen(requestDaten, VERANSTALTER_ID);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals(
			"Der Lösungszettel wurde erfolgreich gespeichert: Punkte 6,25, Länge Kängurusprung 1.",
			messagePayload.getMessage());
		assertEquals("INFO", messagePayload.getLevel());
		assertNotNull(result.getData());

		LoesungszettelpunkteAPIModel entity = (LoesungszettelpunkteAPIModel) result.getData();

		assertEquals(neueLoesungszettelUuid, entity.loesungszettelId());
		List<LoesungszettelZeileAPIModel> actualZeilen = entity.zeilen();

		assertEquals(zeilen.size(), actualZeilen.size());

		for (int i = 0; i < zeilen.size(); i++) {

			assertEquals("Fehler bei Index= " + i, zeilen.get(i), actualZeilen.get(i));
		}

		verify(wettbewerbService, times(1)).aktuellerWettbewerb();
		verify(loesungszettelRepository, times(1)).ofID(any());
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
	@DisplayName("kind mit kindID existiert, hat lzID, LZ mit lzID existiert und hat andere kindID => Abbruch mit 422")
	void should_loesungszettelAnlegenInvalidInputException_when_datenInkonsistent() {

		// Arrange
		String uuidAnderesKind = "kljgdq";

		Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID).withLoesungszettelID(REQUEST_LOESUNGSZETTEL_ID);

		LoesungszettelRohdaten rohdaten = new LoesungszettelRohdaten().withNutzereingabe("EBCACCBDBNBN");

		Loesungszettel vorhandener = new Loesungszettel(REQUEST_LOESUNGSZETTEL_ID).withKindID(new Identifier(uuidAnderesKind))
			.withPunkte(5000)
			.withLaengeKaengurusprung(5).withRohdaten(rohdaten).withKlassenstufe(Klassenstufe.EINS);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
		when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(vorhandener));
		when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);

		// Act
		try {

			service.loesungszettelAnlegen(requestDaten, VERANSTALTER_ID);
			fail("keine InvalidInputException");
		} catch (InvalidInputException e) {

			ResponsePayload responsePayload = e.getResponsePayload();
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("ERROR", messagePayload.getLevel());
			assertEquals(
				"Der Lösungszettel konnte leider nicht gespeichert werden: es gibt inkonsistente Daten in der Datenbank. Bitte wenden Sie sich per Mail an info@egladil.de.",
				messagePayload.getMessage());

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
	@DisplayName("requestDaten.kindID null => Abbruch mit 422")
	void should_loesungszettelAnlegenThrowInvalidInputException_when_kindIDNull() {

		// Arrange
		LoesungszettelAPIModel datenOhneKindId = new LoesungszettelAPIModel().withUuid("neu");

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
	@DisplayName("kind mit kindID existiert, hat keine lzID => anlegen")
	void should_loesungszettelAnlegenWork() {

		// Arrange
		Identifier neueLoesungszettelID = new Identifier("ahhqhiohqi");

		Kind kind = kindOhneIDs.withIdentifier(REQUEST_KIND_ID);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(kinderRepository.ofId(REQUEST_KIND_ID)).thenReturn(Optional.of(kind));
		when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);

		when(kinderRepository.changeKind(kind)).thenReturn(Boolean.TRUE);
		when(loesungszettelRepository.addLoesungszettel(any())).thenReturn(neueLoesungszettelID);

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

		assertEquals(neueLoesungszettelID.identifier(), result.loesungszettelId());
		List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

		assertEquals(zeilen.size(), actualZeilen.size());

		for (int i = 0; i < zeilen.size(); i++) {

			assertEquals("Fehler bei Index= " + i, zeilen.get(i), actualZeilen.get(i));
		}

		verify(wettbewerbService, times(1)).aktuellerWettbewerb();
		verify(loesungszettelRepository, times(0)).ofID(any());
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

}
