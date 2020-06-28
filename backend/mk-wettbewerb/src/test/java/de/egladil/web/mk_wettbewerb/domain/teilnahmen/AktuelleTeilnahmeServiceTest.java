// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_wettbewerb.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.error.AccessDeniedException;
import de.egladil.web.mk_wettbewerb.domain.error.MkWettbewerbRuntimeException;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbStatus;

/**
 * AktuelleTeilnahmeServiceTest
 */
public class AktuelleTeilnahmeServiceTest extends AbstractDomainServiceTest {

	private AktuelleTeilnahmeService service;

	private WettbewerbService wettbewerbService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();

		wettbewerbService = WettbewerbService.createForTest(getMockitoBasedWettbewerbRepository());

		service = AktuelleTeilnahmeService.createForTest(getTeilnahmenRepository(), wettbewerbService, getVeranstalterRepository());
	}

	@Nested
	@DisplayName("Aktuelle Teilnahme Feature")
	class AktuelleTeilnahmeFeature {

		@Test
		void should_GetAktuelleTeilnahmeThrowException_when_TeilnahmenummerNull() {

			// Arrange
			String teilnahmenummer = null;

			try {

				service.aktuelleTeilnahme(teilnahmenummer);
				fail("keine IllegalArgumentException");
			} catch (IllegalArgumentException e) {

				assertEquals("teilnahmenummer darf nicht blank sein.", e.getMessage());
			}

		}

		@Test
		void should_GetAktuelleTeilnahmeThrowException_when_TeilnahmenNull() {

			// Arrange
			List<Teilnahme> teilnahmen = null;

			try {

				service.aktuelleTeilnahme(teilnahmen);
				fail("keine IllegalArgumentException");
			} catch (IllegalArgumentException e) {

				assertEquals("teilnahmen darf nicht null sein.", e.getMessage());
			}

		}

		@Test
		void should_GetAktuelleTeilnahmeThrowException_when_TeilnahmenummerBlank() {

			try {

				service.aktuelleTeilnahme("  ");
				fail("keine IllegalArgumentException");
			} catch (IllegalArgumentException e) {

				assertEquals("teilnahmenummer darf nicht blank sein.", e.getMessage());
			}

		}

		@Test
		void should_GetAktuelleTeilnahmeReturnEmpty_when_KeinLaufenderWettbewerb() {

			// Arrange
			String teilnahmenummer = "HJKGDTDTU";
			Mockito.when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.empty());

			// Act
			Optional<Teilnahme> opt = service.aktuelleTeilnahme(teilnahmenummer);

			// Assert
			assertTrue(opt.isEmpty());

		}

		@Test
		void should_GetAktuelleTeilnahmeReturnEmpty_when_NichtAngemeldet() {

			// Arrange
			String teilnahmenummer = "HJKGDTDTU";

			// Act
			Optional<Teilnahme> opt = service.aktuelleTeilnahme(teilnahmenummer);

			// Assert
			assertTrue(opt.isEmpty());

		}

		@Test
		void should_GetAktuelleTeilnahmeReturnNonEmpty_when_Angemeldet() {

			// Arrange
			Teilnahme vorhandene = new Privatteilnahme(getAktuellerWettbewerb(WETTBEWERBSJAHR_AKTUELL).id(),
				new Identifier(TEILNAHMENUMMER_PRIVAT));

			// Act
			Optional<Teilnahme> opt = service.aktuelleTeilnahme(TEILNAHMENUMMER_PRIVAT);

			// Assert
			assertEquals(vorhandene, opt.get());
			assertEquals(0, getTeilnahmenRepository().getTeilnahmeAdded());

		}

	}

	@Nested
	@DisplayName("Privatperson anmelden")
	class PrivatpersonAnmeldenFeature {

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_ParameterNull() {

			// Arrange
			String uuid = null;

			try {

				service.privatpersonAnmelden(uuid);
				fail("keine IllegalArgumentException");
			} catch (IllegalArgumentException e) {

				assertEquals("uuid darf nicht blank sein.", e.getMessage());
				assertNull(service.privatteilnahmeCreatedEvent());
			}

		}

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_ParameterBlank() {

			// Arrange
			String uuid = "      ";

			try {

				service.privatpersonAnmelden(uuid);
				fail("keine IllegalArgumentException");
			} catch (IllegalArgumentException e) {

				assertEquals("uuid darf nicht blank sein.", e.getMessage());
			}

		}

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_NoPrivatpersonFound() {

			// Arrange
			String uuid = "basghoqh";

			// Act
			try {

				service.privatpersonAnmelden(uuid);
				fail("keine MkWettbewerbRuntimeException");

			} catch (MkWettbewerbRuntimeException e) {

				assertEquals("keinen Veranstalter mit UUID=basghoqh gefunden", e.getMessage());
				assertNull(service.privatteilnahmeCreatedEvent());

			}

		}

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_ZugangUnterlagenEntzogen() {

			// Arrange
			// Act
			try {

				service.privatpersonAnmelden(UUID_PRIVAT_GESPERRT);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("Dem Veranstalter wurde der Zugang zu den Unterlagen entzogen.", e.getMessage());
				assertNull(service.privatteilnahmeCreatedEvent());
			}
		}

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_VeranstalterLehrer() {

			// Arrange
			// Act
			try {

				service.privatpersonAnmelden(UUID_LEHRER_3);
				fail("keine AccessDeniedException");

			} catch (AccessDeniedException e) {

				assertEquals("Der Veranstalter ist ein Lehrer. Nur Privatprsonen dürfen diese Funktion aufrufen.", e.getMessage());
				assertNull(service.privatteilnahmeCreatedEvent());
			}
		}

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_WettbewerbBeendet() {

			// Arrange
			Mockito.when(getMockitoBasedWettbewerbRepository().loadWettbewerbWithMaxJahr())
				.thenReturn(Optional.of(createWettbewerb(2019, WettbewerbStatus.BEENDET)));

			// Act
			try {

				service.privatpersonAnmelden(UUID_PRIVAT_NICHT_ANGEMELDET);
				fail("keine IllegalStateException");

			} catch (IllegalStateException e) {

				assertEquals("Keine Anmeldung möglich. Der Wettbewerb ist beendet.", e.getMessage());
				assertNull(service.privatteilnahmeCreatedEvent());

			}

		}

		@Test
		void should_PrivatpersonAnmeldenThrowException_when_WettbewerbErfasst() {

			// Arrange
			Mockito.when(getMockitoBasedWettbewerbRepository().loadWettbewerbWithMaxJahr())
				.thenReturn(Optional.of(createWettbewerb(2019, WettbewerbStatus.ERFASST)));

			// Act
			try {

				service.privatpersonAnmelden(UUID_PRIVAT_NICHT_ANGEMELDET);
				fail("keine IllegalStateException");

			} catch (IllegalStateException e) {

				assertEquals("Keine Anmeldung möglich. Der Anmeldezeitraum hat noch nicht begonnen.", e.getMessage());
				assertNull(service.privatteilnahmeCreatedEvent());
			}

		}

		@Test
		void should_PrivatpersonAnmeldenCreateNew_when_NichtVorhanden() {

			// Act
			Teilnahme teilnahme = service.privatpersonAnmelden(UUID_PRIVAT_NICHT_ANGEMELDET);

			// Assert
			assertEquals(1, getTeilnahmenRepository().getTeilnahmeAdded());
			assertEquals(Teilnahmeart.PRIVAT, teilnahme.teilnahmeart());
			assertEquals(new Identifier(TEILNAHMENUMMER_PRIVAT_NICHT_ANGEMELDET), teilnahme.teilnahmenummer());
			assertEquals(new WettbewerbID(WETTBEWERBSJAHR_AKTUELL), teilnahme.wettbewerbID());

			PrivatteilnahmeCreated event = service.privatteilnahmeCreatedEvent();

			assertNotNull(event);
			assertEquals(TEILNAHMENUMMER_PRIVAT_NICHT_ANGEMELDET, event.teilnahmenummer());
			assertEquals(UUID_PRIVAT_NICHT_ANGEMELDET, event.triggeringUser());
			assertEquals(WETTBEWERBSJAHR_AKTUELL, event.wettbewerbsjahr());

		}

		@Test
		void should_PrivatpersonAnmeldenDoNothing_when_vorhanden() {

			// Act
			Teilnahme teilnahme = service.privatpersonAnmelden(UUID_PRIVAT);

			// Assert
			assertEquals(0, getTeilnahmenRepository().getTeilnahmeAdded());
			assertEquals(Teilnahmeart.PRIVAT, teilnahme.teilnahmeart());
			assertEquals(new Identifier(TEILNAHMENUMMER_PRIVAT), teilnahme.teilnahmenummer());
			assertEquals(new WettbewerbID(WETTBEWERBSJAHR_AKTUELL), teilnahme.wettbewerbID());

			assertNull(service.privatteilnahmeCreatedEvent());
		}
	}
}
