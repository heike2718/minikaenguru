// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.error.MkGatewayWebApplicationException;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.EditWettbewerbModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.WettbewerbDetailsAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.WettbewerbListAPIModel;
import de.egladil.web.mk_gateway.profiles.FullDatabaseTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

/**
 * WettbewerbServiceTest
 */
@QuarkusTest
@TestProfile(FullDatabaseTestProfile.class)
public class WettbewerbServiceTest extends AbstractDomainServiceTest {

	@Inject
	private WettbewerbService service;

	@Test
	void should_AlleWettbewerbeHolen_Work() {

		// Act
		List<WettbewerbListAPIModel> wettbewerbe = this.service.alleWettbewerbeHolen();

		// Assert
		{

			WettbewerbListAPIModel w = wettbewerbe.get(0);
			assertEquals(Integer.valueOf(2020), w.jahr());
			assertEquals(WettbewerbStatus.DOWNLOAD_PRIVAT, w.status());
			assertFalse(w.completelyLoaded());
		}
	}

	@Test
	void should_AktuellerWettbewerb_return2020() {

		// Act
		Optional<Wettbewerb> optAktueller = service.aktuellerWettbewerb();

		// Assert
		Wettbewerb aktueller = optAktueller.get();
		assertEquals(2020, aktueller.id().jahr().intValue());
		assertEquals(WettbewerbStatus.DOWNLOAD_PRIVAT, aktueller.status());
		assertNotNull(aktueller.wettbewerbsbeginn());
		assertNotNull(aktueller.wettbewerbsende());
		assertNotNull(aktueller.datumFreischaltungLehrer());
		assertNotNull(aktueller.datumFreischaltungLehrer());
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
		assertEquals("05.03.2010", aktueller.getDatumFreischaltungLehrer());
		assertEquals("01.06.2010", aktueller.getDatumFreischaltungPrivat());
		assertEquals("11.11.2009", aktueller.getWettbewerbsbeginn());
		assertEquals("01.08.2010", aktueller.getWettbewerbsende());
	}

	@Test
	void should_WettbewerbMitJahrReturnEmpty_when_NotExists() {

		// Act
		Optional<WettbewerbDetailsAPIModel> opt = service.wettbewerbMitJahr(Integer.valueOf(2005));

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
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(2050, "01.01.2006", "31.05.2006",
			"01.03.2006", "01.06.2006", "AA-AA-AA", "BBBB-BBBB-BBBB", "CCCCC-CCCCC-CCCCC");

		// Act
		Wettbewerb neuer = this.service.wettbewerbAnlegen(data);

		// Assert
		assertEquals(WettbewerbStatus.ERFASST, neuer.status());
		assertEquals("AA-AA-AA", neuer.loesungsbuchstabenIkids());
		assertEquals("BBBB-BBBB-BBBB", neuer.loesungsbuchstabenKlasse1());
		assertEquals("CCCCC-CCCCC-CCCCC", neuer.loesungsbuchstabenKlasse2());

	}

	@Test
	void should_WettbewerbAnlegen_save_handleMissingLoesunsgbuchstaben() {

		// Arrange
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(2006, "01.01.2006", "31.05.2006",
			"01.03.2006", "01.06.2006");

		// Act
		Wettbewerb neuer = this.service.wettbewerbAnlegen(data);

		// Assert
		assertEquals(WettbewerbStatus.ERFASST, neuer.status());
		assertNull(neuer.loesungsbuchstabenIkids());
		assertNull(neuer.loesungsbuchstabenKlasse1());
		assertNull(neuer.loesungsbuchstabenKlasse2());

	}

