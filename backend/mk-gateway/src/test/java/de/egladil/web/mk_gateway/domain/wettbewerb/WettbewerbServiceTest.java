// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.EditWettbewerbModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.WettbewerbDetailsAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.WettbewerbListAPIModel;

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
		assertEquals(4, wettbewerbe.size());

		{

			WettbewerbListAPIModel w = wettbewerbe.get(0);
			assertEquals(2020, w.jahr());
			assertEquals(WettbewerbStatus.ANMELDUNG, w.status());
			assertFalse(w.completelyLoaded());
		}

		{

			WettbewerbListAPIModel w = wettbewerbe.get(1);
			assertEquals(2015, w.jahr());
			assertEquals(WettbewerbStatus.DOWNLOAD_PRIVAT, w.status());
			assertFalse(w.completelyLoaded());
		}

		{

			WettbewerbListAPIModel w = wettbewerbe.get(2);
			assertEquals(2010, w.jahr());
			assertEquals(WettbewerbStatus.BEENDET, w.status());
			assertFalse(w.completelyLoaded());
		}

		{

			WettbewerbListAPIModel w = wettbewerbe.get(3);
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
		assertEquals(2020, aktueller.id().jahr().intValue());
		assertEquals(WettbewerbStatus.ANMELDUNG, aktueller.status());
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
		Optional<WettbewerbDetailsAPIModel> opt = service.wettbewerbMitJahr(Integer.valueOf(2017));

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
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(20010, "01.012006", "31.05.2006", "01.03.2006", "01.06.2006");

		// Act + Assert
		try {

			service.wettbewerbAnlegen(data);
			fail("keine InvalidInputException");

		} catch (InvalidInputException e) {

			assertEquals(0, getCountWettbewerbInsert());
			assertEquals(0, getCountWettbewerbUpdate());
			assertEquals(0, getCountChangeWettbewerbStatus());

			ResponsePayload response = e.getResponsePayload();
			assertEquals("Die Eingaben sind nicht korrekt.", response.getMessage().getMessage());
			assertEquals("ERROR", response.getMessage().getLevel());
		}
	}

	@Test
	void should_WettbewerbAnlegenThrowException_when_WettbewerbsjahrInvalid() {

		// Arrange
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(2004, "01.01.2006", "31.05.2006", "01.03.2006", "01.06.2006");

		// Act + Assert
		try {

			service.wettbewerbAnlegen(data);
			fail("keine InvalidInputException");

		} catch (InvalidInputException e) {

			assertEquals(0, getCountWettbewerbInsert());
			assertEquals(0, getCountWettbewerbUpdate());
			assertEquals(0, getCountChangeWettbewerbStatus());

			ResponsePayload response = e.getResponsePayload();
			assertEquals("Wettbewerbsjahr muss größer als 2004 sein.", response.getMessage().getMessage());
			assertEquals("ERROR", response.getMessage().getLevel());
		}
	}

	@Test
	void should_WettbewerbAnlegen_call_AddOnTheRepo() {

		// Arrange
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(2018, "01.01.2006", "31.05.2006",
			"01.03.2006", "01.06.2006", "AA-AA-AA", "BBBB-BBBB-BBBB", "CCCCC-CCCCC-CCCCC");

		assertEquals(4, service.alleWettbewerbeHolen().size());

		// Act
		Wettbewerb neuer = this.service.wettbewerbAnlegen(data);

		// Assert
		assertEquals(1, getCountWettbewerbInsert());
		assertEquals(0, getCountWettbewerbUpdate());
		assertEquals(0, getCountChangeWettbewerbStatus());
		assertEquals(WettbewerbStatus.ERFASST, neuer.status());
		assertEquals("AA-AA-AA", neuer.loesungsbuchstabenIkids());
		assertEquals("BBBB-BBBB-BBBB", neuer.loesungsbuchstabenKlasse1());
		assertEquals("CCCCC-CCCCC-CCCCC", neuer.loesungsbuchstabenKlasse2());

	}

	@Test
	void should_WettbewerbAnlegen_save_handleMissingLoesunsgbuchstaben() {

		// Arrange
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(2018, "01.01.2006", "31.05.2006",
			"01.03.2006", "01.06.2006");

		assertEquals(4, service.alleWettbewerbeHolen().size());

		// Act
		Wettbewerb neuer = this.service.wettbewerbAnlegen(data);

		// Assert
		assertEquals(1, getCountWettbewerbInsert());
		assertEquals(0, getCountWettbewerbUpdate());
		assertEquals(0, getCountChangeWettbewerbStatus());
		assertEquals(WettbewerbStatus.ERFASST, neuer.status());
		assertNull(neuer.loesungsbuchstabenIkids());
		assertNull(neuer.loesungsbuchstabenKlasse1());
		assertNull(neuer.loesungsbuchstabenKlasse2());

	}

	@Test
	void should_WettbewerbAnlegenConvertPersistenceException() {

		// Arrange
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(2011, "01.02.2006", "31.05.2006",
			"01.03.2006", "01.06.2006");

		assertEquals(0, getCountWettbewerbInsert());

		// Act + Assert
		try {

			service.wettbewerbAnlegen(data);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

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
		WettbewerbID id = new WettbewerbID(2020);

		// Act
		WettbewerbStatus neuerStatus = service.starteNaechstePhase(id.jahr());

		// Assert
		assertEquals(1, getCountChangeWettbewerbStatus());
		assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, neuerStatus);

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
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("PersistenceException beim Speichern eines vorhandenen Wettbewerbs", e.getMessage());
		}
	}

	/// ///////////////////////////////////////////

	@Test
	void should_WettbewerbAendernThrowException_when_InputInvalid() {

		// Arrange
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(2005, "01.012006", "31.05.2006", "01.03.2006", "01.06.2006");

		// Act + Assert
		try {

			service.wettbewerbAendern(data);
			fail("keine InvalidInputException");

		} catch (InvalidInputException e) {

			assertEquals(0, getCountWettbewerbInsert());
			assertEquals(0, getCountWettbewerbUpdate());
			assertEquals(0, getCountChangeWettbewerbStatus());

			ResponsePayload response = e.getResponsePayload();
			assertEquals("Die Eingaben sind nicht korrekt.", response.getMessage().getMessage());
			assertEquals("ERROR", response.getMessage().getLevel());
		}
	}

	@Test
	void should_WettbewerbAendern_call_UpdateOnTheRepo() {

		// Arrange
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(2020, "13.02.2020", "15.08.2020", "23.03.2020",
			"01.07.2020", "AA-AA-AA", "BBBB-BBBB-BBBB", "CCCCC-CCCCC-CCCCC");

		WettbewerbDetailsAPIModel vorhandener = service.wettbewerbMitJahr(Integer.valueOf(2020)).get();
		assertEquals("01.01.2020", vorhandener.getWettbewerbsbeginn());
		assertEquals("01.08.2020", vorhandener.getWettbewerbsende());
		assertEquals("01.03.2020", vorhandener.getDatumFreischaltungLehrer());
		assertEquals("01.06.2020", vorhandener.getDatumFreischaltungPrivat());
		assertNull(vorhandener.getLoesungsbuchstabenIkids());
		assertNull(vorhandener.getLoesungsbuchstabenKlasse1());
		assertNull(vorhandener.getLoesungsbuchstabenKlasse2());

		// Act
		Wettbewerb geaenderter = this.service.wettbewerbAendern(data);

		// Assert
		assertEquals(0, getCountWettbewerbInsert());
		assertEquals(1, getCountWettbewerbUpdate());
		assertEquals(0, getCountChangeWettbewerbStatus());

		assertEquals(WettbewerbStatus.ANMELDUNG, geaenderter.status());
		assertEquals("13.02.2020", CommonTimeUtils.format(geaenderter.wettbewerbsbeginn()));
		assertEquals("23.03.2020", CommonTimeUtils.format(geaenderter.datumFreischaltungLehrer()));
		assertEquals("01.07.2020", CommonTimeUtils.format(geaenderter.datumFreischaltungPrivat()));
		assertEquals("15.08.2020", CommonTimeUtils.format(geaenderter.wettbewerbsende()));
		assertEquals("AA-AA-AA", geaenderter.loesungsbuchstabenIkids());
		assertEquals("BBBB-BBBB-BBBB", geaenderter.loesungsbuchstabenKlasse1());
		assertEquals("CCCCC-CCCCC-CCCCC", geaenderter.loesungsbuchstabenKlasse2());

	}

	@Test
	void should_WettbewerbAendernConvertPersistenceException() {

		// Arrange
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(2015, "01.01.2010", "31.03.2010",
			"01.03.2010", "01.09.2010");

		// Act + Assert
		try {

			service.wettbewerbAendern(data);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals(0, getCountWettbewerbInsert());
			assertEquals(1, getCountWettbewerbUpdate());
			assertEquals(0, getCountChangeWettbewerbStatus());
			assertEquals("PersistenceException beim Speichern eines vorhandenen Wettbewerbs", e.getMessage());
		}

	}

	@Test
	void should_WettbewerbAendernThrowNotFoundException_when_WettbewerbNotPresent() {

		// Arrange
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(2012, "01.01.2017", "31.05.2017",
			"01.03.2017", "01.06.2017");

		// Act + Assert
		try {

			service.wettbewerbAendern(data);
			fail("keine MkWettbewerbAdminRuntimeException");
		} catch (NotFoundException e) {

			assertEquals(0, getCountWettbewerbInsert());
			assertEquals(0, getCountWettbewerbUpdate());
			assertEquals(0, getCountChangeWettbewerbStatus());
			assertEquals("HTTP 404 Not Found", e.getMessage());
		}
	}

	@Test
	void should_WettbewerbAendernThrowWebapplicationException_when_WettbewerbBeendet() {

		// Arrange
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(2005, "01.01.2017", "31.05.2017",
			"01.03.2017", "01.06.2017");

		// Act + Assert
		try {

			service.wettbewerbAendern(data);
			fail("keine WebApplicationException");
		} catch (WebApplicationException e) {

			assertEquals(0, getCountWettbewerbInsert());
			assertEquals(0, getCountWettbewerbUpdate());
			assertEquals(0, getCountChangeWettbewerbStatus());

			assertEquals("HTTP 412 Precondition Failed", e.getMessage());
			Object entity = e.getResponse().getEntity();
			assertTrue(entity instanceof ResponsePayload);
			ResponsePayload payload = (ResponsePayload) entity;
			assertEquals("ERROR", payload.getMessage().getLevel());
			assertEquals("Wettbewerb hat sein Lebensende erreicht und kann nicht mehr geändert werden.",
				payload.getMessage().getMessage());
		}
	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodusThrowException_when_NoWettbewerbAtAll() {

		// Arrange
		WettbewerbRepository repo = Mockito.mock(WettbewerbRepository.class);
		Mockito.when(repo.loadWettbewerbe())
			.thenReturn(Arrays.asList(new Wettbewerb[] {}));

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
		WettbewerbRepository repo = Mockito.mock(WettbewerbRepository.class);
		Wettbewerb expected = createWettbewerb(2019, WettbewerbStatus.BEENDET);
		Mockito.when(repo.loadWettbewerbe())
			.thenReturn(Arrays.asList(new Wettbewerb[] { expected }));

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
		WettbewerbRepository repo = Mockito.mock(WettbewerbRepository.class);
		Wettbewerb expected = createWettbewerb(2019, WettbewerbStatus.ERFASST);

		Mockito.when(repo.loadWettbewerbe())
			.thenReturn(Arrays.asList(new Wettbewerb[] { expected }));

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
		WettbewerbRepository repo = Mockito.mock(WettbewerbRepository.class);

		Wettbewerb expected = createWettbewerb(2019, WettbewerbStatus.ANMELDUNG);

		Mockito.when(repo.loadWettbewerbe())
			.thenReturn(Arrays.asList(new Wettbewerb[] { expected }));

		WettbewerbService wettbewerbService = WettbewerbService.createForTest(repo);

		// Act
		Wettbewerb actual = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

		// Assert
		assertEquals(expected, actual);

	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodus_returnWettbewerb_when_StatusDowloadLehrer() {

		// Arrange
		WettbewerbRepository repo = Mockito.mock(WettbewerbRepository.class);
		Wettbewerb expected = createWettbewerb(2019, WettbewerbStatus.DOWNLOAD_LEHRER);

		Mockito.when(repo.loadWettbewerbe())
			.thenReturn(Arrays.asList(new Wettbewerb[] { expected }));

		WettbewerbService wettbewerbService = WettbewerbService.createForTest(repo);

		// Act
		Wettbewerb actual = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

		// Assert
		assertEquals(expected, actual);

	}

	@Test
	void should_AktuellerWettbewerbImAnmeldemodus_returnWettbewerb_when_StatusDowloadPrivat() {

		// Arrange
		WettbewerbRepository repo = Mockito.mock(WettbewerbRepository.class);
		Wettbewerb expected = createWettbewerb(2019, WettbewerbStatus.DOWNLOAD_PRIVAT);
		Mockito.when(repo.loadWettbewerbe())
			.thenReturn(Arrays.asList(new Wettbewerb[] { expected }));

		WettbewerbService wettbewerbService = WettbewerbService.createForTest(repo);

		// Act
		Wettbewerb actual = wettbewerbService.aktuellerWettbewerbImAnmeldemodus();

		// Assert
		assertEquals(expected, actual);

	}
}
