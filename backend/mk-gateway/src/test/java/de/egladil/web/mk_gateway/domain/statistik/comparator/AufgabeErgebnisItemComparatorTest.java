// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.comparator;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.statistik.AufgabeErgebnisItem;
import de.egladil.web.mk_gateway.domain.statistik.comparators.AufgabeErgebnisItemComparator;

/**
 * AufgabeErgebnisItemComparatorTest
 */
public class AufgabeErgebnisItemComparatorTest {

	private AufgabeErgebnisItemComparator comparator = new AufgabeErgebnisItemComparator();

	@Test
	void should_compareReturn0_when_gleicheNummer() {

		// Arrange
		AufgabeErgebnisItem item1 = new AufgabeErgebnisItem().withNummer("B-4");
		AufgabeErgebnisItem item2 = new AufgabeErgebnisItem().withNummer("B-4");

		// Act + Assert
		assertEquals(0, comparator.compare(item1, item2));
	}

	@Test
	void should_compareReturnNegative_when_KategorieAAndB() {

		// Arrange
		AufgabeErgebnisItem item1 = new AufgabeErgebnisItem().withNummer("A-4");
		AufgabeErgebnisItem item2 = new AufgabeErgebnisItem().withNummer("B-4");

		// Act + Assert
		assertTrue(comparator.compare(item1, item2) < 0);
	}

	@Test
	void should_compareReturnPositive_when_KategorieCndB() {

		// Arrange
		AufgabeErgebnisItem item1 = new AufgabeErgebnisItem().withNummer("C-1");
		AufgabeErgebnisItem item2 = new AufgabeErgebnisItem().withNummer("B-4");

		// Act + Assert
		assertTrue(comparator.compare(item1, item2) > 0);
	}

	@Test
	void should_compareReturnNegative_when_GleicheKategorieNummer1VorNumer2() {

		// Arrange
		AufgabeErgebnisItem item1 = new AufgabeErgebnisItem().withNummer("C-3");
		AufgabeErgebnisItem item2 = new AufgabeErgebnisItem().withNummer("C-4");

		// Act + Assert
		assertTrue(comparator.compare(item1, item2) < 0);
	}

	@Test
	void should_compareReturnNegative_when_GleicheKategorieNummer1NachNumer2() {

		// Arrange
		AufgabeErgebnisItem item1 = new AufgabeErgebnisItem().withNummer("C-3");
		AufgabeErgebnisItem item2 = new AufgabeErgebnisItem().withNummer("C-1");

		// Act + Assert
		assertTrue(comparator.compare(item1, item2) > 0);
	}
}
