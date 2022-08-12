// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.ConcurrentModificationType;
import de.egladil.web.mk_gateway.domain.error.EntityConcurrentlyModifiedException;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;
import de.egladil.web.mkv_server_tests.LoesungszettelList;

/**
 * LoesungszettelHibernateRepositoryIT
 */
public class LoesungszettelHibernateRepositoryIT extends AbstractIntegrationTest {

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
	void should_loadAllWithTeilnahmenummer_when_exist() throws Exception {

		// Arrange
		String teilnahmenummer = "M94P3IH9";
		WettbewerbID wettbewerbID = new WettbewerbID(2018);

		// Act
		List<Loesungszettel> trefferliste = loesungszettelRepository.loadAllWithTeilnahmenummerForWettbewerb(teilnahmenummer,
			wettbewerbID);

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
			assertEquals(teilnahmenummer, theTeilnahmeIdentifier.teilnahmenummer());
			assertEquals(wettbewerbID.jahr().intValue(), theTeilnahmeIdentifier.jahr());
		}

		LoesungszettelList liste = new LoesungszettelList();
		liste.setLoesungszettel(trefferliste);

		// objectMapper.writeValue(System.out, liste);
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
	void should_addLoesungszettelThrowEntityConcurrentlyModified_when_thereIsAnEntityWithThisKindID() {

		// Arrange
		Identifier kindID = new Identifier("64eb1db7-630b-43d7-8049-f8f75e765522");

		String expectedNutzereingabe = "CEBENNECBCAE";

		Optional<Loesungszettel> optResult = loesungszettelRepository.findLoesungszettelWithKindID(kindID);
		assertTrue(optResult.isPresent(), "DB muss zurückgesetzt werden");

		Loesungszettel loesungszettel = optResult.get();
		assertEquals(kindID, loesungszettel.kindID(), "DB muss zurückgesetzt werden");
		assertEquals(new Identifier("219c7a7a-c701-4fcf-997f-398c597a09f0"),
			loesungszettel.identifier(), "DB muss zurückgesetzt werden");
		assertEquals(expectedNutzereingabe, loesungszettel.rohdaten().nutzereingabe(), "DB muss zurückgesetzt werden");
		assertEquals(0, loesungszettel.version(), "DB muss zurückgesetzt werden");

		// Act
		try {

			this.addLoesungszettel(loesungszettel);
			fail("keine EntityConcurrentlyModifiedException");
		} catch (EntityConcurrentlyModifiedException e) {

			assertEquals(ConcurrentModificationType.INSERTED, e.getModificationType());
			Loesungszettel derInDerDatenbank = (Loesungszettel) e.getActualEntity();
			assertEquals(expectedNutzereingabe, derInDerDatenbank.rohdaten().nutzereingabe());
			assertEquals(0, derInDerDatenbank.version());
		}
	}

