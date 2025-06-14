// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.veranstalter.Kollege;

/**
 * SchuleAPIModelTest
 */
public class SchuleAPIModelTest {

	@Test
	void should_staticMethodWithKuerzel_initTheKuerzel() {

		// Arrange
		String kuerzel = "HALLO";

		// Act
		SchuleAPIModel result = new SchuleAPIModel().withKuerzel(kuerzel);

		// Assert
		assertEquals("HALLO", result.kuerzel());
		assertNull(result.name());
		assertNull(result.ort());
		assertNull(result.land());
		assertNull(result.kuerzelLand());
		assertNull(result.details());
		assertFalse(result.aktuellAngemeldet());
	}

	@Test
	void should_staticMethodWithKuerzelLand_initTheKuerzelLand() {

		// Arrange
		String kuerzel = "HALLO";

		// Act
		SchuleAPIModel result = new SchuleAPIModel().withKuerzelLand(kuerzel);

		// Assert
		assertEquals("HALLO", result.kuerzelLand());
		assertNull(result.name());
		assertNull(result.ort());
		assertNull(result.land());
		assertNull(result.kuerzel());
		assertNull(result.details());
		assertFalse(result.aktuellAngemeldet());
	}

	@Test
	void should_fluentApiWork() {

		// Act
		List<Kollege> kollegen = Arrays
			.asList(new Kollege[] { new Kollege("11111", "Alter Verwalter"), new Kollege("22222", "Strick Liesel") });

		SchuleDetails details = new SchuleDetails("12345").withAngemeldetDurch(new Kollege("44444", "Herta Grummlig"))
			.withAnzahlTeilnahmen(4).withHatAdv(true).withKollegen(kollegen).withNameUrkunde("David-Hilbert-Schule");

		// Act
		SchuleAPIModel model = new SchuleAPIModel().withKuerzel("12345").withName("David-Hilbert-Schule").withOrt("Göttingen")
			.withKuerzelLand("DE-NI").withLand("Niedersachsen").withAktuellAngemeldet(true).withDetails(details);

		// Assert
		assertEquals("12345", model.kuerzel());
		assertEquals("David-Hilbert-Schule", model.name());
		assertEquals("Göttingen", model.ort());
		assertEquals("Niedersachsen", model.land());
		assertEquals("DE-NI", model.kuerzelLand());
		assertTrue(model.aktuellAngemeldet());

		SchuleDetails modelDetails = model.details();
		assertEquals(details, modelDetails);
		assertEquals("Herta Grummlig", modelDetails.angemeldetDurch());
		assertEquals(4, modelDetails.anzahlTeilnahmen());
		assertTrue(modelDetails.hatAdv());
		assertEquals("Herta Grummlig", modelDetails.angemeldetDurch());
		assertEquals("12345", modelDetails.kuerzel());
		assertEquals("Alter Verwalter, Strick Liesel", modelDetails.kollegen());
		assertEquals("David-Hilbert-Schule", modelDetails.nameUrkunde());
	}

	@Test
	void should_EqualsHashCode_be_BasedOnKuerzel() {

		// Arrange
		SchuleAPIModel result1 = new SchuleAPIModel().withKuerzel("11111");
		SchuleAPIModel result2 = new SchuleAPIModel().withKuerzel("11111");
		SchuleAPIModel result3 = new SchuleAPIModel().withKuerzel("33333");

		// Act
		assertEquals(result1, result1);
		assertEquals(result1.hashCode(), result1.hashCode());

		assertEquals(result1, result2);
		assertEquals(result1.hashCode(), result2.hashCode());

		assertFalse(result1.equals(result3));
		assertFalse(result1.hashCode() == result3.hashCode());

		assertFalse(result1.equals(null));

	}

}
