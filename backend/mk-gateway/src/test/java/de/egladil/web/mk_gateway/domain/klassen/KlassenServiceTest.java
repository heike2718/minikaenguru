// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassen;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.auswertungen.LoesungszettelService;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.klassen.api.KlasseEditorModel;
import de.egladil.web.mk_gateway.domain.klassen.api.KlasseRequestData;

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

		klassenService = KlassenService.createForTest(authService, getKinderRepository(), getTeilnahmenRepository(),
			getVeranstalterRepository(),
			getWettbewerbService(), LoesungszettelService.createForTest(authService, getLoesungszettelRepository()),
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
		assertNotNull(klasse.getUuid());

		assertNotNull(klassenService.getKlasseCreated());
	}

}
