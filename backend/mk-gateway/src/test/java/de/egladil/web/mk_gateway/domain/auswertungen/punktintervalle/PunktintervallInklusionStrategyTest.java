// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen.punktintervalle;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.auswertungen.Punktintervall;

/**
 * PunktintervallInklusionStrategyTest
 */
public class PunktintervallInklusionStrategyTest {

	private PunktintervallInklusionStrategy strategy = new PunktintervallInklusionStrategy();

	@Test
	void should_createThrowException_when_minValNegative() {

		// Act + Assert
		final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {

			strategy.createPunktintervall(-1);
		});
		assertEquals("minVal muss zwischen 0 und 3600 liegen: minVal=-1", ex.getMessage());

	}

	@Test
	void should_createThrowException_when_minValTooLarge() {

		// Act + Assert
		final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {

			strategy.createPunktintervall(3601);
		});
		assertEquals("minVal muss zwischen 0 und 3600 liegen: minVal=3601", ex.getMessage());

	}

	@Test
	void should_createProducesCorrectIntervalls() {

		{

			final Punktintervall intervall = strategy.createPunktintervall(3600);
			assertEquals(3600, intervall.getMaxVal());
		}

		{

			final Punktintervall intervall = strategy.createPunktintervall(3000);
			assertEquals(3300, intervall.getMaxVal());
		}
	}

	@Test
	void should_getPunktintervalleDescending_work() {

		final List<Punktintervall> actual = strategy.getPunktintervalleDescending();
		final List<Punktintervall> expected = getExpectedPunktintervalle();
		assertEquals(expected.size(), actual.size());

		for (int i = 0; i < expected.size(); i++) {

			assertEquals("Fehler bei " + i, expected.get(i), actual.get(i));
		}
	}

	private List<Punktintervall> getExpectedPunktintervalle() {

		final List<Punktintervall> result = new ArrayList<>();
		result.add(new Punktintervall.Builder(3600).maxVal(3600).build());
		result.add(new Punktintervall.Builder(3000).maxVal(3300).build());
		result.add(new Punktintervall.Builder(2500).maxVal(2900).build());
		result.add(new Punktintervall.Builder(2000).maxVal(2450).build());
		result.add(new Punktintervall.Builder(1500).maxVal(1950).build());
		result.add(new Punktintervall.Builder(1000).maxVal(1450).build());
		result.add(new Punktintervall.Builder(500).maxVal(950).build());
		result.add(new Punktintervall.Builder(0).maxVal(450).build());
		return result;
	}

}
