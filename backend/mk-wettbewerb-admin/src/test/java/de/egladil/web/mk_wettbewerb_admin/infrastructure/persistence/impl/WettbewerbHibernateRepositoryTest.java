// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.infrastructure.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_wettbewerb_admin.infrastructure.persistence.entities.PersistenterWettbewerb;

/**
 * WettbewerbHibernateRepositoryTest
 */
public class WettbewerbHibernateRepositoryTest {

	private WettbewerbHibernateRepository repository;

	private SimpleDateFormat sdf;

	@BeforeEach
	void setUp() {

		repository = new WettbewerbHibernateRepository();
		sdf = new SimpleDateFormat("dd.MM.yyyy");
	}

	@Test
	void should_mapAllAttributesButStatus_IgnoreStatus_when_WettbewerbsbeginnNotNull() {

		// Arrange
		Integer jahr = 2025;

		Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(jahr)).withStatus(WettbewerbStatus.BEENDET)
			.withWettbewerbsbeginn(LocalDate.of(jahr, Month.JANUARY, 1))
			.withDatumFreischaltungLehrer(LocalDate.of(jahr, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(jahr, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(jahr, Month.AUGUST, 1)).withStatus(WettbewerbStatus.DOWNLOAD_LEHRER);

		PersistenterWettbewerb persistenterWettbewerb = new PersistenterWettbewerb();
		persistenterWettbewerb.setUuid("2015");
		persistenterWettbewerb.setStatus(WettbewerbStatus.ANMELDUNG);

		// Act
		repository.mapAllAttributesButStatus(wettbewerb, persistenterWettbewerb);

		// Assert
		assertEquals("2015", persistenterWettbewerb.getUuid());
		assertEquals("01.01.2025", sdf.format(persistenterWettbewerb.getWettbewerbsbeginn()));
		assertEquals("01.03.2025", sdf.format(persistenterWettbewerb.getDatumFreischaltungLehrer()));
		assertEquals("01.06.2025", sdf.format(persistenterWettbewerb.getDatumFreischaltungPrivat()));
		assertEquals("01.08.2025", sdf.format(persistenterWettbewerb.getWettbewerbsende()));
		assertEquals(WettbewerbStatus.ANMELDUNG, persistenterWettbewerb.getStatus());

	}

	@Test
	void should_mapAllAttributesButStatus_IgnoreStatus_when_WettbewerbsbeginNull() {

		// Arrange
		Integer jahr = 2025;

		Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(jahr)).withStatus(WettbewerbStatus.BEENDET)
			.withDatumFreischaltungLehrer(LocalDate.of(jahr, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(jahr, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(jahr, Month.AUGUST, 1)).withStatus(WettbewerbStatus.DOWNLOAD_LEHRER);

		PersistenterWettbewerb persistenterWettbewerb = new PersistenterWettbewerb();
		persistenterWettbewerb.setUuid("2015");
		persistenterWettbewerb.setStatus(WettbewerbStatus.ANMELDUNG);

		// Act
		repository.mapAllAttributesButStatus(wettbewerb, persistenterWettbewerb);

		// Assert
		assertEquals("2015", persistenterWettbewerb.getUuid());
		assertEquals(null, persistenterWettbewerb.getWettbewerbsbeginn());
		assertEquals("01.03.2025", sdf.format(persistenterWettbewerb.getDatumFreischaltungLehrer()));
		assertEquals("01.06.2025", sdf.format(persistenterWettbewerb.getDatumFreischaltungPrivat()));
		assertEquals("01.08.2025", sdf.format(persistenterWettbewerb.getWettbewerbsende()));
		assertEquals(WettbewerbStatus.ANMELDUNG, persistenterWettbewerb.getStatus());
	}

	@Test
	void should_mapAllAttributesButStatus_SetLoesungsbuchstaben_when_DieseTeilweiseGefuellt() {

		// Arrange
		Integer jahr = 2025;

		Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(jahr)).withStatus(WettbewerbStatus.BEENDET)
			.withDatumFreischaltungLehrer(LocalDate.of(jahr, Month.MARCH, 1))
			.withDatumFreischaltungPrivat(LocalDate.of(jahr, Month.JUNE, 1))
			.withWettbewerbsende(LocalDate.of(jahr, Month.AUGUST, 1)).withStatus(WettbewerbStatus.DOWNLOAD_LEHRER)
			.withLoesungsbuchstabenKlasse1("CDAD-ECCC-BCDE")
			.withLoesungsbuchstabenKlasse2("EACDD-CDCCE-BACBA");

		PersistenterWettbewerb persistenterWettbewerb = new PersistenterWettbewerb();
		persistenterWettbewerb.setUuid("2015");
		persistenterWettbewerb.setStatus(WettbewerbStatus.ANMELDUNG);

		// Act
		repository.mapAllAttributesButStatus(wettbewerb, persistenterWettbewerb);

		// Assert
		assertEquals("2015", persistenterWettbewerb.getUuid());
		assertEquals(null, persistenterWettbewerb.getWettbewerbsbeginn());
		assertEquals("01.03.2025", sdf.format(persistenterWettbewerb.getDatumFreischaltungLehrer()));
		assertEquals("01.06.2025", sdf.format(persistenterWettbewerb.getDatumFreischaltungPrivat()));
		assertEquals("01.08.2025", sdf.format(persistenterWettbewerb.getWettbewerbsende()));
		assertNull(persistenterWettbewerb.getLoesungsbuchstabenIkids());
		assertEquals("CDAD-ECCC-BCDE", persistenterWettbewerb.getLoesungsbuchstabenKlasse1());
		assertEquals("EACDD-CDCCE-BACBA", persistenterWettbewerb.getLoesungsbuchstabenKlasse2());
		assertEquals(WettbewerbStatus.ANMELDUNG, persistenterWettbewerb.getStatus());
	}

	@Test
	void should_MapFromPersistenterWettbewerb_MapAllArttributes_when_LoesungsbuchstabenTeilweiseGefuellt() throws ParseException {

		// Arrange
		PersistenterWettbewerb persistenterWettbewerb = new PersistenterWettbewerb();
		persistenterWettbewerb.setUuid("2025");
		persistenterWettbewerb.setStatus(WettbewerbStatus.ERFASST);
		persistenterWettbewerb.setWettbewerbsbeginn(sdf.parse("01.01.2025"));
		persistenterWettbewerb.setWettbewerbsende(sdf.parse("01.08.2025"));
		persistenterWettbewerb.setDatumFreischaltungLehrer(sdf.parse("01.03.2025"));
		persistenterWettbewerb.setDatumFreischaltungPrivat(sdf.parse("01.06.2025"));
		persistenterWettbewerb.setLoesungsbuchstabenKlasse1("CDAD-ECCC-BCDE");
		persistenterWettbewerb.setLoesungsbuchstabenKlasse2("EACDD-CDCCE-BACBA");

		// Act
		Wettbewerb wettbewerb = repository.mapFromPersistenterWettbewerb(persistenterWettbewerb);

		// Assert
		assertEquals(Integer.valueOf(2025), wettbewerb.id().jahr());
		assertEquals(WettbewerbStatus.ERFASST, wettbewerb.status());
		assertEquals("01.01.2025", CommonTimeUtils.format(wettbewerb.wettbewerbsbeginn()));
		assertEquals("01.03.2025", CommonTimeUtils.format(wettbewerb.datumFreischaltungLehrer()));
		assertEquals("01.06.2025", CommonTimeUtils.format(wettbewerb.datumFreischaltungPrivat()));
		assertEquals("01.08.2025", CommonTimeUtils.format(wettbewerb.wettbewerbsende()));
		assertNull(wettbewerb.loesungsbuchstabenIkids());
		assertEquals("CDAD-ECCC-BCDE", wettbewerb.loesungsbuchstabenKlasse1());
		assertEquals("EACDD-CDCCE-BACBA", wettbewerb.loesungsbuchstabenKlasse2());

	}

}
