// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.testutils.LoesungszettelList;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * LoesungszettelHibernateRepositoryTest
 */
@QuarkusTest
public class LoesungszettelHibernateRepositoryTest {

	@Inject
	LoesungszettelHibernateRepository loesungszettelRepository;

	@Test
	void should_anzahlForWettbewerb_work() {

		// Act
		long result = loesungszettelRepository.anzahlForWettbewerb(new WettbewerbID(2018));

		// Assert
		assertEquals(230L, result);

	}

	@Test
	void should_loadLoadPageForWettbewerb_work() {

		// Arrange
		WettbewerbID wettbewerbID = new WettbewerbID(2018);

		// Act
		List<Loesungszettel> result = loesungszettelRepository.loadLoadPageForWettbewerb(wettbewerbID, 20, 35);

		// Assert
		assertEquals(20, result.size());

	}

	@Test
	void should_loadLoadPageForWettbewerb_work_when_trefferlsteEmpty() {

		// Arrange
		WettbewerbID wettbewerbID = new WettbewerbID(2015);

		// Act
		List<Loesungszettel> result = loesungszettelRepository.loadLoadPageForWettbewerb(wettbewerbID, 20, 35);

		// Assert
		assertEquals(0, result.size());

	}

	@Test
	void should_findLoesungszettelWithKindIdReturnTheLoesungszettel_when_entityIsPresent() {

		// Arrange
		Identifier kindID = new Identifier("86582bfc-168e-49af-a5f2-71bf36b23603");

		// Act
		Optional<Loesungszettel> optResult = loesungszettelRepository.findLoesungszettelWithKindID(kindID);

		// Assert
		assertTrue(optResult.isPresent());

		Loesungszettel result = optResult.get();
		assertEquals(kindID, result.kindID());
		assertEquals(0, result.version());
		assertEquals(new Identifier("23ab789a-b569-4843-82d5-c1c2da727f37"), result.identifier());

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
	void should_loadAllWithTeilnahmeId_when_exist() throws Exception {

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

		// objectMapper.writeValue(System.out, liste);
	}

	@Test
	void should_getAuswertungsquellenWithAnzahlForWettbewerbWork_when_Both() {

		// Arrange
		WettbewerbID wettbewerbID = new WettbewerbID(2019);

		// Act
		List<Pair<Auswertungsquelle, Integer>> result = loesungszettelRepository.getAuswertungsquelleMitAnzahl(wettbewerbID);

		// Assert
		assertEquals(2, result.size());

		Optional<Pair<Auswertungsquelle, Integer>> optOnline = result.stream().filter(p -> Auswertungsquelle.ONLINE == p.getLeft())
			.findFirst();
		assertTrue(optOnline.isPresent());
		assertEquals(7, optOnline.get().getRight().intValue());

		Optional<Pair<Auswertungsquelle, Integer>> optUpload = result.stream().filter(p -> Auswertungsquelle.UPLOAD == p.getLeft())
			.findFirst();
		assertTrue(optUpload.isPresent());
		assertEquals(48, optUpload.get().getRight().intValue());
	}

	@Test
	void should_getAuswertungsquellenWithAnzahlForWettbewerbWork_when_NoLoesungszettel() {

		// Arrange
		WettbewerbID wettbewerbID = new WettbewerbID(2021);

		// Act
		List<Pair<Auswertungsquelle, Integer>> result = loesungszettelRepository.getAuswertungsquelleMitAnzahl(wettbewerbID);

		// Assert
		assertEquals(0, result.size());
	}

	@Test
	void should_getAuswertungsquellenWithAnzahlForWettbewerbWork_when_OnlyUpload() {

		// Arrange
		WettbewerbID wettbewerbID = new WettbewerbID(2010);

		// Act
		List<Pair<Auswertungsquelle, Integer>> result = loesungszettelRepository.getAuswertungsquelleMitAnzahl(wettbewerbID);

		// Assert
		assertEquals(1, result.size());

		Pair<Auswertungsquelle, Integer> treffer = result.get(0);
		assertEquals(Auswertungsquelle.UPLOAD, treffer.getLeft());
		assertEquals(142, treffer.getRight().intValue());
	}

}
