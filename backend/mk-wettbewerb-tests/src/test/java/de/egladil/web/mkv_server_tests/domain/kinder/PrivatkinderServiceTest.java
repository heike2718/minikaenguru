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

import de.egladil.web.commons_crypto.impl.CryptoServiceImpl;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auswertungen.LoesungszettelService;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.PrivatkinderService;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.PrivatkindRequestData;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.PrivatteilnahmeKuerzelService;
import de.egladil.web.mk_gateway.domain.veranstalter.PrivatveranstalterService;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagenService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.TeilnahmenHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UserHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VeranstalterHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.WettbewerbHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * PrivatkinderServiceTest
 */
public class PrivatkinderServiceTest extends AbstractIT {

	private static final String VERANSTALTER_UUID = "5d89c2e1-5d35-4e1b-b5a5-c56defd8ba43";

	private KinderRepository kinderRepository;

	private PrivatkinderService privatkinderService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();

		WettbewerbService wettbewerbService = WettbewerbService
			.createForTest(WettbewerbHibernateRepository.createForIntegrationTest(entityManager));

		kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);

		AuthorizationService authService = AuthorizationService.createForTest(
			VeranstalterHibernateRepository.createForIntegrationTest(entityManager),
			UserHibernateRepository.createForIntegrationTest(entityManager));
		TeilnahmenRepository teilnahmenRepository = TeilnahmenHibernateRepository.createForIntegrationTest(entityManager);

		PrivatteilnahmeKuerzelService kuerzelService = PrivatteilnahmeKuerzelService.createForTest(new CryptoServiceImpl(),
			teilnahmenRepository);

		VeranstalterRepository veranstalterRepository = VeranstalterHibernateRepository.createForIntegrationTest(entityManager);

		ZugangUnterlagenService zugangUnterlagenService = ZugangUnterlagenService.createForTest(teilnahmenRepository,
			veranstalterRepository, wettbewerbService);

		PrivatveranstalterService privatveranstalterService = PrivatveranstalterService.createForTest(
			veranstalterRepository, zugangUnterlagenService, wettbewerbService,
			teilnahmenRepository, kuerzelService);

		LoesungszettelService loesungszettelService = LoesungszettelService
			.createForTest(authService, LoesungszettelHibernateRepository.createForIntegrationTest(entityManager));

		privatkinderService = PrivatkinderService.createForTest(authService, kinderRepository, teilnahmenRepository,
			privatveranstalterService, wettbewerbService, loesungszettelService);

	}

	@Test
	void testKindAnlegenUndAendernUndLoeschen() {

		String neueUuid = "";

		{

			// Arrange
			String nachname = "Walter " + System.currentTimeMillis();

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, Sprache.de).withNachname(nachname)
				.withVorname("Fiona").withZusatz("blond");

			PrivatkindRequestData requestData = new PrivatkindRequestData().withKind(kindEditorModel)
				.withUuid(PrivatkindRequestData.KEINE_UUID);

			EntityTransaction trx = entityManager.getTransaction();

			try {

				trx.begin();

				// Act
				KindAPIModel result = privatkinderService.privatkindAnlegen(requestData, VERANSTALTER_UUID);

				trx.commit();

				// Assert
				assertNotNull(result.uuid());

				neueUuid = result.uuid();

				Optional<Kind> optKind = kinderRepository.findKindWithIdentifier(new Identifier(neueUuid),
					privatkinderService.getWettbewerbID());

				assertTrue(optKind.isPresent());

				Kind kind = optKind.get();
				assertEquals(new Identifier(neueUuid), kind.identifier());
				assertNull(kind.klasseID());
				assertNull(kind.loesungszettelID());
				assertEquals(nachname, kind.nachname());
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
			String nachname = System.currentTimeMillis() + " Walter";

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, Sprache.de).withNachname(nachname)
				.withVorname("Fiona").withZusatz("brünett");

			PrivatkindRequestData requestData = new PrivatkindRequestData().withKind(kindEditorModel)
				.withUuid(neueUuid);

			EntityTransaction trx = entityManager.getTransaction();

			try {

				trx.begin();

				// Act
				KindAPIModel result = privatkinderService.privatkindAendern(requestData, VERANSTALTER_UUID);

				trx.commit();

				// Assert
				assertNotNull(result.uuid());

				neueUuid = result.uuid();

				Optional<Kind> optKind = kinderRepository.findKindWithIdentifier(new Identifier(neueUuid),
					privatkinderService.getWettbewerbID());

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
				privatkinderService.privatkindLoeschen(neueUuid, VERANSTALTER_UUID);

				trx.commit();

				Optional<Kind> optKind = kinderRepository.findKindWithIdentifier(new Identifier(neueUuid),
					privatkinderService.getWettbewerbID());

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