	@Test
	void should_updateLoesungszettelThrowEntityConcurrentlyModified_when_readWithLowerVersion() {

		// Arrange
		String loesungszettelUuid = "ee3faf21-6c8f-4270-b75f-6ed654c21fdb";
		Optional<Loesungszettel> optVorhandener = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));

		String expectedNutzereingabe = "BADNACACBENNNNC";

		assertTrue(optVorhandener.isPresent(), "DB muss zurückgesetzt werden");
		Loesungszettel vorhandener = optVorhandener.get();

		assertEquals(3, vorhandener.version(), "DB muss zurückgesetzt werden");
		assertEquals(expectedNutzereingabe, vorhandener.rohdaten().nutzereingabe(),
			"DB muss zurückgesetzt werden");

		LoesungszettelRohdaten rohdaten = vorhandener.rohdaten().withNutzereingabe("HALLOHALLOHALLO");
		vorhandener = vorhandener.withVersion(2).withRohdaten(rohdaten);

		// Act
		try {

			this.updateLoesungszettel(vorhandener);
			fail("keine EntityConcurrentlyModifiedException");
		} catch (EntityConcurrentlyModifiedException e) {

			assertEquals(ConcurrentModificationType.UPDATED, e.getModificationType());
			Loesungszettel derInDerDatenbank = (Loesungszettel) e.getActualEntity();

			assertEquals(expectedNutzereingabe, derInDerDatenbank.rohdaten().nutzereingabe());
			assertEquals(3, derInDerDatenbank.version());

		}

	}

	//

	@Test
	void should_updateLoesungszettelReturnNewVersion_when_readWithEqualVersion() {

		// Arrange
		String loesungszettelUuid = "2fbbc124-74be-4121-b62f-0a5c3a176964";
		Optional<Loesungszettel> optVorhandener = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));

		String expectedNutzereingabe = "AAAABBBBCCCC";

		assertTrue(optVorhandener.isPresent(), "DB muss zurückgesetzt werden");
		Loesungszettel vorhandener = optVorhandener.get();

		assertEquals(1, vorhandener.version(), "DB muss zurückgesetzt werden");
		assertEquals("ANNCNNNNNNNN", vorhandener.rohdaten().nutzereingabe(),
			"DB muss zurückgesetzt werden");

		LoesungszettelRohdaten rohdaten = vorhandener.rohdaten().withNutzereingabe("AAAABBBBCCCC")
			.withAntwortcode("AAAABBBBCCCC")
			.withWertungscode("fffffffffrff");

		vorhandener = vorhandener.withVersion(1).withRohdaten(rohdaten).withPunkte(625).withLaengeKaengurusprung(1);

		// Act
		Loesungszettel geaenderter = this.updateLoesungszettel(vorhandener);

		// Assert
		assertEquals(2, geaenderter.version());
		assertEquals(expectedNutzereingabe, geaenderter.rohdaten().nutzereingabe());

	}

	@Test
	void should_removeLoesungszettelReturnTheLatestVersionOfTheLoesungszettel_when_loesungszettelIsPresent() {

		// Arrange
		String loesungszettelUuid = "c5f54cc3-db7f-4c2e-a03c-680b16311e83";
		Optional<Loesungszettel> optVorhandener = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));

		assertTrue(optVorhandener.isPresent(), "DB muss zurückgesetzt werden");

		// Act
		Optional<PersistenterLoesungszettel> optResult = this.removeLoesungszettel(optVorhandener.get());

		// Assert
		assertTrue(optResult.isPresent());
		PersistenterLoesungszettel result = optResult.get();
		assertEquals("EDBABADCCBEC", result.getNutzereingabe());
		assertEquals("8308ece5-39e5-42ed-b625-d3c0a8485f41", result.getKindID());

		Optional<Loesungszettel> optJetzt = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));
		assertTrue(optJetzt.isEmpty());
	}

	@Test
	void should_removeLoesungszettelTolerate_when_loesungszettelIsAbsent() {

		// Arrange
		String loesungszettelUuid = "zzzzz-db7f-4c2e-a03c-680b16311e83";
		Loesungszettel loesungszettel = new Loesungszettel().withIdentifier(new Identifier(loesungszettelUuid));

		assertTrue(loesungszettelRepository.ofID(loesungszettel.identifier()).isEmpty());

		// Act
		Optional<PersistenterLoesungszettel> optResult = this.removeLoesungszettel(loesungszettel);

		// Assert
		assertTrue(optResult.isEmpty());
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

	private Loesungszettel addLoesungszettel(final Loesungszettel loesungszettel) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();
			Loesungszettel result = loesungszettelRepository.addLoesungszettel(loesungszettel);
			commit(trx);

			return result;

		} catch (PersistenceException e) {

			rollback(trx);
			System.err.println("transaction rolled back");
			e.printStackTrace();
			throw e;
		} catch (EntityConcurrentlyModifiedException e) {

			rollback(trx);
			System.err.println("transaction rolled back");
			throw e;
		}

	}

	private Loesungszettel updateLoesungszettel(final Loesungszettel loesungszettel) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();
			Loesungszettel result = loesungszettelRepository.updateLoesungszettel(loesungszettel);
			commit(trx);

			return result;

		} catch (PersistenceException e) {

			rollback(trx);
			System.err.println("transaction rolled back");
			e.printStackTrace();
			throw e;
		} catch (EntityConcurrentlyModifiedException e) {

			rollback(trx);
			System.err.println("transaction rolled back");
			throw e;
		}

	}

	private Optional<PersistenterLoesungszettel> removeLoesungszettel(final Loesungszettel loesungszettel) {

		//

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();
			Optional<PersistenterLoesungszettel> result = loesungszettelRepository
				.removeLoesungszettel(loesungszettel.identifier());
			commit(trx);

			return result;

		} catch (PersistenceException e) {

			rollback(trx);
			e.printStackTrace();
			throw e;
		}
	}

}
