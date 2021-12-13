// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.kinder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenRepository;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseRequestData;
import de.egladil.web.mk_gateway.domain.kinder.impl.KinderServiceImpl;
import de.egladil.web.mk_gateway.domain.kinder.impl.KlassenServiceImpl;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.OnlineLoesungszettelService;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KlassenHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;
import de.egladil.web.mkv_server_tests.TestUtils;

/**
 * KlassenKinderLoesungszettelServiceIT
 */
public class KlassenKinderLoesungszettelServiceIT extends AbstractIntegrationTest {

	private KlassenRepository klassenRepository;

	private KinderRepository kinderRepository;

	private LoesungszettelRepository loesungszettelRepository;

	private KlassenServiceImpl klassenService;

	private KinderServiceImpl kinderService;

	private OnlineLoesungszettelService loesungszettelService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		klassenRepository = KlassenHibernateRepository.createForIntegrationTest(entityManager);
		kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);
		loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManager);

		klassenService = KlassenServiceImpl.createForIntegrationTest(entityManager);
		kinderService = KinderServiceImpl.createForIntegrationTest(entityManager);
		loesungszettelService = OnlineLoesungszettelService.createForIntegrationTest(entityManager);
	}

	@Test
	void should_klasseAnlegenKindAnlegenLoesungszettelAnlegenKlasseLoeschen_removeAllData() {

		String schulkuerzel = "G1HDI46O";
		String veranstalterUuid = "412b67dc-132f-465a-a3c3-468269e866cb";
		String name = "Tukane";

		KlasseEditorModel klasseEditorModel = new KlasseEditorModel().withName(name);
		KlasseRequestData klasseRequestData = new KlasseRequestData()
			.withKlasse(klasseEditorModel)
			.withSchulkuerzel(schulkuerzel);

		String klasseUuid = null;
		String kindUuid = null;
		String loesungszettelUuid = null;

		// Act 1
		System.out.println("Klasse anlegen...");
		KlasseAPIModel klasseReponseData = this.klasseAnlegen(klasseRequestData, veranstalterUuid);

		assertNotNull(klasseReponseData.uuid());
		klasseUuid = klasseReponseData.uuid();

		// Assert 1
		Optional<Klasse> optKlasse = klassenRepository.ofIdentifier(new Identifier(klasseUuid));
		assertTrue(optKlasse.isPresent());
		Klasse klasse = optKlasse.get();
		assertEquals(0, kinderRepository.countKinderInKlasse(klasse));

		// Act 2
		System.out.println("Kind anlegen...");
		String nachname = "Habermaß";

		KindEditorModel initialesKindEditorModel = new KindEditorModel(Klassenstufe.EINS, Sprache.en)
			.withNachname(nachname)
			.withVorname("Fiona").withKlasseUuid(klasseUuid);

		KindRequestData requestData = new KindRequestData().withKind(initialesKindEditorModel)
			.withUuid(KindRequestData.KEINE_UUID);

		KindAPIModel kindResult = this.kindAnlegen(requestData, veranstalterUuid);
		kindUuid = kindResult.uuid();

		assertEquals(1, kinderRepository.countKinderInKlasse(klasse));
		assertEquals(0, kinderRepository.countLoesungszettelInKlasse(klasse));

		// Act 3
		System.out.println("Lösungszettel anlegen...");
		LoesungszettelAPIModel loesungszettelDaten = TestUtils.createLoesungszettelRequestDatenKlasseEinsKreuzeABC("neu", kindUuid);

		ResponsePayload loesungszettelResponse = this.loesungszettelAnlegen(loesungszettelDaten, veranstalterUuid);

		assertTrue(loesungszettelResponse.isOk());

		LoesungszettelpunkteAPIModel loesungszettelResponseData = (LoesungszettelpunkteAPIModel) loesungszettelResponse.getData();
		assertEquals(1, kinderRepository.countLoesungszettelInKlasse(klasse));
		loesungszettelUuid = loesungszettelResponseData.loesungszettelId();

		Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));
		assertTrue(optLoesungszettel.isPresent());
		assertEquals(new Identifier(kindUuid), optLoesungszettel.get().kindID());

		// Act 4
		System.out.println("Klasse löschen...");

		KlasseAPIModel geloeschteKlasse = this.klasseLoeschen(klasseUuid, veranstalterUuid);
		assertEquals(klasseUuid, geloeschteKlasse.uuid());

		assertTrue(loesungszettelRepository.ofID(new Identifier(loesungszettelUuid)).isEmpty());
		assertTrue(kinderRepository.ofId(new Identifier(kindUuid)).isEmpty());
		assertTrue(klassenRepository.ofIdentifier(new Identifier(klasseUuid)).isEmpty());

	}

	private KlasseAPIModel klasseAnlegen(final KlasseRequestData requestData, final String veranstalterUuid) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			KlasseAPIModel result = klassenService.klasseAnlegen(requestData, veranstalterUuid);

			trx.commit();

			return result;

		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();

			throw new RuntimeException("Klasse konnte nicht gespeichert werden");
		}
	}

	private KindAPIModel kindAnlegen(final KindRequestData requestData, final String veranstalterUuid) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			// Act
			KindAPIModel result = kinderService.kindAnlegen(requestData, veranstalterUuid);

			trx.commit();

			return result;

		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();

			throw new RuntimeException("Kind konnte nicht gespeichert werden");
		}
	}

	private ResponsePayload loesungszettelAnlegen(final LoesungszettelAPIModel loesungszetteldaten, final String veranstalterUuid) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			ResponsePayload result = loesungszettelService.loesungszettelAnlegen(loesungszetteldaten,
				new Identifier(veranstalterUuid));

			trx.commit();

			return result;

		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();

			throw new RuntimeException("Lösungszettel konnte nicht angelegt werden");
		}

	}

	private KlasseAPIModel klasseLoeschen(final String klasseUuid, final String veranstalterUuid) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			KlasseAPIModel result = klassenService.klasseLoeschen(klasseUuid, veranstalterUuid);

			trx.commit();

			return result;

		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();

			throw new RuntimeException("Klasse konnte nicht gelöscht werden");
		}
	}
}
