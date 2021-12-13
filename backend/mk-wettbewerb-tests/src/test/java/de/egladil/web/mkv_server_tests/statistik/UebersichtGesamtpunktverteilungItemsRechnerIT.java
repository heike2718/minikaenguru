// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.statistik;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.statistik.GesamtpunktverteilungItem;
import de.egladil.web.mk_gateway.domain.statistik.VerteilungRechner;
import de.egladil.web.mk_gateway.domain.statistik.impl.UebersichtGesamtpunktverteilungItemsRechner;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * UebersichtGesamtpunktverteilungItemsRechnerIT
 */
public class UebersichtGesamtpunktverteilungItemsRechnerIT extends AbstractIntegrationTest {

	private LoesungszettelHibernateRepository loesungszettelRepository;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManager);
	}

	@Test
	void should_berechneGesamtpunktverteilungItems_work() {

		// Arrange

		WettbewerbID wettbewerbID = new WettbewerbID(2018);
		Klassenstufe klassenstufe = Klassenstufe.EINS;
		List<Loesungszettel> zettelKlassenstufe = loesungszettelRepository.loadAllForWettbewerbAndKlassenstufe(wettbewerbID,
			klassenstufe);

		UebersichtGesamtpunktverteilungItemsRechner rechner = new UebersichtGesamtpunktverteilungItemsRechner();

		// Act
		List<GesamtpunktverteilungItem> items = rechner.berechneGesamtpunktverteilungItems(wettbewerbID, klassenstufe,
			zettelKlassenstufe);

		assertEquals(13, items.size());

		// Act 2
		new VerteilungRechner().addProzentraengeToGesamtpunktverteilungItems(items, zettelKlassenstufe.size());

	}

}
