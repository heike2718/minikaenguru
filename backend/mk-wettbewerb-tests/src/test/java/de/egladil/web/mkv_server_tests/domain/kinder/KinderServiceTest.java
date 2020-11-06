// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.kinder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.KinderService;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * KinderServiceTest
 */
public class KinderServiceTest extends AbstractIT {

	private static final String VERANSTALTER_UUID = "5d89c2e1-5d35-4e1b-b5a5-c56defd8ba43";

	private KinderRepository kinderRepository;

	private KinderService kinderService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);
		kinderService = KinderService.createForIntegrationTest(entityManager);

	}

	@Test
	void testKindAnlegenUndDublettePruefenUndAendernUndLoeschen() {

		String neueUuid = "";

		String initialerNachname = "Walter " + System.currentTimeMillis();

		KindEditorModel initialesKindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, Sprache.de)
			.withNachname(initialerNachname)
			.withVorname("Fiona").withZusatz("blond");

		{

			// Arrange

			KindRequestData requestData = new KindRequestData().withKind(initialesKindEditorModel)
				.withUuid(KindRequestData.KEINE_UUID);

			EntityTransaction trx = entityManager.getTransaction();

			try {

				trx.begin();

				// Act
				KindAPIModel result = kinderService.kindAnlegen(requestData, VERANSTALTER_UUID);

				trx.commit();

				// Assert
				assertNotNull(result.uuid());

				neueUuid = result.uuid();

				Optional<Kind> optKind = kinderRepository.withIdentifier(new Identifier(neueUuid));

				assertTrue(optKind.isPresent());

				Kind kind = optKind.get();
				assertEquals(new Identifier(neueUuid), kind.identifier());
				assertNull(kind.klasseID());
				assertNull(kind.loesungszettelID());
				assertEquals(initialerNachname, kind.nachname());
				assertEquals("Fiona", kind.vorname());
				assertEquals("blond", kind.zusatz());

			} catch (PersistenceException e) {

				trx.rollback();
				e.printStackTrace();
				fail(e.getMessage());
			}
		}

		{

			// Arrange
			KindRequestData requestData = new KindRequestData().withKind(initialesKindEditorModel)
				.withUuid(KindRequestData.KEINE_UUID);

			// Act + Assert
			assertTrue(kinderService.pruefeDublette(requestData, VERANSTALTER_UUID));

		}

		{

			// Arrange
			String nachname = System.currentTimeMillis() + " Walter";

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, Sprache.de).withNachname(nachname)
				.withVorname("Fiona").withZusatz("brünett");

			KindRequestData requestData = new KindRequestData().withKind(kindEditorModel)
				.withUuid(neueUuid);

			EntityTransaction trx = entityManager.getTransaction();

			try {

				trx.begin();

				// Act
				KindAPIModel result = kinderService.kindAendern(requestData, VERANSTALTER_UUID);

				trx.commit();

				// Assert
				assertNotNull(result.uuid());

				neueUuid = result.uuid();

				Optional<Kind> optKind = kinderRepository.withIdentifier(new Identifier(neueUuid));

				assertTrue(optKind.isPresent());

				Kind kind = optKind.get();
				assertEquals(new Identifier(neueUuid), kind.identifier());
				assertNull(kind.klasseID());
				assertNull(kind.loesungszettelID());
				assertEquals(nachname, kind.nachname());
				assertEquals("Fiona", kind.vorname());
				assertEquals("brünett", kind.zusatz());

			} catch (PersistenceException e) {

				trx.rollback();
				e.printStackTrace();
				fail(e.getMessage());
			}
		}

		{

			EntityTransaction trx = entityManager.getTransaction();

			try {

				trx.begin();

				// Act
				kinderService.kindLoeschen(neueUuid, VERANSTALTER_UUID);

				trx.commit();

				Optional<Kind> optKind = kinderRepository.withIdentifier(new Identifier(neueUuid));

				// Assert
				assertTrue(optKind.isEmpty());

			} catch (PersistenceException e) {

				trx.rollback();
				e.printStackTrace();
				fail(e.getMessage());
			}

		}

	}

}
