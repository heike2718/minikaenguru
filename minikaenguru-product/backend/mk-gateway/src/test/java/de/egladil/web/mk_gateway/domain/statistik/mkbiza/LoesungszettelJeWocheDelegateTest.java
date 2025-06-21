//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities.KalenderwochenIntervall;
import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities.WochenstatistikItem;

/**
 *
 */
public class LoesungszettelJeWocheDelegateTest {

	LoesungszettelJeWocheDelegate delegate = new LoesungszettelJeWocheDelegate();

	@Nested
	class BerechneKumulierteWochenstatistikTests {

		@Test
		void should_berechneKumulierteWochenstatistik_work_when_treffermengeLeer() {

			// arrange
			KalenderwochenIntervall intervall = new KalenderwochenIntervall(1, 7);

			// assert
			List<MkBiZaGruppierungsitem> result = delegate.berechneKumulierteWochenstatistik(new ArrayList<>(), intervall);

			// @formatter:off
			assertAll(() -> assertEquals(7, result.size()),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 1", 0), result.get(0)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 2", 0), result.get(1)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 3", 0), result.get(2)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 4", 0), result.get(3)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 5", 0), result.get(4)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 6", 0), result.get(5)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 7", 0), result.get(6)));
			// @formatter:on
		}

		@Test
		void should_berechneKumulierteWochenstatistik_work_when_luecken_und_weniger_wochen() {

			// arrange
			List<WochenstatistikItem> persistenteWochenstatistiken = new ArrayList<>();
			{
				WochenstatistikItem item = new WochenstatistikItem();
				item.setWoche(2);
				item.setAnzahlLoesungszettel(2);
				persistenteWochenstatistiken.add(item);

			}
			{
				WochenstatistikItem item = new WochenstatistikItem();
				item.setWoche(4);
				item.setAnzahlLoesungszettel(4);
				persistenteWochenstatistiken.add(item);

			}
			{
				WochenstatistikItem item = new WochenstatistikItem();
				item.setWoche(6);
				item.setAnzahlLoesungszettel(6);
				persistenteWochenstatistiken.add(item);
			}

			KalenderwochenIntervall intervall = new KalenderwochenIntervall(1, 7);

			// act
			List<MkBiZaGruppierungsitem> result = delegate.berechneKumulierteWochenstatistik(persistenteWochenstatistiken,
				intervall);

			// assert
			assertEquals(7, result.size());
			assertEquals(new MkBiZaGruppierungsitem("Woche 1", 0), result.get(0));
			assertEquals(new MkBiZaGruppierungsitem("Woche 2", 2), result.get(1));
			assertEquals(new MkBiZaGruppierungsitem("Woche 3", 2), result.get(2));
			assertEquals(new MkBiZaGruppierungsitem("Woche 4", 6), result.get(3));
			assertEquals(new MkBiZaGruppierungsitem("Woche 5", 6), result.get(4));
			assertEquals(new MkBiZaGruppierungsitem("Woche 6", 12), result.get(5));
			assertEquals(new MkBiZaGruppierungsitem("Woche 7", 12), result.get(6));

		}

		@Test
		void should_berechneKumulierteWochenstatistik_workFor2021() {

			// arrange: absichtlich nicht sortiert, falls im Repo das order by nicht klappte.
			List<WochenstatistikItem> persistenteWochenstatistiken = new ArrayList<>();
			persistenteWochenstatistiken.add(new WochenstatistikItem(13, 302));
			persistenteWochenstatistiken.add(new WochenstatistikItem(14, 446));
			persistenteWochenstatistiken.add(new WochenstatistikItem(15, 417));
			persistenteWochenstatistiken.add(new WochenstatistikItem(16, 442));
			persistenteWochenstatistiken.add(new WochenstatistikItem(17, 280));
			persistenteWochenstatistiken.add(new WochenstatistikItem(18, 293));
			persistenteWochenstatistiken.add(new WochenstatistikItem(19, 201));
			persistenteWochenstatistiken.add(new WochenstatistikItem(10, 113));
			persistenteWochenstatistiken.add(new WochenstatistikItem(11, 862));
			persistenteWochenstatistiken.add(new WochenstatistikItem(12, 1603));
			persistenteWochenstatistiken.add(new WochenstatistikItem(20, 266));
			persistenteWochenstatistiken.add(new WochenstatistikItem(21, 361));
			persistenteWochenstatistiken.add(new WochenstatistikItem(22, 218));
			persistenteWochenstatistiken.add(new WochenstatistikItem(23, 385));
			persistenteWochenstatistiken.add(new WochenstatistikItem(24, 392));
			persistenteWochenstatistiken.add(new WochenstatistikItem(25, 378));
			persistenteWochenstatistiken.add(new WochenstatistikItem(26, 164));
			persistenteWochenstatistiken.add(new WochenstatistikItem(27, 209));
			persistenteWochenstatistiken.add(new WochenstatistikItem(28, 179));
			persistenteWochenstatistiken.add(new WochenstatistikItem(29, 99));
			persistenteWochenstatistiken.add(new WochenstatistikItem(30, 30));
			persistenteWochenstatistiken.add(new WochenstatistikItem(31, 9));
			persistenteWochenstatistiken.add(new WochenstatistikItem(32, 128));
			persistenteWochenstatistiken.add(new WochenstatistikItem(35, 25));

			KalenderwochenIntervall intervall = new KalenderwochenIntervall(10, 35);

			// act
			List<MkBiZaGruppierungsitem> result = delegate.berechneKumulierteWochenstatistik(persistenteWochenstatistiken,
				intervall);

			// assert
			// @formatter:off
			assertAll(() -> assertEquals(26, result.size()),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 10", 113), result.get(0)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 11", 975), result.get(1)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 12",2578), result.get(2)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 13",2880), result.get(3)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 14",3326), result.get(4)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 15",3743), result.get(5)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 16",4185), result.get(6)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 17",4465), result.get(7)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 18",4758), result.get(8)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 19",4959), result.get(9)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 20",5225), result.get(10)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 21",5586), result.get(11)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 22",5804), result.get(12)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 23",6189), result.get(13)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 24",6581), result.get(14)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 25",6959), result.get(15)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 26",7123), result.get(16)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 27",7332), result.get(17)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 28",7511), result.get(18)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 29",7610), result.get(19)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 30",7640), result.get(20)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 31",7649), result.get(21)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 32",7777), result.get(22)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 33",7777), result.get(23)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 34",7777), result.get(24)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 35",7802), result.get(25)));
			// @formatter:on
		}

	}

	@Nested
	class NormalisierungTests {

		@Test
		void should_normalisiereTrefferliste_return_emptyArray_when_emptyList() {

			// act
			List<MkBiZaGruppierungsitem> result = delegate.normalisiereTrefferliste(new ArrayList<>());

			// assert
			assertTrue(result.isEmpty());
		}

		@Test
		void should_normalisiereTrefferliste_work_when_keineLuecken() {

			// arrange
			List<WochenstatistikItem> persistenteWochenstatistiken = new ArrayList<>();
			{
				WochenstatistikItem item = new WochenstatistikItem();
				item.setWoche(2);
				item.setAnzahlLoesungszettel(2);
				persistenteWochenstatistiken.add(item);

			}
			{
				WochenstatistikItem item = new WochenstatistikItem();
				item.setWoche(3);
				item.setAnzahlLoesungszettel(3);
				persistenteWochenstatistiken.add(item);

			}
			{
				WochenstatistikItem item = new WochenstatistikItem();
				item.setWoche(4);
				item.setAnzahlLoesungszettel(4);
				persistenteWochenstatistiken.add(item);

			}

			// act
			List<MkBiZaGruppierungsitem> result = delegate.normalisiereTrefferliste(persistenteWochenstatistiken);

			// assert
			assertEquals(3, result.size());

			{
				MkBiZaGruppierungsitem item = result.get(0);
				assertEquals(2L, item.getAnzahl());
				assertEquals("Woche 2", item.getName());
			}

			{
				MkBiZaGruppierungsitem item = result.get(1);
				assertEquals(3L, item.getAnzahl());
				assertEquals("Woche 3", item.getName());
			}

			{
				MkBiZaGruppierungsitem item = result.get(2);
				assertEquals(4L, item.getAnzahl());
				assertEquals("Woche 4", item.getName());
			}
		}

		@Test
		void should_normalisiereTrefferliste_work_when_luecken_test1() {

			// arrange
			List<WochenstatistikItem> persistenteWochenstatistiken = new ArrayList<>();
			{
				WochenstatistikItem item = new WochenstatistikItem();
				item.setWoche(2);
				item.setAnzahlLoesungszettel(2);
				persistenteWochenstatistiken.add(item);

			}
			{
				WochenstatistikItem item = new WochenstatistikItem();
				item.setWoche(4);
				item.setAnzahlLoesungszettel(4);
				persistenteWochenstatistiken.add(item);

			}
			{
				WochenstatistikItem item = new WochenstatistikItem();
				item.setWoche(6);
				item.setAnzahlLoesungszettel(6);
				persistenteWochenstatistiken.add(item);
			}

			// act
			List<MkBiZaGruppierungsitem> result = delegate.normalisiereTrefferliste(persistenteWochenstatistiken);

			// assert
			assertEquals(5, result.size());

			{
				MkBiZaGruppierungsitem item = result.get(0);
				assertEquals(2L, item.getAnzahl());
				assertEquals("Woche 2", item.getName());
			}

			{
				MkBiZaGruppierungsitem item = result.get(1);
				assertEquals(0, item.getAnzahl());
				assertEquals("Woche 3", item.getName());
			}

			{
				MkBiZaGruppierungsitem item = result.get(2);
				assertEquals(4L, item.getAnzahl());
				assertEquals("Woche 4", item.getName());
			}

			{
				MkBiZaGruppierungsitem item = result.get(3);
				assertEquals(0, item.getAnzahl());
				assertEquals("Woche 5", item.getName());
			}

			{
				MkBiZaGruppierungsitem item = result.get(4);
				assertEquals(6L, item.getAnzahl());
				assertEquals("Woche 6", item.getName());
			}
		}

		@Test
		void should_normalisiereTrefferliste_work_when_luecken_test2() {

			// arrange
			List<WochenstatistikItem> persistenteWochenstatistiken = new ArrayList<>();
			{
				WochenstatistikItem item = new WochenstatistikItem();
				item.setWoche(2);
				item.setAnzahlLoesungszettel(2);
				persistenteWochenstatistiken.add(item);

			}
			{
				WochenstatistikItem item = new WochenstatistikItem();
				item.setWoche(3);
				item.setAnzahlLoesungszettel(3);
				persistenteWochenstatistiken.add(item);

			}
			{
				WochenstatistikItem item = new WochenstatistikItem();
				item.setWoche(6);
				item.setAnzahlLoesungszettel(6);
				persistenteWochenstatistiken.add(item);
			}

			// act
			List<MkBiZaGruppierungsitem> result = delegate.normalisiereTrefferliste(persistenteWochenstatistiken);

			// assert
			assertEquals(5, result.size());

			{
				MkBiZaGruppierungsitem item = result.get(0);
				assertEquals(2, item.getAnzahl());
				assertEquals("Woche 2", item.getName());
			}

			{
				MkBiZaGruppierungsitem item = result.get(1);
				assertEquals(3, item.getAnzahl());
				assertEquals("Woche 3", item.getName());
			}

			{
				MkBiZaGruppierungsitem item = result.get(2);
				assertEquals(0, item.getAnzahl());
				assertEquals("Woche 4", item.getName());
			}

			{
				MkBiZaGruppierungsitem item = result.get(3);
				assertEquals(0, item.getAnzahl());
				assertEquals("Woche 5", item.getName());
			}

			{
				MkBiZaGruppierungsitem item = result.get(4);
				assertEquals(6L, item.getAnzahl());
				assertEquals("Woche 6", item.getName());
			}
		}

	}

	@Nested
	class KumuliereAnzahlenTests {

		@Test
		void shall_kumulieren_when_nullen() {

			// arrange
			List<MkBiZaGruppierungsitem> gruppen = new ArrayList<>();
			gruppen.add(new MkBiZaGruppierungsitem().withAnzahl(1).withName("Woche 1"));
			gruppen.add(new MkBiZaGruppierungsitem().withAnzahl(2).withName("Woche 2"));
			gruppen.add(new MkBiZaGruppierungsitem().withAnzahl(0).withName("Woche 3"));
			gruppen.add(new MkBiZaGruppierungsitem().withAnzahl(4).withName("Woche 4"));
			gruppen.add(new MkBiZaGruppierungsitem().withAnzahl(5).withName("Woche 5"));

			// act
			List<Integer> kumuliereAnzahlen = delegate.kumuliereAnzahlen(gruppen);

			// assert
			assertAll(() -> assertEquals(5, kumuliereAnzahlen.size()), () -> assertEquals(1, kumuliereAnzahlen.get(0)),
				() -> assertEquals(3, kumuliereAnzahlen.get(1)), () -> assertEquals(3, kumuliereAnzahlen.get(2)),
				() -> assertEquals(7, kumuliereAnzahlen.get(3)), () -> assertEquals(12, kumuliereAnzahlen.get(4)));
		}

		@Test
		void shall_kumulieren_when_nurEinElement() {

			// arrange
			List<MkBiZaGruppierungsitem> gruppen = new ArrayList<>();
			gruppen.add(new MkBiZaGruppierungsitem().withAnzahl(10).withName("Woche 1"));

			// act
			List<Integer> kumuliereAnzahlen = delegate.kumuliereAnzahlen(gruppen);

			// assert
			assertAll(() -> assertEquals(1, kumuliereAnzahlen.size()), () -> assertEquals(10, kumuliereAnzahlen.get(0)));
		}

		@Test
		void shall_kumulieren_when_emptyList() {

			// arrange
			List<MkBiZaGruppierungsitem> gruppen = new ArrayList<>();
			// act
			List<Integer> kumuliereAnzahlen = delegate.kumuliereAnzahlen(gruppen);

			// assert
			assertEquals(0, kumuliereAnzahlen.size());
		}
	}

	@Nested
	class MapFromDBTests {

		@Test
		void should_mapListFromDB_work() {

			// arrange
			List<WochenstatistikItem> persistenteWochenstatistiken = new ArrayList<>();
			persistenteWochenstatistiken.add(new WochenstatistikItem(13, 302));
			persistenteWochenstatistiken.add(new WochenstatistikItem(14, 446));

			// act
			List<MkBiZaGruppierungsitem> result = delegate.mapFromDB(persistenteWochenstatistiken);

			// assert
			assertAll(() -> assertEquals(2, result.size()),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 13", 302), result.get(0)),
				() -> assertEquals(new MkBiZaGruppierungsitem("Woche 14", 446), result.get(1)));

		}

		@Test
		void should_mapListFromDB_returnEmptyList_when_dbListEmpty() {

			// act
			List<MkBiZaGruppierungsitem> result = delegate.mapFromDB(new ArrayList<>());

			// assert
			assertTrue(result.isEmpty());
		}
	}

}
