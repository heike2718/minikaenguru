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

import java.util.Optional;

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
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * LoesungszettelServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class LoesungszettelLoeschenTest extends AbstractLoesungszettelServiceTest {

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

	@Test
	@DisplayName("loesungszettel existiert nicht")
	void should_loesungszettelLoeschenWithAuthorizationCheckReturnFalse_when_keinLoesungszettel() {

		// Arrange
		when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());

		// Act
		service.loesungszettelLoeschenWithAuthorizationCheck(REQUEST_LOESUNGSZETTEL_ID, VERANSTALTER_ID);

		// Assert
		verify(wettbewerbService, times(0)).aktuellerWettbewerb();
		verify(loesungszettelRepository, times(1)).ofID(any());
		verify(kinderRepository, times(0)).ofId(any());
		verify(authService, times(0)).checkPermissionForTeilnahmenummer(any(), any(), any());

		verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
		verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
		verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
		verify(kinderRepository, times(0)).changeKind(any());

		assertNull(service.getLoesungszettelCreated());
		assertNull(service.getLoesungszettelChanged());
		assertNull(service.getLoesungszettelDeleted());
	}

	@Test
	@DisplayName("nicht autorisiert => 401")
	void should_loesungszettelLoeschenWithAuthorizationCheckThrowAccessDenied_when_notAuthorized() {

		// Arrange

		Loesungszettel loesungszettel = new Loesungszettel()
			.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
			.withKindID(REQUEST_KIND_ID)
			.withTeilnahmeIdentifier(teilnahmeIdentifier);

		when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
		when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenThrow(new AccessDeniedException("nö"));

		// Act
		try {

			service.loesungszettelLoeschenWithAuthorizationCheck(REQUEST_LOESUNGSZETTEL_ID, VERANSTALTER_ID);
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			// Assert
			assertEquals("nö", e.getMessage());

			// Assert
			verify(wettbewerbService, times(0)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(kinderRepository, times(0)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

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
	@DisplayName("loesungszettel existiert: loesungszettel.jahr == vorjahr Jahr => Abbruch und 422")
	void should_loesungszettelLoeschenThrowInvalidInputException_when_LoesungszettelAusVorjahr() {

		// Arrange
		TeilnahmeIdentifier teilnahmeId2027 = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer(teilnahmeIdentifier.teilnahmenummer()).withWettbewerbID(new WettbewerbID(2017));

		Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.ONLINE)
			.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
			.withKindID(REQUEST_KIND_ID).withKlassenstufe(Klassenstufe.EINS)
			.withTeilnahmeIdentifier(teilnahmeId2027);

		PersistenterLoesungszettel aelterer = new PersistenterLoesungszettel();
		aelterer.setWettbewerbUuid("2017");

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));

		// Act
		try {

			service.loesungszettelLoeschenWithAuthorizationCheck(REQUEST_LOESUNGSZETTEL_ID, VERANSTALTER_ID);
			fail("keine InvalidInputException");
		} catch (InvalidInputException e) {

			ResponsePayload responsePayload = e.getResponsePayload();
			MessagePayload messagePayload = responsePayload.getMessage();
			assertEquals("WARN", messagePayload.getLevel());
			assertEquals("Dieser Lösungszettel ist aus dem Jahr 2017. Er kann nicht gelöscht werden.",
				messagePayload.getMessage());

			assertNull(responsePayload.getData());

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(1)).ofID(any());
			verify(loesungszettelRepository, times(0)).findPersistentenLoesungszettel(REQUEST_LOESUNGSZETTEL_ID);
			verify(kinderRepository, times(0)).ofId(any());
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

			verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(REQUEST_LOESUNGSZETTEL_ID);
			verify(loesungszettelRepository, times(0)).removeLoesungszettel(any());
			verify(kinderRepository, times(0)).changeKind(any());

			assertNull(service.getLoesungszettelCreated());
			assertNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());
		}

	}

	@Test
	@DisplayName("loesungszettel existiert, kind mit loesungszettelID existiert nicht => loesungszettel wird gelöscht, ereignis wird propagiert")
	void should_loesungszettelLoeschenWithAuthorizationCheckPropagateEvent_when_LoesungszettelVorhanden() {

		// Arrange
		Loesungszettel loesungszettel = new Loesungszettel()
			.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
			.withKindID(REQUEST_KIND_ID)
			.withTeilnahmeIdentifier(teilnahmeIdentifier);

		persistenterLoesungszettel.setUuid(REQUEST_LOESUNGSZETTEL_ID.identifier());
		persistenterLoesungszettel.setKindID(loesungszettel.kindID().identifier());

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
		when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
		when(loesungszettelRepository.removeLoesungszettel(REQUEST_LOESUNGSZETTEL_ID))
			.thenReturn(Optional.of(persistenterLoesungszettel));
		when(kinderRepository.findKindWithLoesungszettelId(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());

		// Act
		service.loesungszettelLoeschenWithAuthorizationCheck(REQUEST_LOESUNGSZETTEL_ID, VERANSTALTER_ID);

		// Assert
		verify(wettbewerbService, times(1)).aktuellerWettbewerb();
		verify(loesungszettelRepository, times(1)).ofID(any());
		verify(kinderRepository, times(0)).ofId(any());
		verify(kinderRepository, times(1)).findKindWithLoesungszettelId(REQUEST_LOESUNGSZETTEL_ID);
		verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

		verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
		verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
		verify(loesungszettelRepository, times(1)).removeLoesungszettel(REQUEST_LOESUNGSZETTEL_ID);
		verify(kinderRepository, times(0)).changeKind(any());

		assertNull(service.getLoesungszettelCreated());
		assertNull(service.getLoesungszettelChanged());
		assertNotNull(service.getLoesungszettelDeleted());
	}

	@Test
	@DisplayName("loesungszettel existiert, kind mit loesungszettelID existiert => kind wird aktualisiert, loesungszettel wird gelöscht, ereignis wird propagiert")
	void should_loesungszettelLoeschenWithAuthorizationCheckPropagateEvent() {

		// Arrange
		Loesungszettel loesungszettel = new Loesungszettel()
			.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
			.withKindID(REQUEST_KIND_ID)
			.withTeilnahmeIdentifier(teilnahmeIdentifier);

		Kind kind = kindOhneIDs.withLoesungszettelID(REQUEST_LOESUNGSZETTEL_ID).withIdentifier(REQUEST_KIND_ID);

		PersistenterLoesungszettel persistenter = new PersistenterLoesungszettel();
		persistenter.setUuid(REQUEST_LOESUNGSZETTEL_ID.identifier());
		persistenter.setKindID(kind.identifier().identifier());
		persistenter.setNutzereingabe("NNNNNNNNNNNN");
		persistenter.setAntwortcode("NNNNNNNNNNNN");
		persistenter.setWertungscode("nnnnnnnnnnnn");
		persistenter.setSprache(Sprache.de);
		persistenter.setWettbewerbUuid("2020");
		persistenter.setTeilnahmenummer(kind.teilnahmeIdentifier().teilnahmenummer());
		persistenter.setTeilnahmeart(Teilnahmeart.SCHULE);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
		when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
		when(kinderRepository.findKindWithLoesungszettelId(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(kind));
		when(loesungszettelRepository.removeLoesungszettel(REQUEST_LOESUNGSZETTEL_ID))
			.thenReturn(Optional.of(persistenter));

		// Act
		service.loesungszettelLoeschenWithAuthorizationCheck(REQUEST_LOESUNGSZETTEL_ID, VERANSTALTER_ID);

		// Assert
		verify(wettbewerbService, times(1)).aktuellerWettbewerb();
		verify(loesungszettelRepository, times(1)).ofID(any());
		verify(kinderRepository, times(0)).ofId(any());
		verify(kinderRepository, times(1)).findKindWithLoesungszettelId(REQUEST_LOESUNGSZETTEL_ID);
		verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

		verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
		verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
		verify(loesungszettelRepository, times(1)).removeLoesungszettel(REQUEST_LOESUNGSZETTEL_ID);
		verify(kinderRepository, times(1)).changeKind(any());

		assertNull(service.getLoesungszettelCreated());
		assertNull(service.getLoesungszettelChanged());
		assertNotNull(service.getLoesungszettelDeleted());
	}

	@Test
	@DisplayName("loesungszettel existiert, kind mit loesungszettelID existiert nicht => loeschen wird aufgerufen, ereignis wird nicht propagiert")
	void should_loesungszettelLoeschenWithAuthorizationCheckPropagateEvent_when_LoesungszettelVorhandenAberRepoGibtEmptyZurueckUndEsGibtKeinKind() {

		// Arrange
		Loesungszettel loesungszettel = new Loesungszettel()
			.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
			.withKindID(REQUEST_KIND_ID)
			.withTeilnahmeIdentifier(teilnahmeIdentifier);

		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
		when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
		when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
		when(kinderRepository.findKindWithLoesungszettelId(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());

		when(loesungszettelRepository.removeLoesungszettel(REQUEST_LOESUNGSZETTEL_ID))
			.thenReturn(Optional.empty());

		// Act
		service.loesungszettelLoeschenWithAuthorizationCheck(REQUEST_LOESUNGSZETTEL_ID, VERANSTALTER_ID);

		// Assert
		verify(wettbewerbService, times(1)).aktuellerWettbewerb();
		verify(loesungszettelRepository, times(1)).ofID(any());
		verify(kinderRepository, times(0)).ofId(any());
		verify(kinderRepository, times(1)).findKindWithLoesungszettelId(REQUEST_LOESUNGSZETTEL_ID);
		verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

		verify(loesungszettelRepository, times(0)).addLoesungszettel(any());
		verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
		verify(loesungszettelRepository, times(1)).removeLoesungszettel(REQUEST_LOESUNGSZETTEL_ID);
		verify(kinderRepository, times(0)).changeKind(any());

		assertNull(service.getLoesungszettelCreated());
		assertNull(service.getLoesungszettelChanged());
		assertNull(service.getLoesungszettelDeleted());
	}
}
