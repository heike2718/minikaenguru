// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.WettbewerbDetailsAPIModel;

/**
 * WettbewerbDetailsAPIModelTest
 */
public class WettbewerbDetailsAPIModelTest {

	@Test
	void should_CreateAndSerialize_when_WettbewerbsbeginnIsNotNull() throws JsonProcessingException {

		// Arrange
		Integer jahr = 2006;
		Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(jahr)).withStatus(WettbewerbStatus.BEENDET)
			.withWettbewerbsbeginn(LocalDate.of(jahr, Month.JANUARY, 1))
			.withDatumFreischaltungLehrer(LocalDate.of(jahr, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(jahr, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(jahr, Month.AUGUST, 1))
			.withLoesungsbuchstabenKlasse1("CDAD-ECCC-BCDE")
			.withLoesungsbuchstabenKlasse2("EACDD-CDCCE-BACBA");

		WettbewerbDetailsAPIModel model = WettbewerbDetailsAPIModel.fromWettbewerb(wettbewerb);

		// Act
		String serialized = new ObjectMapper().writeValueAsString(model);

		// Assert
		assertEquals(jahr.intValue(), model.getJahr());
		assertEquals("BEENDET", model.getStatus());
		assertEquals("01.03.2006", model.getDatumFreischaltungLehrer());
		assertEquals("01.06.2006", model.getDatumFreischaltungPrivat());
		assertEquals("01.01.2006", model.getWettbewerbsbeginn());
		assertEquals("01.08.2006", model.getWettbewerbsende());
		assertNull(model.getLoesungsbuchstabenIkids());
		assertEquals("CDAD-ECCC-BCDE", model.getLoesungsbuchstabenKlasse1());
		assertEquals("EACDD-CDCCE-BACBA", model.getLoesungsbuchstabenKlasse2());

		// {"jahr":2006,"status":"BEENDET","wettbewerbsbeginn":"01.01.2006","wettbewerbsende":"01.08.2006","datumFreischaltungLehrer":"01.03.2006","datumFreischaltungPrivat":"01.06.2006","loesungsbuchstabenIkids":null,"loesungsbuchstabenKlasse1":"CDAD-ECCC-BCDE","loesungsbuchstabenKlasse2":"EACDD-CDCCE-BACBA","teilnahmenuebersicht":null,"completelyLoaded":true}
		assertEquals(
			"{\"jahr\":2006,\"status\":\"BEENDET\",\"wettbewerbsbeginn\":\"01.01.2006\",\"wettbewerbsende\":\"01.08.2006\",\"datumFreischaltungLehrer\":\"01.03.2006\",\"datumFreischaltungPrivat\":\"01.06.2006\",\"loesungsbuchstabenIkids\":null,\"loesungsbuchstabenKlasse1\":\"CDAD-ECCC-BCDE\",\"loesungsbuchstabenKlasse2\":\"EACDD-CDCCE-BACBA\",\"teilnahmenuebersicht\":null,\"completelyLoaded\":true}",
			serialized);

		System.out.println(serialized);
	}

	@Test
	void should_CreateAndSerialize_when_WettbewerbsbeginnIsNull() throws JsonProcessingException {

		// Arrange
		Integer jahr = 2006;
		Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(jahr)).withStatus(WettbewerbStatus.ERFASST)
			.withDatumFreischaltungLehrer(LocalDate.of(jahr, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(jahr, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(jahr, Month.AUGUST, 1));

		WettbewerbDetailsAPIModel model = WettbewerbDetailsAPIModel.fromWettbewerb(wettbewerb);

		// Act
		String serialized = new ObjectMapper().writeValueAsString(model);

		// Assert
		// System.out.println(serialized);

		assertEquals(jahr.intValue(), model.getJahr());
		assertEquals("ERFASST", model.getStatus());
		assertEquals("01.03.2006", model.getDatumFreischaltungLehrer());
		assertEquals("01.06.2006", model.getDatumFreischaltungPrivat());
		assertNull(model.getWettbewerbsbeginn());
		assertEquals("01.08.2006", model.getWettbewerbsende());

		// {"jahr":2006,"status":"ERFASST","wettbewerbsbeginn":null,"wettbewerbsende":"01.08.2006","datumFreischaltungLehrer":"01.03.2006","datumFreischaltungPrivat":"01.06.2006","loesungsbuchstabenIkids":null,"loesungsbuchstabenKlasse1":null,"loesungsbuchstabenKlasse2":null,"teilnahmenuebersicht":null,"completelyLoaded":true}
		assertEquals(
			"{\"jahr\":2006,\"status\":\"ERFASST\",\"wettbewerbsbeginn\":null,\"wettbewerbsende\":\"01.08.2006\",\"datumFreischaltungLehrer\":\"01.03.2006\",\"datumFreischaltungPrivat\":\"01.06.2006\",\"loesungsbuchstabenIkids\":null,\"loesungsbuchstabenKlasse1\":null,\"loesungsbuchstabenKlasse2\":null,\"teilnahmenuebersicht\":null,\"completelyLoaded\":true}",
			serialized);

		System.out.println(serialized);
	}

}
