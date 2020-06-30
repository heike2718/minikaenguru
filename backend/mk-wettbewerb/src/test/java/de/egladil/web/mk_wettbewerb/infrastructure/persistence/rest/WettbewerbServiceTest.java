// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_wettbewerb.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_wettbewerb.infrastructure.rest.WettbewerbResource;

/**
 * WettbewerbServiceTest
 */
public class WettbewerbServiceTest extends AbstractDomainServiceTest {

	@Override
	@BeforeEach
	public void setUp() {

		super.setUp();
	}

	@Test
	void should_getAktuellenWettbewerbThrowNotFound_when_noWettbewerb() {

		// Arrange
		WettbewerbService wettbewerbService = Mockito.mock(WettbewerbService.class);
		Mockito.when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.empty());

		WettbewerbResource resource = WettbewerbResource.createForTest(wettbewerbService);

		// Act
		try {

			resource.getAktuellenWettbewerb();
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			// nüscht
		}

	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodusThrowException_when_NoWettbewerbAtAll() {

		// Arrange
		WettbewerbRepository repo = getMockitoBasedWettbewerbRepository();
		Mockito.when(repo.loadWettbewerbWithMaxJahr())
			.thenReturn(Optional.empty());

		WettbewerbService wettbewerbService = WettbewerbService.createForTest(repo);

		// Act
		try {

			wettbewerbService.aktuellerWettbewerbImAnmeldemodus();
			fail("keine IllegalStateException");

		} catch (IllegalStateException e) {

			assertEquals("Keine Anmeldung möglich. Es gibt keinen aktuellen Wettbewerb.", e.getMessage());
		}

	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodusThrowException_when_WettbewerbBeendet() {

		// Arrange
		WettbewerbRepository repo = getMockitoBasedWettbewerbRepository();
		Mockito.when(repo.loadWettbewerbWithMaxJahr())
			.thenReturn(Optional.empty());

		Mockito.when(repo.loadWettbewerbWithMaxJahr())
			.thenReturn(Optional.of(createWettbewerb(2019, WettbewerbStatus.BEENDET)));

		WettbewerbService wettbewerbService = WettbewerbService.createForTest(repo);

		// Act
		try {

			wettbewerbService.aktuellerWettbewerbImAnmeldemodus();
			fail("keine IllegalStateException");

		} catch (IllegalStateException e) {

			assertEquals("Keine Anmeldung möglich. Der Wettbewerb ist beendet.", e.getMessage());
		}

	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodusThrowException_when_WettbewerbErfasst() {

		// Arrange
		WettbewerbRepository repo = getMockitoBasedWettbewerbRepository();
		Mockito.when(repo.loadWettbewerbWithMaxJahr())
			.thenReturn(Optional.empty());

		Mockito.when(repo.loadWettbewerbWithMaxJahr())
			.thenReturn(Optional.of(createWettbewerb(2019, WettbewerbStatus.ERFASST)));

		WettbewerbService wettbewerbService = WettbewerbService.createForTest(repo);

		// Act
		try {

			wettbewerbService.aktuellerWettbewerbImAnmeldemodus();
			fail("keine IllegalStateException");

		} catch (IllegalStateException e) {

			assertEquals("Keine Anmeldung möglich. Der Anmeldezeitraum hat noch nicht begonnen.", e.getMessage());
		}

	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodus_returnWettbewerb_when_StatusAnmeldung() {

		// Arrange
		WettbewerbRepository repo = getMockitoBasedWettbewerbRepository();
		Mockito.when(repo.loadWettbewerbWithMaxJahr())
			.thenReturn(Optional.empty());

		Wettbewerb expected = createWettbewerb(2019, WettbewerbStatus.ANMELDUNG);
		Mockito.when(repo.loadWettbewerbWithMaxJahr())
			.thenReturn(Optional.of(expected));

		WettbewerbService wettbewerbService = WettbewerbService.createForTest(repo);

		// Act
		Wettbewerb actual = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

		// Assert
		assertEquals(expected, actual);

	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodus_returnWettbewerb_when_StatusDowloadLehrer() {

		// Arrange
		WettbewerbRepository repo = getMockitoBasedWettbewerbRepository();
		Mockito.when(repo.loadWettbewerbWithMaxJahr())
			.thenReturn(Optional.empty());

		Wettbewerb expected = createWettbewerb(2019, WettbewerbStatus.DOWNLOAD_LEHRER);
		Mockito.when(repo.loadWettbewerbWithMaxJahr())
			.thenReturn(Optional.of(expected));

		WettbewerbService wettbewerbService = WettbewerbService.createForTest(repo);

		// Act
		Wettbewerb actual = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

		// Assert
		assertEquals(expected, actual);

	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodus_returnWettbewerb_when_StatusDowloadPrivat() {

		// Arrange
		WettbewerbRepository repo = getMockitoBasedWettbewerbRepository();
		Mockito.when(repo.loadWettbewerbWithMaxJahr())
			.thenReturn(Optional.empty());

		Wettbewerb expected = createWettbewerb(2019, WettbewerbStatus.DOWNLOAD_PRIVAT);
		Mockito.when(repo.loadWettbewerbWithMaxJahr())
			.thenReturn(Optional.of(expected));

		WettbewerbService wettbewerbService = WettbewerbService.createForTest(repo);

		// Act
		Wettbewerb actual = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

		// Assert
		assertEquals(expected, actual);

	}

}
