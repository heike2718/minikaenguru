// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.apimodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.WettbewerbStatus;

/**
 * WettbewerbListAPIModelTest
 */
public class WettbewerbListAPIModelTest {

	@Test
	void should_Serialize() throws JsonProcessingException {

		// Arrange
		Integer jahr = 2006;
		Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(jahr)).withStatus(WettbewerbStatus.BEENDET)
			.withWettbewerbsbeginn(LocalDate.of(jahr, Month.JANUARY, 1))
			.withDatumFreischaltungLehrer(LocalDate.of(jahr, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(jahr, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(jahr, Month.AUGUST, 1));

		WettbewerbListAPIModel model = WettbewerbListAPIModel.fromWettbewerb(wettbewerb);

		// Act
		String serialized = new ObjectMapper().writeValueAsString(model);

		// Assert

		assertEquals(jahr.intValue(), model.jahr());
		assertEquals(WettbewerbStatus.BEENDET, model.status());

		assertEquals(
			"{\"jahr\":2006,\"status\":\"BEENDET\",\"completelyLoaded\":false}",
			serialized);

		System.out.println(serialized);
	}
}
