// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		SchuleAPIModel result = SchuleAPIModel.withKuerzel(kuerzel);

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
		SchuleAPIModel result = SchuleAPIModel.withKuerzelLand(kuerzel);

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
	void should_staticMethodWithMap_initAllSimpleAttributes() {

		// Arrange
		Map<String, Object> schuleWettbewerbMap = new HashMap<>();

		schuleWettbewerbMap.put("kuerzel", "12345");
		schuleWettbewerbMap.put("name", "David-Hilbert-Schule");
		schuleWettbewerbMap.put("ort", "Göttingen");
		schuleWettbewerbMap.put("land", "Niedersachsen");
		schuleWettbewerbMap.put("kuerzelLand", "DE-NI");
		schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.FALSE);

		// Act
		SchuleAPIModel model = SchuleAPIModel.withAttributes(schuleWettbewerbMap);

		// Assert
		assertEquals("12345", model.kuerzel());
		assertEquals("David-Hilbert-Schule", model.name());
		assertEquals("Göttingen", model.ort());
		assertEquals("Niedersachsen", model.land());
		assertEquals("DE-NI", model.kuerzelLand());
		assertEquals(Boolean.FALSE, model.aktuellAngemeldet());
		assertNull(model.details());
	}

	@Test
	void should_fluentApiWork() {

		// Act
		Map<String, Object> schuleWettbewerbMap = new HashMap<>();

		schuleWettbewerbMap.put("kuerzel", "12345");
		schuleWettbewerbMap.put("name", "David-Hilbert-Schule");
		schuleWettbewerbMap.put("ort", "Göttingen");
		schuleWettbewerbMap.put("land", "Niedersachsen");
		schuleWettbewerbMap.put("kuerzelLand", "DE-NI");
		schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.FALSE);

		List<Kollege> kollegen = Arrays
			.asList(new Kollege[] { new Kollege("11111", "Alter Verwalter"), new Kollege("22222", "Strick Liesel") });

		SchuleDetails details = new SchuleDetails("12345").withAngemeldetDurch(new Kollege("44444", "Herta Grummlig"))
			.withAnzahlTeilnahmen(4).withHatAdv(true).withKollegen(kollegen).withNameUrkunde("David-Hilbert-Schule");

		// Act
		SchuleAPIModel model = SchuleAPIModel.withAttributes(schuleWettbewerbMap).withAngemeldet(true).withDetails(details);

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
		SchuleAPIModel result1 = SchuleAPIModel.withKuerzel("11111");
		SchuleAPIModel result2 = SchuleAPIModel.withKuerzel("11111");
		SchuleAPIModel result3 = SchuleAPIModel.withKuerzel("33333");

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
