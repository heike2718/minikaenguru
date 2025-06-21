// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * StatistikUtilsTest
 */
public class StatistikUtilsTest {

	List<Double> prozentRichtig;

	@BeforeEach
	void setUp() {

		prozentRichtig = Arrays.asList(new Double[] { 0.0, 5.0, 10.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0,
			25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 32.0, 33.0, 34.0, 35.0, 40.0, 45.0, 50.0, 55.0, 56.0, 57.0, 58.9, 59.0, 60.0,
			61.0, 62.0, 63.0, 64.0, 65.0, 70.0, 75.0, 76.0, 77.0, 78.0, 79.0, 80.0, 81.0, 82.0, 83.0, 84.0, 85.0, 90.0, 95.0,
			100.0 });

	}

	@Nested
	class EstimatePassungTests {

		@Test
		void estimatePassung_when_kategorieA() {

			System.err.println("Passung Kategorie A");

			for (double value : prozentRichtig) {

				Passung passung = StatistikUtils.estimatePassung(Aufgabenkategorie.A, value);

				System.out.println(value + " => " + passung);

			}
		}

		@Test
		void estimatePassung_when_kategorieB() {

			System.err.println("Passung Kategorie B");

			for (double value : prozentRichtig) {

				Passung passung = StatistikUtils.estimatePassung(Aufgabenkategorie.B, value);

				System.out.println(value + " => " + passung);

			}
		}

		@Test
		void estimatePassung_when_kategorieC() {

			System.err.println("Passung Kategorie C");

			for (double value : prozentRichtig) {

				Passung passung = StatistikUtils.estimatePassung(Aufgabenkategorie.C, value);

				System.out.println(value + " => " + passung);

			}
		}
	}

	@Nested
	class CalculateMembershipDegreeTests {

		@Test
		void calculateMembershipDegree_when_kategorieA() {

			System.err.println("membershipDegree Kategorie A");

			for (double value : prozentRichtig) {

				double membershipDegree = StatistikUtils.calculateMembershipDegree(Aufgabenkategorie.A, value);

				System.out.println(value + " => " + membershipDegree);

			}
		}

		@Test
		void calculateMembershipDegree_when_kategorieB() {

			System.err.println("membershipDegree Kategorie B");

			for (double value : prozentRichtig) {

				double membershipDegree = StatistikUtils.calculateMembershipDegree(Aufgabenkategorie.B, value);

				System.out.println(value + " => " + membershipDegree);

			}
		}

		@Test
		void calculateMembershipDegree_when_kategorieC() {

			System.err.println("membershipDegree Kategorie C");

			for (double value : prozentRichtig) {

				double membershipDegree = StatistikUtils.calculateMembershipDegree(Aufgabenkategorie.C, value);

				System.out.println(value + " => " + membershipDegree);

			}
		}

	}

	@Nested
	class ProzentTests {

		@Test
		void testProzent() {

			// Arrange
			int anteil = 105;
			int gesamt = 245;

			double expected = 42.86;

			// Act
			double result = StatistikUtils.calculatePercentRoundedUpTo2Digits(anteil, gesamt);

			// Assert
			assertEquals(expected, result);

		}

	}
}
