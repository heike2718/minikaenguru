// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.veranstalter.Kollege;

/**
 * SchuleDetailsTest
 */
public class SchuleDetailsTest {

	@Test
	void should_EqualsHashCode_be_BasedOnKuerzel() {

		// Arrange
		List<Kollege> kollegen = Arrays
			.asList(new Kollege[] { new Kollege("11111", "Alter Verwalter"), new Kollege("22222", "Strick Liesel") });

		SchuleDetails details1 = new SchuleDetails("12345").withAngemeldetDurch(new Kollege("44444", "Herta Grummlig"))
			.withAnzahlTeilnahmen(4).withHatAdv(true).withKollegen(kollegen);

		SchuleDetails details2 = new SchuleDetails("12345").withAngemeldetDurch(new Kollege("33333", "Helle Barde"))
			.withAnzahlTeilnahmen(3).withHatAdv(true);

		SchuleDetails details3 = new SchuleDetails("54321").withAngemeldetDurch(new Kollege("44444", "Herta Grummlig"))
			.withAnzahlTeilnahmen(4).withHatAdv(true).withKollegen(kollegen);

		// Act
		assertEquals(details1, details1);
		assertEquals(details1.hashCode(), details1.hashCode());

		assertEquals(details1, details2);
		assertEquals(details1.hashCode(), details2.hashCode());

		assertFalse(details1.equals(details3));
		assertFalse(details1.hashCode() == details3.hashCode());

		assertFalse(details1.equals(null));

	}

}
