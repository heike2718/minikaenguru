// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.kinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.kinder.impl.AdminKinderServiceImpl;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Gruppeninfo;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Gruppenitem;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * AdminKinderServiceImplIT
 */
public class AdminKinderServiceImplIT extends AbstractIntegrationTest {

	AdminKinderServiceImpl service;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		service = AdminKinderServiceImpl.createForIntegrationTest(entityManager);
	}

	@Test
	void should_createKurzstatistikKinderWork() {

		// Act
		Gruppeninfo gruppeninfo = service.createKurzstatistikKinder();

		// Assert
		assertEquals("KINDER", gruppeninfo.getUuid());
		List<Gruppenitem> items = gruppeninfo.getGruppenItems();
		assertEquals(4, items.size());

		Optional<Gruppenitem> optItem = items.stream().filter(i -> "importiert".equals(i.getName())).findFirst();
		assertTrue(optItem.isPresent());

		System.out.println(optItem.get().toString());

		optItem = items.stream().filter(i -> "Klassenstufe".equals(i.getName())).findFirst();
		assertTrue(optItem.isPresent());

		System.out.println(optItem.get().toString());

		optItem = items.stream().filter(i -> "Sprache".equals(i.getName())).findFirst();
		assertTrue(optItem.isPresent());

		System.out.println(optItem.get().toString());

		optItem = items.stream().filter(i -> "Teilnahmeart".equals(i.getName())).findFirst();
		assertTrue(optItem.isPresent());

		System.out.println(optItem.get().toString());

	}

}
