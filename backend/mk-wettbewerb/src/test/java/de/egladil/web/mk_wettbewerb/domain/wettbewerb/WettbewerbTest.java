// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.wettbewerb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;

/**
 * WettbewerbTest
 */
public class WettbewerbTest {

	@Test
	void should_ConstructorThrowException_when_ParameterNull() {

		try {

			new Wettbewerb(null);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("wettbewerbId darf nicht null sein.", e.getMessage());
		}
	}

	@Test
	void should_FloatingSettersWork() {

		// Arrange
		Integer jahr = Integer.valueOf(2013);
		WettbewerbID id = new WettbewerbID(jahr);
		Wettbewerb wettbewerb = new Wettbewerb(id);

		LocalDate freischaltungLehrer = LocalDate.of(id.jahr(), Month.MARCH, 1);
		LocalDate freischaltungPrivat = LocalDate.of(id.jahr(), Month.JUNE, 1);
		LocalDate beginn = LocalDate.of(id.jahr(), Month.JANUARY, 1);
		LocalDate ende = LocalDate.of(id.jahr(), Month.AUGUST, 1);

		// Act
		wettbewerb.withDatumFreischaltungLehrer(freischaltungLehrer).withDatumFreischaltungPrivat(freischaltungPrivat)
			.withWettbewerbsbeginn(beginn).withWettbewerbsende(ende).withStatus(WettbewerbStatus.ERFASST);

		// Assert
		assertEquals(0, freischaltungLehrer.compareTo(wettbewerb.datumFreischaltungLehrer()));
		assertEquals(0, freischaltungPrivat.compareTo(wettbewerb.datumFreischaltungPrivat()));
		assertEquals(0, beginn.compareTo(wettbewerb.wettbewerbsbeginn()));
		assertEquals(0, ende.compareTo(wettbewerb.wettbewerbsende()));
		assertEquals(WettbewerbStatus.ERFASST, wettbewerb.status());

		assertEquals("2013", wettbewerb.toString());

	}

	@Test
	void should_EqualsAndHashCode_BaseOnWettbewerbsID() {

		// Arrange
		Wettbewerb erster = new Wettbewerb(new WettbewerbID(Integer.valueOf(2005)));
		Wettbewerb zweiter = new Wettbewerb(new WettbewerbID(Integer.valueOf(2005))).withStatus(WettbewerbStatus.DOWNLOAD_LEHRER);
		Wettbewerb dritter = new Wettbewerb(new WettbewerbID(Integer.valueOf(2010)));

		// Assert
		assertEquals(erster, erster);
		assertEquals(erster, zweiter);
		assertEquals(erster.hashCode(), zweiter.hashCode());
		assertFalse(erster.equals(dritter));

		assertFalse(erster.equals(null));
		assertFalse(erster.equals(new Object()));
	}

}
