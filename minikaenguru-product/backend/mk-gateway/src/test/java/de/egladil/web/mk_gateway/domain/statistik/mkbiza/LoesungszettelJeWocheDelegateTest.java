//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities.WochenstatistikItem;

/**
 *
 */
public class LoesungszettelJeWocheDelegateTest {

	LoesungszettelJeWocheDelegate delegate = new LoesungszettelJeWocheDelegate();

	@Test
	void should_getAnzahlenAsList_returnUnchanged_when_maxWocheEqualsAnzahlWochen() {

		// arrange
		int anzahlWochen = 4;

		List<WochenstatistikItem> items = new ArrayList<>();
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(0);
			item.setWoche(1L);
			items.add(item);
		}
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(1);
			item.setWoche(2L);
			items.add(item);
		}
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(2);
			item.setWoche(3L);
			items.add(item);
		}
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(3);
			item.setWoche(4L);
			items.add(item);
		}

		// act
		List<Integer> result = delegate.getAnzahlenAsList(items, anzahlWochen);

		// assert
		assertEquals(anzahlWochen, result.size());

		List<Integer> errors = new ArrayList<>();

		for (int i = 0; i < result.size() - 1; i++) {
			if (i != result.get(i)) {
				errors.add(i);
			}
		}

		assertTrue(errors.isEmpty());
	}

	@Test
	void should_getAnzahlenAsList_addNulls_when_maxWocheLowerThanAnzahlWochen() {

		// arrange
		int anzahlWochen = 4;

		List<WochenstatistikItem> items = new ArrayList<>();
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(0);
			item.setWoche(1L);
			items.add(item);
		}
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(1);
			item.setWoche(2L);
			items.add(item);
		}

		// act
		List<Integer> result = delegate.getAnzahlenAsList(items, anzahlWochen);

		// assert
		assertEquals(anzahlWochen, result.size());

		List<Integer> errors = new ArrayList<>();

		if (result.get(0) != 0) {
			errors.add(0);
		}

		if (result.get(1) != 1) {
			errors.add(1);
		}

		if (result.get(2) != 0) {
			errors.add(2);
		}

		if (result.get(3) != 0) {
			errors.add(3);
		}

		assertTrue(errors.isEmpty());
	}

	@Test
	void should_getAnzahlenAsList_ThrowException_when_sizeLargerThanAnzahlWochen() {

		// arrange
		int anzahlWochen = 3;

		List<WochenstatistikItem> items = new ArrayList<>();
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(0);
			item.setWoche(1L);
			item.setWettbewerbUUID("2024");
			items.add(item);

		}
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(1);
			item.setWoche(2L);
			item.setWettbewerbUUID("2024");
			items.add(item);
		}
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(2);
			item.setWoche(3L);
			item.setWettbewerbUUID("2024");
			items.add(item);
		}
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(3);
			item.setWoche(4L);
			item.setWettbewerbUUID("2024");
			items.add(item);
		}

		// act
		try {
			delegate.getAnzahlenAsList(items, anzahlWochen);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {
			assertEquals("Anzahl WochenstatistikItem fuer Wettbewerb 2024 ist größer als 3", e.getMessage());
		}
	}

	@Test
	void should_berechneKumulierteWochenstatistik_work_whenSizeGleichAnzahlWochen() {

		// Arrange
		int anzahlWochen = 4;
		List<WochenstatistikItem> items = new ArrayList<>();
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(1);
			item.setWoche(1L);
			item.setWettbewerbUUID("2024");
			items.add(item);

		}
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(2);
			item.setWoche(2L);
			item.setWettbewerbUUID("2024");
			items.add(item);
		}
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(3);
			item.setWoche(3L);
			item.setWettbewerbUUID("2024");
			items.add(item);
		}
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(4);
			item.setWoche(4L);
			item.setWettbewerbUUID("2024");
			items.add(item);
		}

		// act
		List<MkBiZaGruppierungsitem> result = delegate.berechneKumulierteWochenstatistik(items, anzahlWochen);

		// assert
		assertEquals(4, result.size());

		{
			MkBiZaGruppierungsitem lz = result.get(0);
			assertEquals(1L, lz.getAnzahl());
			assertEquals("Woche 1", lz.getName());
		}

		{
			MkBiZaGruppierungsitem lz = result.get(1);
			assertEquals(3L, lz.getAnzahl());
			assertEquals("Woche 2", lz.getName());
		}

		{
			MkBiZaGruppierungsitem lz = result.get(2);
			assertEquals(6L, lz.getAnzahl());
			assertEquals("Woche 3", lz.getName());
		}

		{
			MkBiZaGruppierungsitem lz = result.get(3);
			assertEquals(10L, lz.getAnzahl());
			assertEquals("Woche 4", lz.getName());
		}

	}

	@Test
	void should_berechneKumulierteWochenstatistik_work_whenSizeKleinerAnzahlWochen() {

		// Arrange
		int anzahlWochen = 5;
		List<WochenstatistikItem> items = new ArrayList<>();
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(1);
			item.setWoche(1L);
			item.setWettbewerbUUID("2024");
			items.add(item);

		}
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(2);
			item.setWoche(2L);
			item.setWettbewerbUUID("2024");
			items.add(item);
		}
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(3);
			item.setWoche(3L);
			item.setWettbewerbUUID("2024");
			items.add(item);
		}
		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setAnzahlLoesungszettel(4);
			item.setWoche(4L);
			item.setWettbewerbUUID("2024");
			items.add(item);
		}

		// act
		List<MkBiZaGruppierungsitem> result = delegate.berechneKumulierteWochenstatistik(items, anzahlWochen);

		// assert
		assertEquals(5, result.size());

		{
			MkBiZaGruppierungsitem lz = result.get(0);
			assertEquals(1L, lz.getAnzahl());
			assertEquals("Woche 1", lz.getName());
		}

		{
			MkBiZaGruppierungsitem lz = result.get(1);
			assertEquals(3L, lz.getAnzahl());
			assertEquals("Woche 2", lz.getName());
		}

		{
			MkBiZaGruppierungsitem lz = result.get(2);
			assertEquals(6L, lz.getAnzahl());
			assertEquals("Woche 3", lz.getName());
		}

		{
			MkBiZaGruppierungsitem lz = result.get(3);
			assertEquals(10L, lz.getAnzahl());
			assertEquals("Woche 4", lz.getName());
		}

		{
			MkBiZaGruppierungsitem lz = result.get(4);
			assertEquals(10L, lz.getAnzahl());
			assertEquals("Woche 5", lz.getName());
		}
	}
}
