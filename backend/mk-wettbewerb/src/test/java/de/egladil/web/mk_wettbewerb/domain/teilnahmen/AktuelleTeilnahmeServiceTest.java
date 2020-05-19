// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbStatus;

/**
 * AktuelleTeilnahmeServiceTest
 */
public class AktuelleTeilnahmeServiceTest {

	private AktuelleTeilnahmeService service;

	private TeilnahmenRepository teilnahmenRepository;

	private WettbewerbService wettbewerbService;

	@BeforeEach
	void setUp() {

		teilnahmenRepository = Mockito.mock(TeilnahmenRepository.class);
		wettbewerbService = Mockito.mock(WettbewerbService.class);

		service = AktuelleTeilnahmeService.createForTest(teilnahmenRepository, wettbewerbService);
	}

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
		WettbewerbID aktuelleWettbewerbID = new WettbewerbID(2020);

		Mockito.when(wettbewerbService.aktuellerWettbewerb())
			.thenReturn(Optional.of(new Wettbewerb(aktuelleWettbewerbID).withStatus(WettbewerbStatus.ANMELDUNG)));

		Mockito.when(teilnahmenRepository.ofTeilnahmenummer(teilnahmenummer)).thenReturn(new ArrayList<>());

		// Act
		Optional<Teilnahme> opt = service.aktuelleTeilnahme(teilnahmenummer);

		// Assert
		assertTrue(opt.isEmpty());

	}

	@Test
	void should_GetAktuelleTeilnahmeReturnNonEmpty_when_Angemeldet() {

		// Arrange
		String teilnahmenummer = "HJKGDTDTU";
		WettbewerbID aktuelleWettbewerbID = new WettbewerbID(2020);

		Mockito.when(wettbewerbService.aktuellerWettbewerb())
			.thenReturn(Optional.of(new Wettbewerb(aktuelleWettbewerbID).withStatus(WettbewerbStatus.ANMELDUNG)));

		List<Teilnahme> teilnahmen = new ArrayList<>();

		Teilnahme vorhandene = new Privatteilnahme(aktuelleWettbewerbID, new Identifier(teilnahmenummer));
		teilnahmen.add(vorhandene);

		Mockito.when(teilnahmenRepository.ofTeilnahmenummer(teilnahmenummer)).thenReturn(teilnahmen);

		// Act
		Optional<Teilnahme> opt = service.aktuelleTeilnahme(teilnahmenummer);

		// Assert
		assertEquals(vorhandene, opt.get());

	}

}