	@Test
	void should_StarteNaechstePhaseThrowNotFoundException_when_WettbewerbNotPresent() {

		// Arrange
		WettbewerbID id = new WettbewerbID(2007);

		// Act + Assert
		try {

			service.starteNaechstePhase(id.jahr());
		} catch (NotFoundException e) {

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
		assertEquals(WettbewerbStatus.BEENDET, neuerStatus);

	}

	@Test
	void should_StarteNaechstePhaseThrowException_when_WettbewerbBeendet() {

		// Arrange
		WettbewerbID id = new WettbewerbID(2010);

		// Act + Assert
		try {

			service.starteNaechstePhase(id.jahr());
			fail("keine WebApplicationException");
		} catch (MkGatewayWebApplicationException e) {

			assertEquals(412, e.getResponse().getStatus());
			Object entity = e.getResponse().getEntity();
			assertTrue(entity instanceof ResponsePayload);
			ResponsePayload payload = (ResponsePayload) entity;
			assertEquals("ERROR", payload.getMessage().getLevel());
			assertEquals("Wettbewerb hat sein Lebensende erreicht. Es gibt keinen Folgestatus.", payload.getMessage().getMessage());
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
		assertEquals("11.11.2019", vorhandener.getWettbewerbsbeginn());
		assertEquals("31.07.2020", vorhandener.getWettbewerbsende());
		assertEquals("05.03.2020", vorhandener.getDatumFreischaltungLehrer());
		assertEquals("01.06.2020", vorhandener.getDatumFreischaltungPrivat());
		assertEquals("AC-CB-BC", vorhandener.getLoesungsbuchstabenIkids());
		assertEquals("CEBE-DDEC-BCAE", vorhandener.getLoesungsbuchstabenKlasse1());
		assertEquals("BAECA-CACBE-DECEC", vorhandener.getLoesungsbuchstabenKlasse2());

		// Act
		Wettbewerb geaenderter = this.service.wettbewerbAendern(data);

		// Assert
		assertEquals(WettbewerbStatus.DOWNLOAD_PRIVAT, geaenderter.status());
		assertEquals("13.02.2020", CommonTimeUtils.format(geaenderter.wettbewerbsbeginn()));
		assertEquals("23.03.2020", CommonTimeUtils.format(geaenderter.datumFreischaltungLehrer()));
		assertEquals("01.07.2020", CommonTimeUtils.format(geaenderter.datumFreischaltungPrivat()));
		assertEquals("15.08.2020", CommonTimeUtils.format(geaenderter.wettbewerbsende()));
		assertEquals("AA-AA-AA", geaenderter.loesungsbuchstabenIkids());
		assertEquals("BBBB-BBBB-BBBB", geaenderter.loesungsbuchstabenKlasse1());
		assertEquals("CCCCC-CCCCC-CCCCC", geaenderter.loesungsbuchstabenKlasse2());

	}

	@Test
	void should_WettbewerbAendernThrowNotFoundException_when_WettbewerbNotPresent() {

		// Arrange
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(2008, "01.01.2017", "31.05.2017",
			"01.03.2017", "01.06.2017");

		// Act + Assert
		try {

			service.wettbewerbAendern(data);
			fail("keine MkWettbewerbAdminRuntimeException");
		} catch (NotFoundException e) {

			//
		}
	}

	@Test
	void should_WettbewerbAendernThrowWebapplicationException_when_WettbewerbBeendet() {

		// Arrange
		EditWettbewerbModel data = EditWettbewerbModel.createForTest(2010, "01.01.2017", "31.05.2017",
			"01.03.2017", "01.06.2017");

		// Act + Assert
		try {

			service.wettbewerbAendern(data);
			fail("keine WebApplicationException");
		} catch (MkGatewayWebApplicationException e) {

			assertEquals(412, e.getResponse().getStatus());
			Object entity = e.getResponse().getEntity();
			assertTrue(entity instanceof ResponsePayload);
			ResponsePayload payload = (ResponsePayload) entity;
			assertEquals("ERROR", payload.getMessage().getLevel());
			assertEquals("Wettbewerb hat sein Lebensende erreicht und kann nicht mehr geändert werden.",
				payload.getMessage().getMessage());
		}
	}
}
