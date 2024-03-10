// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;

/**
 * WettbewerbServiceWithMocksTest
 */
@QuarkusTest
public class WettbewerbServiceWithMocksTest {

	@Inject
	WettbewerbService wettbewerbService;

	@InjectMock
	WettbewerbRepository repo;

	@Test
	void should_StarteNaechstePhaseConvertPersistenceException() {

		// Arrange
		WettbewerbID wettbewerbID = new WettbewerbID(2020);
		Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID).withStatus(WettbewerbStatus.ERFASST);

		Mockito.when(repo.wettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
		Mockito.when(repo.changeWettbewerbStatus(wettbewerbID, WettbewerbStatus.ANMELDUNG))
			.thenThrow(new PersistenceException("Blöd, die Datenbank ist weg"));

		// Act
		try {

			wettbewerbService.starteNaechstePhase(wettbewerbID.jahr());
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("PersistenceException beim Speichern eines vorhandenen Wettbewerbs", e.getMessage());
			verify(repo).wettbewerbMitID(any(WettbewerbID.class));
			verify(repo).changeWettbewerbStatus(wettbewerbID, WettbewerbStatus.ANMELDUNG);
		}
	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodusThrowException_when_NoWettbewerbAtAll() {

		// Arrange
		Mockito.when(repo.loadWettbewerbe())
			.thenReturn(Arrays.asList(new Wettbewerb[] {}));

		// Act
		try {

			wettbewerbService.aktuellerWettbewerbImAnmeldemodus();
			fail("keine IllegalStateException");

		} catch (IllegalStateException e) {

			assertEquals("Keine Anmeldung möglich. Es gibt keinen aktuellen Wettbewerb.", e.getMessage());
			verify(repo).loadWettbewerbe();
		}

	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodusThrowException_when_WettbewerbBeendet() {

		// Arrange
		Wettbewerb expected = createWettbewerb(2019, WettbewerbStatus.BEENDET);
		Mockito.when(repo.loadWettbewerbe())
			.thenReturn(Arrays.asList(new Wettbewerb[] { expected }));

		// Act
		try {

			wettbewerbService.aktuellerWettbewerbImAnmeldemodus();
			fail("keine IllegalStateException");

		} catch (IllegalStateException e) {

			assertEquals("Keine Anmeldung möglich. Der Wettbewerb ist beendet.", e.getMessage());
			verify(repo).loadWettbewerbe();
		}

	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodus_returnWettbewerb_when_StatusAnmeldung() {

		// Arrange
		Wettbewerb expected = createWettbewerb(2019, WettbewerbStatus.ANMELDUNG);

		Mockito.when(repo.loadWettbewerbe())
			.thenReturn(Arrays.asList(new Wettbewerb[] { expected }));

		// Act
		Wettbewerb actual = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

		// Assert
		assertEquals(expected, actual);
		verify(repo).loadWettbewerbe();

	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodus_returnWettbewerb_when_StatusDowloadLehrer() {

		// Arrange
		Wettbewerb expected = createWettbewerb(2019, WettbewerbStatus.DOWNLOAD_LEHRER);

		Mockito.when(repo.loadWettbewerbe())
			.thenReturn(Arrays.asList(new Wettbewerb[] { expected }));

		// Act
		Wettbewerb actual = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

		// Assert
		assertEquals(expected, actual);
		verify(repo).loadWettbewerbe();

	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodus_returnWettbewerb_when_StatusDowloadPrivat() {

		// Arrange
		Wettbewerb expected = createWettbewerb(2019, WettbewerbStatus.DOWNLOAD_PRIVAT);
		Mockito.when(repo.loadWettbewerbe())
			.thenReturn(Arrays.asList(new Wettbewerb[] { expected }));

		// Act
		Wettbewerb actual = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

		// Assert
		assertEquals(expected, actual);
		verify(repo).loadWettbewerbe();

	}

	@Test
	void should_AktuellerWettbewerbBeEmpty_when_thereAreNoWettbewerbe() {

		// Arrange
		Mockito.when(repo.loadWettbewerbe()).thenReturn(new ArrayList<>());

		// Act + Assert
		assertTrue(wettbewerbService.aktuellerWettbewerb().isEmpty());
		verify(repo).loadWettbewerbe();
	}

	private Wettbewerb createWettbewerb(final Integer jahr, final WettbewerbStatus status) {

		return new Wettbewerb(new WettbewerbID(jahr)).withStatus(status)
			.withWettbewerbsbeginn(LocalDate.of(jahr, Month.JANUARY, 1))
			.withDatumFreischaltungLehrer(LocalDate.of(jahr, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(jahr, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(jahr, Month.AUGUST, 1));

	}

}
