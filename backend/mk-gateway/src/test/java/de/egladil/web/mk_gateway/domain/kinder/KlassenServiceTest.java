// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseRequestData;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * KlassenServiceTest
 */
public class KlassenServiceTest extends AbstractDomainServiceTest {

	private KlassenService klassenService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();

		AuthorizationService authService = AuthorizationService.createForTest(getVeranstalterRepository(), getUserRepository());

		klassenService = KlassenService.createForTest(authService,
			KinderService.createForTest(authService, getKinderRepository(), getTeilnahmenRepository(), getVeranstalterRepository(),
				getWettbewerbService(),
				LoesungszettelService.createForTest(authService, getWettbewerbService(), getKinderRepository(),
					getLoesungszettelRepository()),
				getKlassenRepository()),
			getTeilnahmenRepository(),
			getVeranstalterRepository(),
			getWettbewerbService(),
			LoesungszettelService.createForTest(authService, getWettbewerbService(), getKinderRepository(),
				getLoesungszettelRepository()),
			getKlassenRepository());
	}

	@Test
	void should_klassenZuSchuleLaden_callTheAuthorizationService() {

		// Act
		try {

			klassenService.klassenZuSchuleLaden(SCHULKUERZEL_1, UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AuthException");

		} catch (AccessDeniedException e) {

		}
	}

	@Test
	void should_klassenZuSchuleLadenThrowNotFound_when_nichtAngemeldet() {

		// Act
		try {

			klassenService.klassenZuSchuleLaden(SCHULKUERZEL_2, UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

		}
	}

	@Test
	void should_klassenZuSchuleLadenReturnTheKlassen_when_schuleAngemeldet() {

		// Act
		List<KlasseAPIModel> klassen = klassenService.klassenZuSchuleLaden(SCHULKUERZEL_1, UUID_LEHRER_1);

		// Assert
		assertEquals(2, klassen.size());
	}

	@Test
	void should_pruefeDuplikat_callTheAuthorizationService() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.pruefeDuplikat(data, UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AuthException");

		} catch (AccessDeniedException e) {

		}
	}

	@Test
	void should_pruefeDuplikatReturnFalse_when_KeineUeberschneidungWegenUUID() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		boolean result = klassenService.pruefeDuplikat(data, UUID_LEHRER_1);

		// Assert
		assertFalse(result);

	}

	@Test
	void should_pruefeDuplikatReturnFalse_when_KeineUeberschneidungWegenName() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		boolean result = klassenService.pruefeDuplikat(data, UUID_LEHRER_1);

		// Assert
		assertFalse(result);

	}

	@Test
	void should_pruefeDuplikatReturnTrue_when_UeberschneidungNeueKlasse() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2b"));

		// Act
		boolean result = klassenService.pruefeDuplikat(data, UUID_LEHRER_1);

		// Assert
		assertTrue(result);

	}

	@Test
	void should_pruefeDuplikatReturnTrue_when_UeberschneidungUnzulaessigerNamenswechsel() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2B"));

		// Act
		boolean result = klassenService.pruefeDuplikat(data, UUID_LEHRER_1);

		// Assert
		assertTrue(result);

	}

	@Test
	void should_klasseAnlegen_callTheAuthorizationService() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.klasseAnlegen(data, UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AuthException");

		} catch (AccessDeniedException e) {

		}
	}

	@Test
	void should_klasseAnlegenThrowNotFound_when_SchuleNichtAngemeldet() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(SCHULKUERZEL_2)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.klasseAnlegen(data, UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

			assertEquals(
				"KlassenService.klasseAnlegen(...): Schule mit UUID=SCHULKUERZEL_2 ist nicht zum aktuellen Wettbewerb (2020) angemeldet",
				e.getMessage());
		}
	}

	@Test
	void should_klasseAnlegen_triggerEvent() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("neu").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		KlasseAPIModel klasse = klassenService.klasseAnlegen(data, UUID_LEHRER_1);

		// Assert
		assertEquals("2c", klasse.name());
		assertNotNull(klasse.uuid());

		assertNotNull(klassenService.getKlasseCreated());
	}

	@Test
	void should_klasseUmbenennen_callTheAuthorizationService() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.klasseUmbenennen(data, UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AuthException");

		} catch (AccessDeniedException e) {

		}
	}

	@Test
	void should_klasseUmbenennenThrowNotFound_when_SchuleNichtAngemeldet() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A").withSchulkuerzel(SCHULKUERZEL_2)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.klasseUmbenennen(data, UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

			assertEquals(
				"KlassenService.klasseUmbenennen(...): Schule mit UUID=SCHULKUERZEL_2 ist nicht zum aktuellen Wettbewerb (2020) angemeldet",
				e.getMessage());
		}
	}

	@Test
	void should_klasseUmbenennenThrowNotFound_when_KlasseNichtVorhanden() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2C").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.klasseUmbenennen(data, UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

		}
	}

	@Test
	void should_klasseUmbenennen_triggerEvent() {

		// Arrange
		String expectedName = "2a - Füchse";

		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A").withSchulkuerzel(SCHULKUERZEL_1)
			.withKlasse(new KlasseEditorModel().withName(expectedName));

		// Act
		KlasseAPIModel klasse = klassenService.klasseUmbenennen(data, UUID_LEHRER_1);

		// Assert
		assertEquals("SCHULKUERZEL_1_KLASSE_2A", klasse.uuid());
		assertEquals(expectedName, klasse.name());
		assertEquals(SCHULKUERZEL_1, klasse.schulkuerzel());

		Optional<Klasse> opt = getKlassenRepository().ofIdentifier(new Identifier(klasse.uuid()));

		assertFalse(opt.isEmpty());

		Klasse gespeichert = opt.get();
		assertEquals(expectedName, gespeichert.name());

		assertNotNull(klassenService.getKlasseChanged());

	}

	////
	@Test
	void should_klasseLoeschen_callTheAuthorizationService() {

		// Act
		try {

			klassenService.klasseLoeschen("SCHULKUERZEL_1_KLASSE_2A", UUID_LEHRER_ANDERE_SCHULE);
			fail("keine AuthException");

		} catch (AccessDeniedException e) {

		}
	}

	@Test
	void should_klasseLoeschenThrowNotFound_when_schuleNichtAngemeldet() {

		// Arrange
		KlasseRequestData data = new KlasseRequestData().withUuid("SCHULKUERZEL_1_KLASSE_2A").withSchulkuerzel(SCHULKUERZEL_2)
			.withKlasse(new KlasseEditorModel().withName("2c"));

		// Act
		try {

			klassenService.klasseUmbenennen(data, UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

		}
	}

	@Test
	void should_klasseLoeschenThrowNotFound_when_klasseNichtVorhanden() {

		// Act
		try {

			klassenService.klasseLoeschen("EXISTIERT-NICHT", UUID_LEHRER_1);
			fail("keine NotFoundException");

		} catch (NotFoundException e) {

		}
	}

	@Test
	void should_klasseLoeschen_triggerEvent() {

		// Arrange
		String klasseUUID = "SCHULKUERZEL_1_KLASSE_2A";
		Identifier klasseID = new Identifier(klasseUUID);
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer(SCHULKUERZEL_1).withWettbewerbID(new WettbewerbID(WETTBEWERBSJAHR_AKTUELL));

		List<Kind> kinderMitKlasse = getKinderRepository()
			.withTeilnahme(new TeilnahmeIdentifierAktuellerWettbewerb(SCHULKUERZEL_1, Teilnahmeart.SCHULE)).stream()
			.filter(k -> klasseID.equals(k.klasseID())).collect(Collectors.toList());

		final List<Loesungszettel> alleLoesungszettel = getLoesungszettelRepository().loadAll(teilnahmeIdentifier);

		final List<Loesungszettel> zuLoeschendeLoesungszettel = new ArrayList<>();

		for (Kind kind : kinderMitKlasse) {

			Optional<Loesungszettel> optLZ = alleLoesungszettel.stream()
				.filter(lz -> kind.loesungszettelID().equals(lz.identifier())).findAny();

			if (optLZ.isPresent()) {

				zuLoeschendeLoesungszettel.add(optLZ.get());
			}
		}

		// Act
		KlasseAPIModel result = klassenService.klasseLoeschen(klasseUUID, UUID_LEHRER_1);

		assertEquals(klasseUUID, result.uuid());

		List<Kind> kinderHinterher = getKinderRepository()
			.withTeilnahme(new TeilnahmeIdentifierAktuellerWettbewerb(SCHULKUERZEL_1, Teilnahmeart.SCHULE));

		for (Kind kind : kinderMitKlasse) {

			assertNull("Fehler bei Kind " + kind + ": Lösungszettelreferenz nicht gelöscht", kind.loesungszettelID());

			Optional<Kind> optKind = kinderHinterher.stream().filter(k -> k.equals(kind)).findAny();
			assertTrue("Fehler bei Kind " + kind + ": Kind nicht gelöscht", optKind.isEmpty());

		}

		assertNotNull(klassenService.getKlasseDeleted());

	}

}
