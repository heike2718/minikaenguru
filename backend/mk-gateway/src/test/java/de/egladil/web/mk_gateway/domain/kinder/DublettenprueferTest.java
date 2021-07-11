// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * DublettenprueferTest
 */
public class DublettenprueferTest {

	private Dublettenpruefer dublettenpruefer = new Dublettenpruefer();

	private KindAdapter kindAdapter = new KindAdapter();

	@Nested
	class KinderTests {

		@Test
		void should_NeuesKindNotBeDoubleOfItself() {

			// Arrange
			Kind kind1 = new Kind().withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts");
			KindAdaptable adaptedKind = kindAdapter.adaptKind(kind1);
			assertTrue(adaptedKind.isNeu());

			// Act
			Boolean result = dublettenpruefer.apply(adaptedKind, adaptedKind);

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

		@Test
		void should_NeuesKindBeDoubleOfAnotherNeuesKind() {

			// Arrange
			Kind kind1 = new Kind().withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts");
			KindAdaptable adaptedKind1 = kindAdapter.adaptKind(kind1);

			Kind kind2 = new Kind().withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts");
			KindAdaptable adaptedKind2 = kindAdapter.adaptKind(kind2);

			// Act
			Boolean result = dublettenpruefer.apply(adaptedKind1, adaptedKind2);

			// Assert
			assertEquals(Boolean.TRUE, result);
		}

		@Test
		void should_VorhandenesKindNotBeDoubleOfItself() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("hdihHHAIOHo")).withVorname("HarAld").withNachname("HEimeLig")
				.withZusatz("Bank rechts");
			KindAdaptable adaptedKind = kindAdapter.adaptKind(kind1);
			assertFalse(adaptedKind.isNeu());

			// Act
			Boolean result = dublettenpruefer.apply(adaptedKind, adaptedKind);

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

		@Test
		void should_VorhandenesKindNotBeDoubleOfOtherVorhandenesKind() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("hdihHHAIOHo")).withVorname("HarAld").withNachname("HEimeLig")
				.withZusatz("Bank rechts");
			KindAdaptable adaptedKind1 = kindAdapter.adaptKind(kind1);

			Kind kind2 = new Kind(new Identifier("HDIHHHAIOHO")).withVorname("HarAld").withNachname("HEimeLig")
				.withZusatz("Bank rechts");
			KindAdaptable adaptedKind2 = kindAdapter.adaptKind(kind2);

			// Act
			Boolean result = dublettenpruefer.apply(adaptedKind1, adaptedKind2);

