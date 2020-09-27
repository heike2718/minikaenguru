// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * AufgabeErgebnisItemTest
 */
public class AufgabeErgebnisItemTest {

	@Test
	void should_equalsAndHashCode_baseOn_Index() {

		// Arrange
		AufgabeErgebnisItem item1 = new AufgabeErgebnisItem().withNummer("A-3").withIndex(2);
		AufgabeErgebnisItem item2 = new AufgabeErgebnisItem().withNummer("A-2").withIndex(2);
		AufgabeErgebnisItem item3 = new AufgabeErgebnisItem().withNummer("B-3").withIndex(7);

		// Assert
		assertTrue(item1.equals(item1));
		assertTrue(item1.equals(item2));
		assertFalse(item1.equals(item3));

		assertEquals(item1.hashCode(), item1.hashCode());
		assertEquals(item1.hashCode(), item2.hashCode());
		assertFalse(item1.hashCode() == item3.hashCode());
		assertFalse(item1.equals(null));
		assertFalse(item1.equals(new Object()));

		assertTrue(item1.compareTo(item2) == 0);
		assertTrue(item1.compareTo(item3) < 0);

	}

	@Test
	void test_toString_and_schwierigkeit() {

		{

			AufgabeErgebnisItem item = new AufgabeErgebnisItem().withNummer("A-1").withIndex(0).withAnzahlRichtigGeloest(14)
				.withAnteilRichtigGeloest(67.0);

			assertEquals(
				"AufgabeErgebnisItem [nummer=A-1, richtig=14, anteilRichtig=67,0, falsch=0, anteilFalsch=null, nichtGeloest=0, anteilNichtGeloest=null]",
				item.toString());

			assertFalse(item.isZuLeicht());
			assertFalse(item.isZuSchwer());
		}

		{

			AufgabeErgebnisItem item = new AufgabeErgebnisItem().withNummer("A-1").withIndex(0).withAnzahlRichtigGeloest(14)
				.withAnteilRichtigGeloest(66.99);

			assertFalse(item.isZuLeicht());
			assertTrue(item.isZuSchwer());
		}

		{

			AufgabeErgebnisItem item = new AufgabeErgebnisItem().withNummer("B-1").withIndex(0).withAnzahlRichtigGeloest(14)
				.withAnteilRichtigGeloest(66.01);

			assertTrue(item.isZuLeicht());
			assertFalse(item.isZuSchwer());
		}

		{

			AufgabeErgebnisItem item = new AufgabeErgebnisItem().withNummer("B-1").withIndex(0).withAnzahlRichtigGeloest(14)
				.withAnteilRichtigGeloest(66.0);

			assertFalse(item.isZuLeicht());
			assertFalse(item.isZuSchwer());
		}

		{

			AufgabeErgebnisItem item = new AufgabeErgebnisItem().withNummer("B-1").withIndex(0).withAnzahlRichtigGeloest(14)
				.withAnteilRichtigGeloest(34.0);

			assertFalse(item.isZuLeicht());
			assertFalse(item.isZuSchwer());
		}

		{

			AufgabeErgebnisItem item = new AufgabeErgebnisItem().withNummer("B-1").withIndex(0).withAnzahlRichtigGeloest(14)
				.withAnteilRichtigGeloest(33.99);

			assertFalse(item.isZuLeicht());
			assertTrue(item.isZuSchwer());
		}

		{

			AufgabeErgebnisItem item = new AufgabeErgebnisItem().withNummer("C-1").withIndex(0).withAnzahlRichtigGeloest(14)
				.withAnteilRichtigGeloest(33.01);

			assertTrue(item.isZuLeicht());
			assertFalse(item.isZuSchwer());
		}

		{

			AufgabeErgebnisItem item = new AufgabeErgebnisItem().withNummer("C-1").withIndex(0).withAnzahlRichtigGeloest(14)
				.withAnteilRichtigGeloest(33.00);

			assertFalse(item.isZuLeicht());
			assertFalse(item.isZuSchwer());
		}

	}

}
