// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * LoesungszettelServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class LoesungszettelServiceTest extends AbstractLoesungszettelServiceTest {

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
	class FindPunkteTests {

		@Test
		void should_findPunkteWithIDReturnEmpty_when_IdentifierNull() {

			// Arrange
			Identifier loesungszettelID = null;

			// Act + Assert
			assertTrue(service.findPunkteWithID(loesungszettelID).isEmpty());

		}

		@Test
		void should_findPunkteWithIDReturnEmpty_when_LoesungszettelAbsent() {

			// Arrange
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());

			// Act + Assert
			assertTrue(service.findPunkteWithID(REQUEST_LOESUNGSZETTEL_ID).isEmpty());

			verify(loesungszettelRepository, times(1)).ofID(REQUEST_LOESUNGSZETTEL_ID);

		}

		@Test
		void should_findPunkteWithIDReturnResult_when_LoesungszettelPresent() {

			// Arrange
			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withLaengeKaengurusprung(1)
				.withPunkte(625)
				.withKlassenstufe(Klassenstufe.EINS)
				.withRohdaten(createRohdatenKlasseEINS())
				.withVersion(3);

			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));

			// Act
			Optional<LoesungszettelpunkteAPIModel> optResult = service.findPunkteWithID(REQUEST_LOESUNGSZETTEL_ID);

			// Assert
			assertTrue(optResult.isPresent());

			LoesungszettelpunkteAPIModel result = optResult.get();
			assertEquals(3, result.getVersion());
			assertNull(result.getConcurrentModificationType());

			verify(loesungszettelRepository, times(1)).ofID(REQUEST_LOESUNGSZETTEL_ID);

		}

	}

	@Nested
	class FindLoesungszettelTests {

		@Test
		void should_findLoesungszettelWithIDThrowNotFoundException_when_loesungszettelAbsent() {

			// Arrange
			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());

			// Act
			try {

				service.findLoesungszettelWithID(REQUEST_LOESUNGSZETTEL_ID, VERANSTALTER_ID);

				fail("keine NotFoundException");
			} catch (NotFoundException e) {

				verify(loesungszettelRepository, times(1)).ofID(REQUEST_LOESUNGSZETTEL_ID);
				verify(authService, times(0)).checkPermissionForTeilnahmenummer(any(), any(), any());
			}

		}

		@Test
		void should_findLoesungszettelWithIDReturnAllData_when_loesungszettelPresent() {

			// Arrange
			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withLaengeKaengurusprung(1)
				.withPunkte(625)
				.withKlassenstufe(Klassenstufe.EINS)
				.withRohdaten(createRohdatenKlasseEINS())
				.withVersion(3)
				.withAuswertungsquelle(Auswertungsquelle.ONLINE);

			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);

			// Act
			LoesungszettelAPIModel result = service.findLoesungszettelWithID(REQUEST_LOESUNGSZETTEL_ID, VERANSTALTER_ID);

			// Assert
			assertEquals(3, result.version());
			assertEquals(Klassenstufe.EINS, result.klassenstufe());
			assertEquals(REQUEST_KIND_ID.identifier(), result.kindID());
			assertEquals(REQUEST_LOESUNGSZETTEL_ID.identifier(), result.uuid());

			List<LoesungszettelZeileAPIModel> actualZeilen = result.zeilen();

			assertEquals(zeilen.size(), actualZeilen.size());

			for (int i = 0; i < zeilen.size(); i++) {

				assertEquals("Fehler bei Index= " + i, zeilen.get(i), actualZeilen.get(i));
			}

			verify(loesungszettelRepository, times(1)).ofID(REQUEST_LOESUNGSZETTEL_ID);
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
		}

		@Test
		void should_findLoesungszettelWithIDThrowBadRequestException_when_auswertungsquelleNull() {

			// Arrange
			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withLaengeKaengurusprung(1)
				.withPunkte(625)
				.withKlassenstufe(Klassenstufe.EINS)
				.withRohdaten(createRohdatenKlasseEINS())
				.withVersion(3);

			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);

			// Act
			try {

				service.findLoesungszettelWithID(REQUEST_LOESUNGSZETTEL_ID, VERANSTALTER_ID);

			} catch (BadRequestException e) {

				assertEquals("Diese Methode darf nur bei Onlineauswertungen aufgerufen werden", e.getMessage());

				verify(loesungszettelRepository, times(1)).ofID(REQUEST_LOESUNGSZETTEL_ID);
				verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
			}
		}

		@Test
		void should_findLoesungszettelWithIDThrowBadRequestException_when_auswertungsquelleUpload() {

			// Arrange
			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withLaengeKaengurusprung(1)
				.withPunkte(625)
				.withKlassenstufe(Klassenstufe.EINS)
				.withRohdaten(createRohdatenKlasseEINS())
				.withVersion(3)
				.withAuswertungsquelle(Auswertungsquelle.UPLOAD);

			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);

			// Act
			try {

				service.findLoesungszettelWithID(REQUEST_LOESUNGSZETTEL_ID, VERANSTALTER_ID);

			} catch (BadRequestException e) {

				assertEquals("Diese Methode darf nur bei Onlineauswertungen aufgerufen werden", e.getMessage());

				verify(loesungszettelRepository, times(1)).ofID(REQUEST_LOESUNGSZETTEL_ID);
				verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
			}
		}

		@Test
		void should_findLoesungszettelWithIDThrowAccessDeniedException_when_auswertungsquelleUpload() {

			// Arrange
			Loesungszettel loesungszettel = new Loesungszettel()
				.withIdentifier(REQUEST_LOESUNGSZETTEL_ID)
				.withKindID(REQUEST_KIND_ID)
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withLaengeKaengurusprung(1)
				.withPunkte(625)
				.withKlassenstufe(Klassenstufe.EINS)
				.withRohdaten(createRohdatenKlasseEINS())
				.withVersion(3)
				.withAuswertungsquelle(Auswertungsquelle.UPLOAD);

			when(loesungszettelRepository.ofID(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.of(loesungszettel));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenThrow(new AccessDeniedException());

			// Act
			try {

				service.findLoesungszettelWithID(REQUEST_LOESUNGSZETTEL_ID, VERANSTALTER_ID);

			} catch (AccessDeniedException e) {

				verify(loesungszettelRepository, times(1)).ofID(REQUEST_LOESUNGSZETTEL_ID);
				verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
			}
		}
	}

	@Nested
	class SpracheLoesungszettelAendernTests {

		@Test
		void should_spracheLoesungszettelAendernDoNothing_when_LoesungszettelAbsent() {

			// Arrange
			when(loesungszettelRepository.findPersistentenLoesungszettel(REQUEST_LOESUNGSZETTEL_ID)).thenReturn(Optional.empty());

			// Act
			service.spracheLoesungszettelAendern(REQUEST_LOESUNGSZETTEL_ID, Sprache.en, VERANSTALTER_ID.identifier());

			// Assert
			verify(loesungszettelRepository, times(1)).findPersistentenLoesungszettel(REQUEST_LOESUNGSZETTEL_ID);
			verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
			verify(authService, times(0)).checkPermissionForTeilnahmenummer(any(), any(), any());
			verify(wettbewerbService, times(0)).aktuellerWettbewerb();
		}

		@Test
		void should_spracheLoesungszettelAendernDoChange_when_LoesungszettelPresent() {

			// Arrange
			PersistenterLoesungszettel persistenter = new PersistenterLoesungszettel();
			persistenter.setAuswertungsquelle(Auswertungsquelle.ONLINE);
			persistenter.setUuid(REQUEST_LOESUNGSZETTEL_ID.identifier());
			persistenter.setKindID(REQUEST_KIND_ID.identifier());
			persistenter.setNutzereingabe("NNNNNNNNNNNN");
			persistenter.setAntwortcode("NNNNNNNNNNNN");
			persistenter.setWertungscode("nnnnnnnnnnnn");
			persistenter.setSprache(Sprache.de);
			persistenter.setWettbewerbUuid(aktuellerWettbewerb.id().toString());
			persistenter.setTeilnahmenummer(teilnahmeIdentifier.teilnahmenummer());
			persistenter.setTeilnahmeart(teilnahmeIdentifier.teilnahmeart());

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.findPersistentenLoesungszettel(REQUEST_LOESUNGSZETTEL_ID))
				.thenReturn(Optional.of(persistenter));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);

			// Act
			service.spracheLoesungszettelAendern(REQUEST_LOESUNGSZETTEL_ID, Sprache.en, VERANSTALTER_ID.identifier());

			verify(wettbewerbService, times(1)).aktuellerWettbewerb();
			verify(loesungszettelRepository, times(1)).findPersistentenLoesungszettel(REQUEST_LOESUNGSZETTEL_ID);
			verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

			verify(loesungszettelRepository, times(1)).updateLoesungszettelInTransaction(any());

			assertNull(service.getLoesungszettelCreated());
			assertNotNull(service.getLoesungszettelChanged());
			assertNull(service.getLoesungszettelDeleted());

		}

		@Test
		void should_spracheLoesungszettelThrowBadRequestException_when_LoesungszettelPresentButNotActualYear() {

			// Arrange
			PersistenterLoesungszettel persistenter = new PersistenterLoesungszettel();
			persistenter.setUuid(REQUEST_LOESUNGSZETTEL_ID.identifier());
			persistenter.setKindID(REQUEST_KIND_ID.identifier());
			persistenter.setNutzereingabe("NNNNNNNNNNNN");
			persistenter.setAntwortcode("NNNNNNNNNNNN");
			persistenter.setWertungscode("nnnnnnnnnnnn");
			persistenter.setSprache(Sprache.de);
			persistenter.setAuswertungsquelle(Auswertungsquelle.ONLINE);
			persistenter.setWettbewerbUuid("2017");
			persistenter.setTeilnahmenummer(teilnahmeIdentifier.teilnahmenummer());
			persistenter.setTeilnahmeart(teilnahmeIdentifier.teilnahmeart());

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));
			when(loesungszettelRepository.findPersistentenLoesungszettel(REQUEST_LOESUNGSZETTEL_ID))
				.thenReturn(Optional.of(persistenter));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);

			try {

				service.spracheLoesungszettelAendern(REQUEST_LOESUNGSZETTEL_ID, Sprache.en, VERANSTALTER_ID.identifier());
			} catch (BadRequestException e) {

				assertEquals("Dieser Lösungszettel ist aus dem Jahr 2017. Er kann nicht geändert werden.", e.getMessage());

				verify(wettbewerbService, times(1)).aktuellerWettbewerb();
				verify(loesungszettelRepository, times(1)).findPersistentenLoesungszettel(REQUEST_LOESUNGSZETTEL_ID);
				verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());
			}

		}

		@Test
		void should_spracheLoesungszettelAendernDoNothing_when_LoesungszettelPresentButNotAuthorized() {

			// Arrange
			PersistenterLoesungszettel persistenter = new PersistenterLoesungszettel();
			persistenter.setUuid(REQUEST_LOESUNGSZETTEL_ID.identifier());
			persistenter.setKindID(REQUEST_KIND_ID.identifier());
			persistenter.setNutzereingabe("NNNNNNNNNNNN");
			persistenter.setAntwortcode("NNNNNNNNNNNN");
			persistenter.setWertungscode("nnnnnnnnnnnn");
			persistenter.setSprache(Sprache.de);
			persistenter.setWettbewerbUuid(teilnahmeIdentifier.wettbewerbID());
			persistenter.setTeilnahmenummer(teilnahmeIdentifier.teilnahmenummer());
			persistenter.setTeilnahmeart(teilnahmeIdentifier.teilnahmeart());

			when(loesungszettelRepository.findPersistentenLoesungszettel(REQUEST_LOESUNGSZETTEL_ID))
				.thenReturn(Optional.of(persistenter));
			when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenThrow(new AccessDeniedException());

			// Act
			try {

				service.spracheLoesungszettelAendern(REQUEST_LOESUNGSZETTEL_ID, Sprache.en, VERANSTALTER_ID.identifier());
				fail("keine AccessDeniedException");
			} catch (AccessDeniedException e) {

				verify(loesungszettelRepository, times(1)).findPersistentenLoesungszettel(REQUEST_LOESUNGSZETTEL_ID);
				verify(loesungszettelRepository, times(0)).updateLoesungszettel(any());
				verify(authService, times(1)).checkPermissionForTeilnahmenummer(any(), any(), any());

			}

		}

	}

}
