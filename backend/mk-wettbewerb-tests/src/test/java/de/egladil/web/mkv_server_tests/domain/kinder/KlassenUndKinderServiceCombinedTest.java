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

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderService;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenService;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseRequestData;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KlassenHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * KlassenUndKinderServiceCombinedTest
 */
public class KlassenUndKinderServiceCombinedTest extends AbstractIT {

	private KlassenService klassenService;

	private KinderService kinderService;

	private KinderHibernateRepository kinderRepository;

	private KlassenHibernateRepository klassenRepository;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		klassenService = KlassenService.createForIntegrationTest(entityManager);
		kinderService = KinderService.createForIntegrationTest(entityManager);

		klassenRepository = KlassenHibernateRepository.createForIntegrationTest(entityManager);
		kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);

	}

	@Test
	void klasseAnlegen_und_kindZuordnen_und_klasseLoeschen() {

		// Arrange
		String schulkuerzel = "G1HDI46O";
		String lehrerUuid = "412b67dc-132f-465a-a3c3-468269e866cb";
		String name = "2a";

		int anzahlKInderVorher = kinderRepository
			.withTeilnahme(TeilnahmeIdentifierAktuellerWettbewerb.createForSchulteilnahme(schulkuerzel)).size();

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

		String initialerNachname = "Walter " + System.currentTimeMillis();

		KindEditorModel initialesKindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, Sprache.de)
			.withNachname(initialerNachname)
			.withVorname("Fiona").withZusatz("blond")
			.withKlasseUuid(klasseUuid);

		KindRequestData kindRequestData = new KindRequestData().withKind(initialesKindEditorModel)
			.withUuid("neu").withKuerzelLand("DE-ST");

		String kindUuid = null;

		Identifier klasseID = new Identifier(klasseUuid);

		{

			// Arrange
			EntityTransaction trx = entityManager.getTransaction();

			try {

				trx.begin();

				// Act
				KindAPIModel result = kinderService.kindAnlegen(kindRequestData, lehrerUuid);

				trx.commit();

				// Assert
				assertNotNull(result.uuid());

				kindUuid = result.uuid();

			} catch (PersistenceException e) {

				trx.rollback();
				e.printStackTrace();
				fail(e.getMessage());
			}
		}

		{

			Optional<Kind> optKind = kinderRepository
				.withIdentifier(new Identifier(kindUuid));

			assertTrue(optKind.isPresent());

			Kind kind = optKind.get();
			assertEquals(new Identifier(kindUuid), kind.identifier());
			assertNull(kind.loesungszettelID());
			assertEquals(initialerNachname, kind.nachname());
			assertEquals("Fiona", kind.vorname());
			assertEquals("blond", kind.zusatz());
			assertEquals("DE-ST", kind.landkuerzel());
			assertEquals(klasseID, kind.klasseID());
		}

		{

			List<KlasseAPIModel> apiKlassen = this.klassenService.klassenZuSchuleLaden(schulkuerzel, lehrerUuid);
			assertTrue(apiKlassen.size() >= 1);

			for (KlasseAPIModel apiKlasse : apiKlassen) {

				if (klasseID.identifier().equals(apiKlasse.uuid())) {

					assertEquals(1, apiKlasse.anzahlKinder());
				}
			}
		}

		{

			EntityTransaction trx = entityManager.getTransaction();

			try {

				trx.begin();

				KlasseAPIModel result = klassenService.klasseLoeschen(klasseUuid, lehrerUuid);

				trx.commit();

				// Assert
				assertEquals("2a", result.name());

			} catch (PersistenceException e) {

				trx.rollback();
				e.printStackTrace();
				fail("klasse lösechen: " + e.getMessage());
			}
		}

		Optional<Klasse> optKlasse = klassenRepository.ofIdentifier(klasseID);

		List<Kind> kinder = kinderRepository
			.withTeilnahme(TeilnahmeIdentifierAktuellerWettbewerb.createForSchulteilnahme(schulkuerzel));

		assertTrue(optKlasse.isEmpty());
		assertEquals(anzahlKInderVorher, kinder.size());

	}

}
