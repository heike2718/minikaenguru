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
		klassenService = KlassenService.createForIntegrationTest(entityManager);
	}

	@Test
	void testKlasseAnlegenUndDublettePruefenUndAendernUndLoeschen() {

		// Arrange
		String schulkuerzel = "G1HDI46O";
		String lehrerUuid = "412b67dc-132f-465a-a3c3-468269e866cb";
		String name = "2a";

		KlasseEditorModel klasseEditorModel = new KlasseEditorModel().withName(name);
		KlasseRequestData data = new KlasseRequestData()
			.withKlasse(klasseEditorModel)
			.withSchulkuerzel(schulkuerzel);

		String klasseUuid = null;

		{

			EntityTransaction trx = entityManager.getTransaction();

			try {

				trx.begin();

				KlasseAPIModel result = klassenService.klasseAnlegen(data, lehrerUuid);

				trx.commit();

				// Assert
				// Act
				boolean duplikat = klassenService.pruefeDuplikat(data, lehrerUuid);

				// Assert
				assertTrue(duplikat);

				klasseUuid = result.getUuid();
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

			EntityTransaction trx = entityManager.getTransaction();

			try {

				klasseEditorModel = klasseEditorModel.withName("2a - Füchse");

				trx.begin();

				KlasseAPIModel result = klassenService.klasseUmbenennen(data, lehrerUuid);

				trx.commit();

				// Assert
				assertEquals("2a - Füchse", result.name());

			} catch (PersistenceException e) {

				trx.rollback();
				e.printStackTrace();
				fail("klasse umbenennen: " + e.getMessage());
			}
		}

		{

			EntityTransaction trx = entityManager.getTransaction();

			try {

				trx.begin();

				KlasseAPIModel result = klassenService.klasseLoeschen(klasseUuid, lehrerUuid);

				trx.commit();

				// Assert
				assertEquals("2a - Füchse", result.name());

			} catch (PersistenceException e) {

				trx.rollback();
				e.printStackTrace();
				fail("klasse lösechen: " + e.getMessage());
			}
		}

		Optional<Klasse> optKlasse = KlassenHibernateRepository.createForIntegrationTest(entityManager)
			.ofIdentifier(new Identifier(klasseUuid));

		assertTrue(optKlasse.isEmpty());

	}

}
