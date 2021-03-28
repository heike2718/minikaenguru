// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.kinder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenService;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseRequestData;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KlassenHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * KlassenServiceTest
 */
public class KlassenServiceTest extends AbstractIT {

	private KlassenService klassenService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		klassenService = KlassenService.createForIntegrationTest(entityManagerWettbewerbDB);
	}

	@Test
	void should_klassenZuSchuleLaden_work() {

		// Arrange
		String schulkuerzel = "G1HDI46O";
		String lehrerUuid = "412b67dc-132f-465a-a3c3-468269e866cb";

		// Act
		List<KlasseAPIModel> klassen = klassenService.klassenZuSchuleLaden(schulkuerzel, lehrerUuid);

		// Assert
		assertEquals(1, klassen.size());
		KlasseAPIModel klasse = klassen.get(0);
		assertEquals("Testklasse", klasse.name());
		assertEquals(schulkuerzel, klasse.schulkuerzel());
		assertEquals("6d57b4f6-61ed-450a-ae17-7ccb3d9d776a", klasse.uuid());
		assertEquals(1, klasse.anzahlKinder());
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

		String klasseUuid = null;

		{

			EntityTransaction trx = entityManagerWettbewerbDB.getTransaction();

			try {

				trx.begin();

				KlasseAPIModel result = klassenService.klasseAnlegen(data, lehrerUuid);

				trx.commit();

				// Assert
				// Act
				boolean duplikat = klassenService.pruefeDuplikat(data, lehrerUuid);

				// Assert
				assertTrue(duplikat);

				klasseUuid = result.uuid();
				data = data.withUuid(klasseUuid);

				// Assert

				assertNotNull(klasseUuid);

			} catch (PersistenceException e) {

				trx.rollback();
				e.printStackTrace();
				fail("klasse anlegen: " + e.getMessage());
			}

		}

		{

			// Act
			boolean duplikat = klassenService.pruefeDuplikat(data, lehrerUuid);

			// Assert
			assertFalse(duplikat);
		}

		{

			EntityTransaction trx = entityManagerWettbewerbDB.getTransaction();

			try {

				klasseEditorModel = klasseEditorModel.withName("Seeadler");

				trx.begin();

				KlasseAPIModel result = klassenService.klasseUmbenennen(data, lehrerUuid);

				trx.commit();

				// Assert
				assertEquals("Seeadler", result.name());

			} catch (PersistenceException e) {

				trx.rollback();
				e.printStackTrace();
				fail("klasse umbenennen: " + e.getMessage());
			}
		}

		{

			EntityTransaction trx = entityManagerWettbewerbDB.getTransaction();

			try {

				trx.begin();

				KlasseAPIModel result = klassenService.klasseLoeschen(klasseUuid, lehrerUuid);

				trx.commit();

				// Assert
				assertEquals("Seeadler", result.name());

			} catch (PersistenceException e) {

				trx.rollback();
				e.printStackTrace();
				fail("klasse lösechen: " + e.getMessage());
			}
		}

		Optional<Klasse> optKlasse = KlassenHibernateRepository.createForIntegrationTest(entityManagerWettbewerbDB)
			.ofIdentifier(new Identifier(klasseUuid));

		assertTrue(optKlasse.isEmpty());

	}

}
