// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * WettbewerbTest
 */
public class WettbewerbTest {

	@Test
	void should_ConstructorInitializeStatus() {

		// Arrange
		Integer jahr = Integer.valueOf(2013);
		WettbewerbID id = new WettbewerbID(jahr);

		// Act
		Wettbewerb wettbewerb = new Wettbewerb(id);

		// Assert
		assertEquals(id, wettbewerb.id());
		assertEquals(WettbewerbStatus.ERFASST, wettbewerb.status());
		wettbewerb.naechsterStatus();
		assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
		assertEquals(wettbewerb, wettbewerb);
		assertFalse(wettbewerb.equals(new Object()));
		assertFalse(wettbewerb.equals(null));
		assertNull(wettbewerb.datumFreischaltungLehrer());
		assertNull(wettbewerb.datumFreischaltungPrivat());
		assertNull(wettbewerb.wettbewerbsbeginn());
		assertNull(wettbewerb.wettbewerbsende());

	}

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
		assertEquals(erster, zweiter);
		assertEquals(erster.hashCode(), zweiter.hashCode());
		assertFalse(erster.equals(dritter));
	}

	@Test
	void should_ComparatorSortDescending() {

		// Arrange
		List<Wettbewerb> wettbewerbe = new ArrayList<>();
		List<Integer> jahre = Arrays.asList(new Integer[] { 2005, 2017, 2010 });

		jahre.forEach(jahr -> {

			wettbewerbe.add(new Wettbewerb(new WettbewerbID(jahr)));
		});

		assertEquals("2005", wettbewerbe.get(0).toString());
		assertEquals("2017", wettbewerbe.get(1).toString());
		assertEquals("2010", wettbewerbe.get(2).toString());

		// Act
		Collections.sort(wettbewerbe, new WettbewerbeDescendingComparator());

		// Assert
		assertEquals("2017", wettbewerbe.get(0).toString());
		assertEquals("2010", wettbewerbe.get(1).toString());
		assertEquals("2005", wettbewerbe.get(2).toString());

	}

}
