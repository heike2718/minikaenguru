// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;
import de.egladil.web.mkv_server_tests.LoesungszettelList;

/**
 * LoesungszettelHibernateRepositoryTest
 */
public class LoesungszettelHibernateRepositoryTest extends AbstractIT {

	private LoesungszettelRepository loesungszettelRepository;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		this.loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManager);
	}

	@Test
	void should_getAnzahlLoesungszettel_when_exist() {

		// Arrange
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.PRIVAT)
			.withTeilnahmenummer("AUFNUR0WEG").withWettbewerbID(new WettbewerbID(2017));

		// Act
		int anzahl = loesungszettelRepository.anzahlLoesungszettel(teilnahmeIdentifier);

		// Assert
		assertEquals(112, anzahl);

	}

	@Test
	void should_loadAll_when_exist() throws Exception {

		// Arrange
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer("M94P3IH9").withWettbewerbID(new WettbewerbID(2018));

		// Act
		List<Loesungszettel> trefferliste = loesungszettelRepository.loadAll(teilnahmeIdentifier);

		// Assert
		assertEquals(9, trefferliste.size());

		ObjectMapper objectMapper = new ObjectMapper();

		for (Loesungszettel loesungszettel : trefferliste) {

			assertNotNull(loesungszettel.auswertungsquelle());
			assertNotNull(loesungszettel.identifier());
			assertNotNull(loesungszettel.klassenstufe());
			assertNotNull(loesungszettel.sprache());

			LoesungszettelRohdaten rohdaten = loesungszettel.rohdaten();
			assertNotNull(rohdaten);

			if (rohdaten.antwortcode() == null) {

				assertEquals(Auswertungsquelle.UPLOAD, loesungszettel.auswertungsquelle());
			}

			assertNotNull(rohdaten.nutzereingabe());
			assertNotNull(rohdaten.wertungscode());

			TeilnahmeIdentifier theTeilnahmeIdentifier = loesungszettel.teilnahmeIdentifier();
			assertNotNull(theTeilnahmeIdentifier);
			assertEquals(teilnahmeIdentifier, theTeilnahmeIdentifier);
		}

		LoesungszettelList liste = new LoesungszettelList();
		liste.setLoesungszettel(trefferliste);

		objectMapper.writeValue(System.out, liste);
	}

}