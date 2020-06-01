// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.WettbewerbAPIModel;
import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.WettbewerbDetailsAPIModel;
import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.WettbewerbListAPIModel;
import de.egladil.web.mk_wettbewerb_admin.domain.error.MkWettbewerbAdminRuntimeException;

/**
 * WettbewerbServiceTest
 */
public class WettbewerbServiceTest extends AbstractDomainServiceTest {

	private WettbewerbService service;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		this.service = WettbewerbService.createForTest(getWettbewerbRepository());
	}

	@Test
	void should_AlleWettbewerbeHolen_Work() {

		// Act
		List<WettbewerbListAPIModel> wettbewerbe = this.service.alleWettbewerbeHolen();

		// Assert
		assertEquals(3, wettbewerbe.size());

		{

			WettbewerbListAPIModel w = wettbewerbe.get(0);
			assertEquals(2017, w.jahr());
			assertEquals(WettbewerbStatus.ERFASST, w.status());
			assertFalse(w.completelyLoaded());
		}

		{

			WettbewerbListAPIModel w = wettbewerbe.get(1);
			assertEquals(2010, w.jahr());
			assertEquals(WettbewerbStatus.BEENDET, w.status());
			assertFalse(w.completelyLoaded());
		}

		{

			WettbewerbListAPIModel w = wettbewerbe.get(2);
			assertEquals(2005, w.jahr());
			assertEquals(WettbewerbStatus.BEENDET, w.status());
			assertFalse(w.completelyLoaded());
		}

	}

	@Test
	void should_AktuellerWettbewerb_return2017() {

		// Act
		Optional<Wettbewerb> optAktueller = service.aktuellerWettbewerb();

		// Assert
		Wettbewerb aktueller = optAktueller.get();
		assertEquals(2017, aktueller.id().jahr().intValue());
		assertEquals(WettbewerbStatus.ERFASST, aktueller.status());
		assertNotNull(aktueller.wettbewerbsbeginn());
		assertNotNull(aktueller.wettbewerbsende());
		assertNotNull(aktueller.datumFreischaltungLehrer());
		assertNotNull(aktueller.datumFreischaltungLehrer());
	}

	@Test
	void should_AktuellerWettbewerbBeEmpty_when_thereAreNoWettbewerbe() {

		// Arrange
		WettbewerbRepository repo = Mockito.mock(WettbewerbRepository.class);
		Mockito.when(repo.loadWettbewerbe()).thenReturn(new ArrayList<>());
		WettbewerbService wettbewerbService = WettbewerbService.createForTest(repo);

		// Act + Assert
		assertTrue(wettbewerbService.aktuellerWettbewerb().isEmpty());
	}

	@Test
	void should_WettbewerbMitJahrReturn_when_Exists() {

		// Act
		Optional<WettbewerbDetailsAPIModel> opt = service.wettbewerbMitJahr(Integer.valueOf(2010));

		// Assert
		assertTrue(opt.isPresent());
		WettbewerbDetailsAPIModel aktueller = opt.get();
		assertEquals(2010, aktueller.getJahr());
		assertEquals(WettbewerbStatus.BEENDET.toString(), aktueller.getStatus());
		assertEquals("01.03.2010", aktueller.getDatumFreischaltungLehrer());
		assertEquals("01.06.2010", aktueller.getDatumFreischaltungPrivat());
		assertEquals("01.01.2010", aktueller.getWettbewerbsbeginn());
		assertEquals("01.08.2010", aktueller.getWettbewerbsende());
	}

	@Test
	void should_WettbewerbMitJahrReturnEmpty_when_NotExists() {

		// Act
		Optional<WettbewerbDetailsAPIModel> opt = service.wettbewerbMitJahr(Integer.valueOf(2020));

		// Assert
		assertFalse(opt.isPresent());
	}

	@Test
	void should_WettbewerbMitJahrReturnEmpty_when_Jahr0() {

		// Act
		Optional<WettbewerbDetailsAPIModel> opt = service.wettbewerbMitJahr(null);

		// Assert
		assertFalse(opt.isPresent());
	}

	@Test
	void should_WettbewerbAnlegenThrowException_when_InputInvalid() {

		// Arrange
		WettbewerbAPIModel data = WettbewerbAPIModel.create(20010, "01.012006", "31.05.2006", "01.03.2006", "01.06.2006");

		// Act + Assert
		try {

			service.wettbewerbAnlegen(data);
			fail("keine InvalidInputException");

		} catch (InvalidInputException e) {

			ResponsePayload response = e.getResponsePayload();
			assertEquals("Die Eingaben sind nicht korrekt.", response.getMessage().getMessage());
			assertEquals("ERROR", response.getMessage().getLevel());
		}
	}

	@Test
	void should_WettbewerbAnlegenThrowException_when_WettbewerbsjahrInvalid() {

		// Arrange
		WettbewerbAPIModel data = WettbewerbAPIModel.create(2004, "01.01.2006", "31.05.2006", "01.03.2006", "01.06.2006");

		// Act + Assert
		try {

			service.wettbewerbAnlegen(data);
			fail("keine InvalidInputException");

		} catch (InvalidInputException e) {

			ResponsePayload response = e.getResponsePayload();
			assertEquals("Wettbewerbsjahr muss größer als 2004 sein.", response.getMessage().getMessage());
			assertEquals("ERROR", response.getMessage().getLevel());
		}
	}

	@Test
	void should_WettbewerbAnlegen_call_AddOnTheRepo() {

		// Arrange
		WettbewerbAPIModel data = WettbewerbAPIModel.create(2018, "01.01.2006", "31.05.2006",
			"01.03.2006", "01.06.2006");

		assertEquals(3, service.alleWettbewerbeHolen().size());
		// Act
		this.service.wettbewerbAnlegen(data);

		// Assert
		assertEquals(1, getCountWettbewerbInsert());
		List<WettbewerbListAPIModel> wettbewerbe = service.alleWettbewerbeHolen();
		assertEquals(4, wettbewerbe.size());

		Optional<WettbewerbDetailsAPIModel> optNeuer = service.wettbewerbMitJahr(2018);
		assertTrue(optNeuer.isPresent());

		WettbewerbDetailsAPIModel neuer = optNeuer.get();
		assertEquals(WettbewerbStatus.ERFASST.toString(), neuer.getStatus());

	}

	@Test
	void should_WettbewerbAnlegen_when_WettbewerbsbeginnNull() {

		// Arrange
		WettbewerbAPIModel data = WettbewerbAPIModel.create(2018, null, "31.05.2006",
			"01.03.2006", "01.06.2006");

		assertEquals(3, service.alleWettbewerbeHolen().size());
		// Act
		this.service.wettbewerbAnlegen(data);

		// Assert
		assertEquals(1, getCountWettbewerbInsert());
		List<WettbewerbListAPIModel> wettbewerbe = service.alleWettbewerbeHolen();
		assertEquals(4, wettbewerbe.size());

		Optional<WettbewerbDetailsAPIModel> optNeuer = service.wettbewerbMitJahr(2018);
		assertTrue(optNeuer.isPresent());

		WettbewerbDetailsAPIModel neuer = optNeuer.get();
		assertEquals(WettbewerbStatus.ERFASST.toString(), neuer.getStatus());
		assertNull(neuer.getWettbewerbsbeginn());

	}

	@Test
	void should_WettbewerbAnlegenConvertPersistenceException() {

		// Arrange
		WettbewerbAPIModel data = WettbewerbAPIModel.create(2011, null, "31.05.2006",
			"01.03.2006", "01.06.2006");

		assertEquals(0, getCountWettbewerbInsert());

		// Act + Assert
		try {

			service.wettbewerbAnlegen(data);
			fail("keine MkWettbewerbAdminRuntimeException");
		} catch (MkWettbewerbAdminRuntimeException e) {

			assertEquals(1, getCountWettbewerbInsert());
			assertEquals("PersistenceException beim Speichern eines neuen Wettbewerbs", e.getMessage());
		}

	}

	@Test
	void should_StarteNaechstePhaseThrowNotFoundException_when_WettbewerbNotPresent() {

		// Arrange
		WettbewerbID id = new WettbewerbID(2012);

		// Act + Assert
		try {

			service.starteNaechstePhase(id.jahr());
		} catch (NotFoundException e) {

			assertEquals(0, getCountChangeWettbewerbStatus());
			assertEquals("HTTP 404 Not Found", e.getMessage());
		}

	}

	@Test
	void should_StarteNaechstePhaseWork_when_WettbewerbPresent() {

		// Arrange
		WettbewerbID id = new WettbewerbID(2017);

		// Act
		WettbewerbStatus neuerStatus = service.starteNaechstePhase(id.jahr());

		// Assert
		assertEquals(1, getCountChangeWettbewerbStatus());
		assertEquals(WettbewerbStatus.ANMELDUNG, neuerStatus);

	}

	@Test
	void should_StarteNaechstePhaseThrowException_when_WettbewerbBeendet() {

		// Arrange
		WettbewerbID id = new WettbewerbID(2005);

		// Act + Assert
		try {

			service.starteNaechstePhase(id.jahr());
			fail("keine WebApplicationException");
		} catch (WebApplicationException e) {

			assertEquals(0, getCountChangeWettbewerbStatus());
			assertEquals("HTTP 412 Precondition Failed", e.getMessage());
			Object entity = e.getResponse().getEntity();
			assertTrue(entity instanceof ResponsePayload);
			ResponsePayload payload = (ResponsePayload) entity;
			assertEquals("ERROR", payload.getMessage().getLevel());
			assertEquals("Wettbewerb hat sein Lebensende erreicht. Es gibt keinen Folgestatus.", payload.getMessage().getMessage());
		}

	}

	@Test
	void should_StarteNaechstePhaseConvertPersistenceException() {

		// Arrange
		WettbewerbID wettbewerbID = new WettbewerbID(2020);
		Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID).withStatus(WettbewerbStatus.ERFASST);

		WettbewerbRepository repo = Mockito.mock(WettbewerbRepository.class);
		Mockito.when(repo.wettbewerbMitID(wettbewerbID)).thenReturn(Optional.of(wettbewerb));
		Mockito.when(repo.changeWettbewerbStatus(wettbewerbID, WettbewerbStatus.ANMELDUNG))
			.thenThrow(new PersistenceException("Blöd, die Datenbank ist weg"));

		WettbewerbService wettbewerbService = WettbewerbService.createForTest(repo);

		// Act
		try {

			wettbewerbService.starteNaechstePhase(wettbewerbID.jahr());
			fail("keine MkWettbewerbAdminRuntimeException");
		} catch (MkWettbewerbAdminRuntimeException e) {

			assertEquals("PersistenceException beim Speichern eines vorhandenen Wettbewerbs", e.getMessage());
		}
	}

}
