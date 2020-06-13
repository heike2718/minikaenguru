// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.impl;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.PersistenterWettbewerb;

/**
 * WettbewerbeHibernateRepositoryTest
 */
public class WettbewerbeHibernateRepositoryTest {

	@Test
	void should_MapFromPersistenterWettbewerb_MapAllArttributes() throws ParseException {

		final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

		// Arrange
		PersistenterWettbewerb persistenterWettbewerb = new PersistenterWettbewerb();
		persistenterWettbewerb.setUuid("2025");
		persistenterWettbewerb.setStatus(WettbewerbStatus.ERFASST);
		persistenterWettbewerb.setWettbewerbsbeginn(sdf.parse("01.01.2025"));
		persistenterWettbewerb.setWettbewerbsende(sdf.parse("01.08.2025"));
		persistenterWettbewerb.setDatumFreischaltungLehrer(sdf.parse("01.03.2025"));
		persistenterWettbewerb.setDatumFreischaltungPrivat(sdf.parse("01.06.2025"));

		// Act
		Wettbewerb wettbewerb = new WettbewerbHibernateRepository().mapFromPersistenterWettbewerb(persistenterWettbewerb);

		// Assert
		assertEquals(Integer.valueOf(2025), wettbewerb.id().jahr());
		assertEquals(WettbewerbStatus.ERFASST, wettbewerb.status());
		assertEquals("01.01.2025", CommonTimeUtils.format(wettbewerb.wettbewerbsbeginn()));
		assertEquals("01.03.2025", CommonTimeUtils.format(wettbewerb.datumFreischaltungLehrer()));
		assertEquals("01.06.2025", CommonTimeUtils.format(wettbewerb.datumFreischaltungPrivat()));
		assertEquals("01.08.2025", CommonTimeUtils.format(wettbewerb.wettbewerbsende()));

	}
}
