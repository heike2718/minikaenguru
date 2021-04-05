// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * LoesungszettelPunkteComparatorTest
 */
public class LoesungszettelPunkteComparatorTest {

	@Test
	void should_compareReturnMinusOne_when_punkte1LessThanPunkte2() {

		// Arrange
		int punkte = new Random().nextInt();

		Loesungszettel l1 = new Loesungszettel().withPunkte(punkte);
		Loesungszettel l2 = new Loesungszettel().withPunkte(punkte + 1);

		// Act + Assert
		assertEquals(-1, new LoesungszettelPunkteComparator().compare(l1, l2));
	}

	@Test
	void should_compareReturnPlusOne_when_punkte1GreaterThanPunkte2() {

		// Arrange
		int punkte = new Random().nextInt();

		Loesungszettel l1 = new Loesungszettel().withPunkte(punkte);
		Loesungszettel l2 = new Loesungszettel().withPunkte(punkte - 1);

		// Act + Assert
		assertEquals(1, new LoesungszettelPunkteComparator().compare(l1, l2));
	}

	@Test
	void should_compareReturnZero_when_punkte1AndPunkte2AreEqual() {

		// Arrange
		int punkte = new Random().nextInt();

		Loesungszettel l1 = new Loesungszettel().withPunkte(punkte);
		Loesungszettel l2 = new Loesungszettel().withPunkte(punkte);

		// Act + Assert
		assertEquals(0, new LoesungszettelPunkteComparator().compare(l1, l2));
	}

}