			// Assert
			assertEquals(Boolean.TRUE, result);
		}

		@Test
		void should_KindBeDoubleWhenAttributesEqualAndKind2IsNeu() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("ajsdgqg")).withVorname("HarAld").withNachname("HEimeLig")
				.withZusatz("Bank rechts");
			Kind kind2 = new Kind().withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts");

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.TRUE, result);
		}

		@Test
		void should_KindBeDoubleWhenAttributesEqualAndKind1IsNeu() {

			// Arrange
			Kind kind1 = new Kind().withVorname("HarAld").withNachname("HEimeLig")
				.withZusatz("Bank rechts");
			Kind kind2 = new Kind(new Identifier("ajsdgqg")).withVorname("HarAld").withNachname("HEimeLig")
				.withZusatz("Bank rechts");

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.TRUE, result);
		}

		@Test
		void should_KinderNotBePossibleDoubles_when_klassenstufeDiffersAndKlasseIDAndAllNamesNotNullAndEqual() {

			// Arrange
			Identifier klasseID = new Identifier("Hasen");

			Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.ZWEI).withKlasseID(klasseID);
			Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS).withKlasseID(klasseID);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

		@Test
		void should_KinderBePossibleDoubles_when_klassenstufeAndKlasseIDAndAllNamesNotNullAndEqual() {

			// Arrange
			Identifier klasseID = new Identifier("Hasen");

			Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.ZWEI).withKlasseID(klasseID);
			Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.ZWEI).withKlasseID(klasseID);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.TRUE, result);
		}

		@Test
		void should_KinderBePossibleDoubles_when_klassenstufeAndAllNamesNotNullAndEqual() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS);
			Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.TRUE, result);
		}

		@Test
		void should_KinderNotBePossibleDoubles_when_klassenstufeDiffersAndllNamesNotNullAndEqual() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.ZWEI);
			Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

		@Test
		void should_KinderBePossibleDoubles_when_klassenstufeAnsAllNamesButVornameNullAndVornameEqual() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withKlassenstufe(Klassenstufe.EINS);
			Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withKlassenstufe(Klassenstufe.EINS);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.TRUE, result);
		}

		@Test
		void should_KinderNotBePossibleDoubles_when_oneVornameNull() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("eins")).withNachname("HEimeLig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS);
			Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

		@Test
		void should_KinderNotBePossibleDoubles_when_oneVornameBlank() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("eins")).withVorname("  ").withNachname("HEimeLig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS);
			Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

		///////////////////////////////////////////////////////

		@Test
		void should_KinderBePossibleDoubles_when_allNamesButNachnameNullAndKlassenstufeAndVornameEqual() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("eins")).withNachname("HEimeLig").withKlassenstufe(Klassenstufe.EINS);
			Kind kind2 = new Kind(new Identifier("zwei")).withNachname("Heimelig").withKlassenstufe(Klassenstufe.EINS);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.TRUE, result);
		}

		@Test
		void should_KinderNotBePossibleDoubles_when_oneNachnameNull() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS);
			Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

		@Test
		void should_KinderNotBePossibleDoubles_when_oneNachnameBlank() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("HEimeLig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS);
			Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("    ").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

		///////////////////////////////////////////////////////

		@Test
		void should_KinderBePossibleDoubles_when_klassenstufeAndAllNamesButZusatzNull() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("eins")).withZusatz("HEimeLig").withKlassenstufe(Klassenstufe.EINS);
			Kind kind2 = new Kind(new Identifier("zwei")).withZusatz("Heimelig").withKlassenstufe(Klassenstufe.EINS);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.TRUE, result);
		}

		@Test
		void should_KinderNotBePossibleDoubles_when_oneZusatzNull() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("Heimelig")
				.withKlassenstufe(Klassenstufe.EINS);
			Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

		@Test
		void should_KinderNotBePossibleDoubles_when_oneZusatzBlank() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("eins")).withVorname("HarAld").withNachname("Heimelig").withZusatz("  ")
				.withKlassenstufe(Klassenstufe.EINS);
			Kind kind2 = new Kind(new Identifier("zwei")).withVorname("Harald").withNachname("Heimelig").withZusatz("Bank rechts")
				.withKlassenstufe(Klassenstufe.EINS);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.FALSE, result);
		}
	}

	@Nested
	class MixedTests {

		@Test
		void should_KindBeDoubleWhenAttributesEqualAndKind1IsPersistentKindAndKind2IsRequestedKindNeu() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("eine-uuid")).withVorname("HarAld").withNachname("HEimeLig")
				.withZusatz("Bank rechts").withKlasseID(new Identifier("klasse-uuid")).withKlassenstufe(Klassenstufe.EINS)
				.withSprache(Sprache.en);

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, Sprache.en).withNachname("Heimelig")
				.withVorname("Harald").withZusatz("Bank rechts").withKlasseUuid("klasse-uuid");

			KindRequestData kind2 = new KindRequestData().withKind(kindEditorModel).withUuid(KindRequestData.KEINE_UUID);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKindRequestData(kind2));

			// Assert
			assertEquals(Boolean.TRUE, result);
		}

		@Test
		void should_KindBeDoubleWhenAttributesEqualAndKind2IsPersistentKindAndKind1IsRequestedKindNeu() {

			// Arrange
			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, Sprache.en).withNachname("Heimelig")
				.withVorname("Harald").withZusatz("Bank rechts").withKlasseUuid("klasse-uuid");

			KindRequestData kind1 = new KindRequestData().withKind(kindEditorModel).withUuid(KindRequestData.KEINE_UUID);

			Kind kind2 = new Kind(new Identifier("eine-uuid")).withVorname("HarAld").withNachname("HEimeLig")
				.withZusatz("Bank rechts").withKlasseID(new Identifier("klasse-uuid")).withKlassenstufe(Klassenstufe.EINS)
				.withSprache(Sprache.en);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKindRequestData(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.TRUE, result);
		}

		@Test
		void should_NotKindBeDoubleWhenAttributesEqualAndKind1IsPersistentKindAndKind2IsRequestedKindAlt() {

			// Arrange
			Kind kind1 = new Kind(new Identifier("eine-uuid")).withVorname("HarAld").withNachname("HEimeLig")
				.withZusatz("Bank rechts").withKlasseID(new Identifier("klasse-uuid")).withKlassenstufe(Klassenstufe.EINS)
				.withSprache(Sprache.en);

			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, Sprache.en).withNachname("Heimelig")
				.withVorname("Harald").withZusatz("Bank rechts").withKlasseUuid("klasse-uuid");

			KindRequestData kind2 = new KindRequestData().withKind(kindEditorModel).withUuid("eine-uuid");

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKind(kind1), kindAdapter.adaptKindRequestData(kind2));

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

		@Test
		void should_NotKindBeDoubleWhenAttributesEqualAndKind2IsPersistentKindAndKind1IsRequestedKindAlt() {

			// Arrange
			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, Sprache.en).withNachname("Heimelig")
				.withVorname("Harald").withZusatz("Bank rechts").withKlasseUuid("klasse-uuid");

			KindRequestData kind1 = new KindRequestData().withKind(kindEditorModel).withUuid("eine-uuid");

			Kind kind2 = new Kind(new Identifier("eine-uuid")).withVorname("HarAld").withNachname("HEimeLig")
				.withZusatz("Bank rechts").withKlasseID(new Identifier("klasse-uuid")).withKlassenstufe(Klassenstufe.EINS)
				.withSprache(Sprache.en);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKindRequestData(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

		@Test
		void should_NotKindBeDoubleWhenAttributesEqualAndKind2IsPersistentKindAndKind1IsRequestedKindAltAndKlasseUUIDNull() {

			// Arrange
			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, Sprache.en).withNachname("Heimelig")
				.withVorname("Harald").withZusatz("Bank rechts");

			KindRequestData kind1 = new KindRequestData().withKind(kindEditorModel).withUuid("eine-uuid");

			Kind kind2 = new Kind(new Identifier("eine-uuid")).withVorname("HarAld").withNachname("HEimeLig")
				.withZusatz("Bank rechts").withKlassenstufe(Klassenstufe.EINS)
				.withSprache(Sprache.en);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKindRequestData(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

		@Test
		void should_NotKindBeDoubleWhenKind1KlasseUUIDNullKind2KlasseUUIDNotNull() {

			// Arrange
			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, Sprache.en).withNachname("Heimelig")
				.withVorname("Harald").withZusatz("Bank rechts");

			KindRequestData kind1 = new KindRequestData().withKind(kindEditorModel).withUuid("eine-uuid");

			Kind kind2 = new Kind(new Identifier("andere-uuid")).withVorname("HarAld").withNachname("HEimeLig")
				.withZusatz("Bank rechts").withKlassenstufe(Klassenstufe.EINS)
				.withSprache(Sprache.en).withKlasseID(new Identifier("uuid-klasse"));

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKindRequestData(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

		@Test
		void should_NotKindBeDoubleWhenKind1KlasseUUIDNotNullKind2KlasseUUIDNull() {

			// Arrange
			KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, Sprache.en).withNachname("Heimelig")
				.withVorname("Harald").withZusatz("Bank rechts").withKlasseUuid("uuid-klasse");

			KindRequestData kind1 = new KindRequestData().withKind(kindEditorModel).withUuid("eine-uuid");

			Kind kind2 = new Kind(new Identifier("andere-uuid")).withVorname("HarAld").withNachname("HEimeLig")
				.withZusatz("Bank rechts").withKlassenstufe(Klassenstufe.EINS)
				.withSprache(Sprache.en);

			// Act
			Boolean result = dublettenpruefer.apply(kindAdapter.adaptKindRequestData(kind1), kindAdapter.adaptKind(kind2));

			// Assert
			assertEquals(Boolean.FALSE, result);
		}

	}
}
