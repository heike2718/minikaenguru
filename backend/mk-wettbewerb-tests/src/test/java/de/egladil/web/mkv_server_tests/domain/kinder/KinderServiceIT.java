// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.kinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.kinder.impl.KinderServiceImpl;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * KinderServiceIT
 */
public class KinderServiceIT extends AbstractIntegrationTest {

	private KinderRepository kinderRepository;

	private LoesungszettelRepository loesungszettelRepository;

	private KinderServiceImpl kinderService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);
		loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManager);
		kinderService = KinderServiceImpl.createForIntegrationTest(entityManager);

	}

	@Test
	void testKindAnlegenUndDublettePruefenUndAendern() {

		// Arrange
		String veranstalterUuid = "5d89c2e1-5d35-4e1b-b5a5-c56defd8ba43";
		String neueUuid = "";

		String initialerNachname = "Meier-Walter";

		KindEditorModel initialesKindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, Sprache.de)
			.withNachname(initialerNachname)
			.withVorname("Fiona").withZusatz("blond");

		KindRequestData requestData = new KindRequestData().withKind(initialesKindEditorModel)
			.withUuid(KindRequestData.KEINE_UUID);

		// Act 1
		System.out.println("Act 1: Kind anlegen...");
		KindAPIModel result = this.kindAnlegen(requestData, veranstalterUuid);

		// Assert 1
		assertNotNull(result.uuid());

		neueUuid = result.uuid();

		Optional<Kind> optKind = kinderRepository.ofId(new Identifier(neueUuid));

		assertTrue(optKind.isPresent());

		Kind kind = optKind.get();
		assertEquals(new Identifier(neueUuid), kind.identifier());
		assertNull(kind.klasseID());
		assertNull(kind.loesungszettelID());
		assertEquals(initialerNachname, kind.nachname());
		assertEquals("Fiona", kind.vorname());
		assertEquals("blond", kind.zusatz());

		// Act 2
		System.out.println("Act 2: Dublette prüfen...");
		requestData = new KindRequestData().withKind(initialesKindEditorModel)
			.withUuid(KindRequestData.KEINE_UUID);

		// Act + Assert
		assertTrue(kinderService.pruefeDublette(requestData, veranstalterUuid));

		// Act 2
		System.out.println("Act 3: Kind ändern...");
		String nachname = "Müller-Walter";

		KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, Sprache.de).withNachname(nachname)
			.withVorname("Fiona").withZusatz("brünett");

		requestData = new KindRequestData().withKind(kindEditorModel)
			.withUuid(neueUuid);

		result = this.kindAendern(requestData, veranstalterUuid);
		assertNotNull(result.uuid());

		neueUuid = result.uuid();

		optKind = kinderRepository.ofId(new Identifier(neueUuid));

		assertTrue(optKind.isPresent());

		kind = optKind.get();
		assertEquals(new Identifier(neueUuid), kind.identifier());
		assertNull(kind.klasseID());
		assertNull(kind.loesungszettelID());
		assertEquals(nachname, kind.nachname());
		assertEquals("Fiona", kind.vorname());
		assertEquals("brünett", kind.zusatz());
	}

	@Test
	void should_pruefeDubletteFindDubletteReturnTrue_when_kindInKlasseAlleAttributeGefuellt() {

		// Arrange
		String veranstalterUuid = "c048e4d6-3d40-412a-8d8c-95fb3e0eb614";
		String klasseUuid = "6d57b4f6-61ed-450a-ae17-7ccb3d9d776a";

		KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, Sprache.de)
			.withNachname("Mirkowitz")
			.withVorname("Fiona").withZusatz("blond").withKlasseUuid(klasseUuid);

		KindRequestData requestData = new KindRequestData().withKind(kindEditorModel)
			.withUuid(KindRequestData.KEINE_UUID);

		// Act + Assert
		assertTrue(kinderService.pruefeDublette(requestData, veranstalterUuid));

	}

	@Test
	void should_pruefeDubletteFindDubletteReturnTrue_when_kindInKlasseOhneZusatz() {

		// Arrange
		String veranstalterUuid = "c048e4d6-3d40-412a-8d8c-95fb3e0eb614";
		String klasseUuid = "6d57b4f6-61ed-450a-ae17-7ccb3d9d776a";

		KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, Sprache.de)
			.withNachname("Mirkowitz")
			.withVorname("Fiona").withKlasseUuid(klasseUuid);

		KindRequestData requestData = new KindRequestData().withKind(kindEditorModel)
			.withUuid(KindRequestData.KEINE_UUID);

		// Act + Assert
		assertFalse(kinderService.pruefeDublette(requestData, veranstalterUuid));

	}

	@Test
	void should_pruefeDubletteFindDubletteReturnFalse_when_kindInKlasseAlleAttributeGefuellt_and_andereKlasse() {

		// Arrange
		String veranstalterUuid = "c048e4d6-3d40-412a-8d8c-95fb3e0eb614";
		String klasseUuid = "fe2ed680-d1b8-41f8-9ef1-3c5b2fdbad87";

		KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, Sprache.de)
			.withNachname("Mirkowitz")
			.withVorname("Fiona").withZusatz("blond").withKlasseUuid(klasseUuid);

		KindRequestData requestData = new KindRequestData().withKind(kindEditorModel)
			.withUuid(KindRequestData.KEINE_UUID);

		// Act + Assert
		assertFalse(kinderService.pruefeDublette(requestData, veranstalterUuid));

	}

	@Test
	void should_pruefeDubletteFindDubletteReturnFalse_when_kindInKlasseAlleAttributeGefuellt_andereKlasenstufe() {

		// Arrange
		String veranstalterUuid = "c048e4d6-3d40-412a-8d8c-95fb3e0eb614";
		String klasseUuid = "6d57b4f6-61ed-450a-ae17-7ccb3d9d776a";

		KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, Sprache.de)
			.withNachname("Mirkowitz")
			.withVorname("Fiona").withZusatz("blond").withKlasseUuid(klasseUuid);

		KindRequestData requestData = new KindRequestData().withKind(kindEditorModel)
			.withUuid(KindRequestData.KEINE_UUID);

		// Act + Assert
		assertFalse(kinderService.pruefeDublette(requestData, veranstalterUuid));

	}

	@Test
	void shhould_pruefeDubletteFindDubletteReturnTrue_when_kindInKlasseAlleAttributeGefuellt_andereSprache() {

		// Arrange
		String veranstalterUuid = "c048e4d6-3d40-412a-8d8c-95fb3e0eb614";
		String klasseUuid = "6d57b4f6-61ed-450a-ae17-7ccb3d9d776a";

		KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, Sprache.en)
			.withNachname("Mirkowitz")
			.withVorname("Fiona").withZusatz("blond").withKlasseUuid(klasseUuid);

		KindRequestData requestData = new KindRequestData().withKind(kindEditorModel)
			.withUuid(KindRequestData.KEINE_UUID);

		// Act + Assert
		assertTrue(kinderService.pruefeDublette(requestData, veranstalterUuid));

	}

	@Test
	void should_kindLoeschenLoeschtLoesungszettel_when_KindMitLoesungszettel() {

		// Arrange
		String kindUuid = "00bf7996-4224-49a3-908d-d18258fec747";
		String loesungszettelUuid = "a1527ce3-a835-4d2c-b4a9-7a59f1f637e6";
		String veranstalterUuid = "2f09da36-07c6-4033-a2f1-5e110c804026";

		// Test data
		Optional<Kind> optKind = kinderRepository.ofId(new Identifier(kindUuid));
		assertTrue(optKind.isPresent());

		Kind kind = optKind.get();
		assertEquals(new Identifier(loesungszettelUuid), kind.loesungszettelID());
		assertEquals("Ernst", kind.vorname());
		assertEquals("Lustig", kind.nachname());

		Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));
		assertTrue(optLoesungszettel.isPresent());

		// Act
		KindAPIModel result = this.kindLoeschen(kindUuid, veranstalterUuid);

		// Assert
		assertNotNull(result);
		assertEquals(kindUuid, result.uuid());
		assertNull(result.punkte());

		optKind = kinderRepository.ofId(new Identifier(kindUuid));
		assertTrue(optKind.isEmpty());
		optLoesungszettel = loesungszettelRepository.ofID(new Identifier(loesungszettelUuid));
		assertTrue(optLoesungszettel.isEmpty());

	}

	@Test
	void should_changeSpracheKindBePropagatedToLoesungszettel() {

		// Arrange
		String veranstalterUuid = "eee4dcf4-decf-4d7f-89cd-ea2516122320";

		Identifier kindID = new Identifier("cc74ed3c-28cd-4cc5-8744-c4c3eb7e5c19");
		String loesungszettelUuid = "6c180cbf-d78e-4173-992e-a6d095485299";
		Identifier loesungszettelID = new Identifier(loesungszettelUuid);

		Optional<Kind> optKind = kinderRepository.ofId(kindID);

		assertTrue(optKind.isPresent(), "DB muss zurückgesetzt werden");

		Kind kind = optKind.get();

		assertEquals(loesungszettelID, kind.loesungszettelID(), "DB muss zurückgesetzt werden");
		assertEquals(Sprache.de, kind.sprache(), "DB muss zurückgesetzt werden");

		Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository.ofID(loesungszettelID);
		assertTrue(optLoesungszettel.isPresent(), "DB muss zurückgesetzt werden");

		Loesungszettel loesungszettel = optLoesungszettel.get();

		int expectedVersion = loesungszettel.version() + 1;

		assertEquals(Sprache.de, loesungszettel.sprache(), "DB muss zurückgesetzt werden");

		KindEditorModel kindEditorModel = new KindEditorModel(kind.klassenstufe(), Sprache.en).withVorname(kind.vorname())
			.withNachname(kind.nachname()).withZusatz(kind.zusatz()).withKlasseUuid(kind.klasseID().identifier());
		KindRequestData daten = new KindRequestData().withKind(kindEditorModel).withKuerzelLand(kind.landkuerzel())
			.withUuid(kind.identifier().identifier());

		// Act
		KindAPIModel result = this.kindAendern(daten, veranstalterUuid);

		// Act
		assertEquals(Sprache.en, result.sprache().sprache());
		assertEquals(kind.vorname(), result.vorname());
		assertEquals(kind.nachname(), result.nachname());
		assertEquals(kind.zusatz(), result.zusatz());
		assertEquals(kind.klassenstufe(), result.klassenstufe().klassenstufe());
		assertEquals(kind.klasseID().identifier(), result.klasseId());

		Kind geaendertesKind = kinderRepository.ofId(kindID).get();
		assertEquals(Sprache.en, geaendertesKind.sprache());
		assertEquals(kind.klasseID(), geaendertesKind.klasseID());
		assertEquals(loesungszettelID, geaendertesKind.loesungszettelID());

		Loesungszettel geanderterLoesungszettel = loesungszettelRepository.ofID(loesungszettelID).get();
		assertEquals(expectedVersion, geanderterLoesungszettel.version());
		assertEquals(Sprache.en, geanderterLoesungszettel.sprache());
		assertEquals(kindID, geanderterLoesungszettel.kindID());

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

	private KindAPIModel kindAendern(final KindRequestData requestData, final String veranstalterUuid) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			// Act
			KindAPIModel result = kinderService.kindAendern(requestData, veranstalterUuid);

			trx.commit();

			return result;

		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();

			throw new RuntimeException("Kind konnte nicht gespeichert werden");
		}
	}

	private KindAPIModel kindLoeschen(final String kindUuid, final String veranstalterUuid) {

		EntityTransaction trx = entityManager.getTransaction();

		try {

			trx.begin();

			// Act
			KindAPIModel result = kinderService.kindLoeschen(kindUuid, veranstalterUuid);

			trx.commit();

			return result;

		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();

			throw new RuntimeException("Kind konnte nicht gespeichert werden");
		}

	}

}
