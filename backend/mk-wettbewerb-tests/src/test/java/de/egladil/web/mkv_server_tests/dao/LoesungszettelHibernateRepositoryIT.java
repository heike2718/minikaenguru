// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

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
		Identifier kindID = new Identifier("cd9c85a2-966f-48c9-bf3e-3f844669dedb");

		String expectedNutzereingabe = "EBCACCBDBNBN";

		Optional<Loesungszettel> optResult = loesungszettelRepository.findLoesungszettelWithKindID(kindID);
		assertTrue("DB muss zurückgesetzt werden", optResult.isPresent());

		Loesungszettel loesungszettel = optResult.get();
		assertEquals("DB muss zurückgesetzt werden", kindID, loesungszettel.kindID());
		assertEquals("DB muss zurückgesetzt werden", new Identifier("fae8ff6e-194e-4257-a1ff-14a670c476ab"),
			loesungszettel.identifier());
		assertEquals("DB muss zurückgesetzt werden", expectedNutzereingabe, loesungszettel.rohdaten().nutzereingabe());
		assertEquals("DB muss zurückgesetzt werden", 0, loesungszettel.version());

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

		assertTrue("DB muss zurückgesetzt werden", optVorhandener.isPresent());
		Loesungszettel vorhandener = optVorhandener.get();

		assertEquals("DB muss zurückgesetzt werden", 3, vorhandener.version());
		assertEquals("DB muss zurückgesetzt werden", expectedNutzereingabe, vorhandener.rohdaten().nutzereingabe());

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

		assertTrue("DB muss zurückgesetzt werden", optVorhandener.isPresent());
		Loesungszettel vorhandener = optVorhandener.get();

		assertEquals("DB muss zurückgesetzt werden", 1, vorhandener.version());
		assertEquals("DB muss zurückgesetzt werden", "ANNCNNNNNNNN", vorhandener.rohdaten().nutzereingabe());

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

		assertTrue("DB muss zurückgesetzt werden", optVorhandener.isPresent());

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
