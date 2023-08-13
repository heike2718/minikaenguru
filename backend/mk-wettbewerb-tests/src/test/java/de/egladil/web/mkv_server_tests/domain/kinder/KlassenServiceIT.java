// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.kinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenRepository;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseRequestData;
import de.egladil.web.mk_gateway.domain.kinder.impl.KlassenServiceImpl;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KlassenHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * KlassenServiceIT
 */
public class KlassenServiceIT extends AbstractIntegrationTest {

	private KlassenRepository klassenRepository;

	private KinderRepository kinderRepository;

	private LoesungszettelRepository loesungszettelRepository;

	private KlassenServiceImpl klassenService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);
		klassenRepository = KlassenHibernateRepository.createForIntegrationTest(entityManager);
		loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManager);
		klassenService = KlassenServiceImpl.createForIntegrationTest(entityManager);
	}

	@Test
	void should_klassenZuSchuleLaden_work() {

		// Arrange
		String schulkuerzel = "G1HDI46O";
		String lehrerUuid = "412b67dc-132f-465a-a3c3-468269e866cb";

		// Act
		List<KlasseAPIModel> klassen = klassenService.klassenZuSchuleLaden(schulkuerzel, lehrerUuid);

		// Assert
		assertEquals(4, klassen.size());

		{

			KlasseAPIModel klasse = klassen.get(0);
			assertEquals("Sumpfhühner", klasse.name());
			assertEquals(schulkuerzel, klasse.schulkuerzel());
			assertEquals("52ade3fd-78f7-4a80-95c9-a16606b33873", klasse.uuid());
			assertEquals(3, klasse.anzahlKinder());
		}

		{

			KlasseAPIModel klasse = klassen.get(1);
			assertEquals("Eichhörnchen", klasse.name());
			assertEquals(schulkuerzel, klasse.schulkuerzel());
			assertEquals("6d57b4f6-61ed-450a-ae17-7ccb3d9d776a", klasse.uuid());
			assertEquals(4, klasse.anzahlKinder());
		}

		{

			KlasseAPIModel klasse = klassen.get(2);
			assertEquals("Eisvögel", klasse.name());
			assertEquals(schulkuerzel, klasse.schulkuerzel());
			assertEquals("7cb4d7b4-c0c0-4d0b-99c0-a1ae9c392562", klasse.uuid());
			assertEquals(4, klasse.anzahlKinder());
		}

		{

			KlasseAPIModel klasse = klassen.get(3);
			assertEquals("Albatrosse", klasse.name());
			assertEquals(schulkuerzel, klasse.schulkuerzel());
			assertEquals("fe2ed680-d1b8-41f8-9ef1-3c5b2fdbad87", klasse.uuid());
			assertEquals(4, klasse.anzahlKinder());
		}
	}

	@Test
	void testKlasseAnlegenUndDublettePruefenUndAendernUndLoeschen() {

		// Arrange
		String schulkuerzel = "G1HDI46O";
		String lehrerUuid = "412b67dc-132f-465a-a3c3-468269e866cb";
		String name = "Füchse";

		KlasseEditorModel klasseEditorModel = new KlasseEditorModel().withName(name);
		KlasseRequestData data = new KlasseRequestData()
			.withKlasse(klasseEditorModel)
			.withSchulkuerzel(schulkuerzel);

		// Act anlegen
		KlasseAPIModel result = this.klasseAnlegen(data, lehrerUuid);

		// Assert anlegen
		String klasseUuid = result.uuid();
		assertNotNull(klasseUuid);
		assertTrue(klassenService.pruefeDuplikat(data, lehrerUuid));
		data = data.withUuid(klasseUuid);

		// Act Duplikat
		boolean duplikat = klassenService.pruefeDuplikat(data, lehrerUuid);

		// Assert Duplikat
		assertFalse(duplikat);

		// Arrange umbenennen
		klasseEditorModel = klasseEditorModel.withName("Seeadler");
		data = data.withKlasse(klasseEditorModel);

		// Act umbenennen
		result = this.klasseUmbenennen(data, lehrerUuid);

		// Assert umbenennen
		assertEquals("Seeadler", result.name());
		Optional<Klasse> optKlasse = klassenRepository.ofIdentifier(new Identifier(klasseUuid));
		Klasse klasse = optKlasse.get();
		assertEquals("Seeadler", klasse.name());

		// Act löschen
		result = this.klasseLoeschen(klasseUuid, lehrerUuid);

		// Assert löschen
		assertEquals("Seeadler", result.name());
		assertEquals(klasseUuid, result.uuid());

		optKlasse = klassenRepository.ofIdentifier(new Identifier(klasseUuid));
		assertTrue(optKlasse.isEmpty());

	}

	@Test
	void should_klasseLoeschenLoeschtKinderUndLoesungszettel() {

		// Arrange
		String schulkuerzel = "VC91WP8L";
		String lehrerUuid = "eee4dcf4-decf-4d7f-89cd-ea2516122320";
		String klasseUuid = "222f3bc1-7437-4935-a1fd-2c821a603baf";
		Identifier klasseID = new Identifier(klasseUuid);

		String uuidOskar = "d1a1d319-2947-4cc2-926e-747aa8262124";
		String uuidPaulina = "978d57d5-b78e-49ac-8e5e-a61ce068cfbd";

		String uuidLoesungszettel = "139a26a9-475c-4e37-acd0-d8f593819d20";

		TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier = TeilnahmeIdentifierAktuellerWettbewerb
			.createForSchulteilnahme(schulkuerzel);

		Optional<Klasse> optKlasse = this.klassenRepository.ofIdentifier(klasseID);
		assertTrue(optKlasse.isPresent(), "DB muss zurückgesetzt werden");

		Klasse klasse = optKlasse.get();
		assertEquals("2a", klasse.name());
		List<Kind> kinder = kinderRepository.withTeilnahme(teilnahmeIdentifier);

		assertEquals(20, kinder.size(), "DB muss zurückgesetzt werden");

		assertTrue(this.klassenRepository.ofIdentifier(klasseID).isPresent(),
			"DB muss zurückgesetzt werden");
		assertTrue(this.kinderRepository.ofId(new Identifier(uuidOskar)).isPresent(),
			"DB muss zurückgesetzt werden");
		assertTrue(this.kinderRepository.ofId(new Identifier(uuidPaulina)).isPresent(),
			"DB muss zurückgesetzt werden");
		assertTrue(this.loesungszettelRepository.ofID(new Identifier(uuidLoesungszettel)).isPresent(),
			"DB muss zurückgesetzt werden");

		// Act
		KlasseAPIModel result = this.klasseLoeschen(klasseUuid, lehrerUuid);

		// Assert
		assertEquals(klasseUuid, result.uuid());
		assertEquals("2a", result.name());

		assertEquals(18, kinderRepository.withTeilnahme(teilnahmeIdentifier).size());

		assertTrue(this.klassenRepository.ofIdentifier(klasseID).isEmpty());
		assertTrue(this.kinderRepository.ofId(new Identifier(uuidOskar)).isEmpty());
		assertTrue(this.kinderRepository.ofId(new Identifier(uuidPaulina)).isEmpty());
		assertTrue(this.loesungszettelRepository.ofID(new Identifier(uuidLoesungszettel)).isEmpty());

	}

	private KlasseAPIModel klasseAnlegen(final KlasseRequestData data, final String lehrerUuid) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			KlasseAPIModel result = klassenService.klasseAnlegen(data, lehrerUuid);

			trx.commit();

			return result;

		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	private KlasseAPIModel klasseUmbenennen(final KlasseRequestData data, final String lehrerUuid) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			KlasseAPIModel result = klassenService.klasseUmbenennen(data, lehrerUuid);

			trx.commit();

			return result;

		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	private KlasseAPIModel klasseLoeschen(final String klasseUuid, final String lehrerUuid) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			KlasseAPIModel result = klassenService.klasseLoeschen(klasseUuid, lehrerUuid);

			trx.commit();

			return result;

		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

	}

}
