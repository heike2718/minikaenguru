//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 *
 */
public class WochenstatistikItemTest {

	@Test
	void should_sortByWoche_test1() {

		WochenstatistikItem item1 = new WochenstatistikItem();
		item1.setWoche(0L);

		WochenstatistikItem item2 = new WochenstatistikItem();
		item2.setWoche(3L);

		assertTrue(item1.compareTo(item2) < 0);

	}

	@Test
	void should_sortByWoche_test2() {

		WochenstatistikItem item1 = new WochenstatistikItem();
		item1.setWoche(3L);
		item1.setWettbewerbUUID("2021");

		WochenstatistikItem item2 = new WochenstatistikItem();
		item2.setWoche(3L);
		item2.setWettbewerbUUID("2021");

		assertTrue(item1.compareTo(item2) == 0);

	}

	@Test
	void should_sortByWoche_test3() {

		WochenstatistikItem item1 = new WochenstatistikItem();
		item1.setWoche(22L);

		WochenstatistikItem item2 = new WochenstatistikItem();
		item2.setWoche(2L);

		assertTrue(item1.compareTo(item2) > 0);
	}

	@Test
	void testSort() {

		List<WochenstatistikItem> items = new ArrayList<>();

		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setWoche(22L);
			items.add(item);
		}

		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setWoche(1L);
			items.add(item);
		}

		{
			WochenstatistikItem item = new WochenstatistikItem();
			item.setWoche(5L);
			items.add(item);
		}

		Collections.sort(items);

		assertAll(() -> assertEquals(1L, items.get(0).getWoche()), () -> assertEquals(5L, items.get(1).getWoche()),
			() -> assertEquals(22L, items.get(2).getWoche()));

	}
}
