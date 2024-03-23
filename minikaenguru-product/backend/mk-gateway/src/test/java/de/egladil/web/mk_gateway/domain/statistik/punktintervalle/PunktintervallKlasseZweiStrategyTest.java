// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.punktintervalle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.statistik.Punktintervall;

/**
 * PunktintervallKlasseZweiStrategyTest
 */
public class PunktintervallKlasseZweiStrategyTest {

	private PunktintervallKlasseZweiStrategy strategy = new PunktintervallKlasseZweiStrategy();

	@Test
	void should_createThrowException_when_minValNegative() {

		// Act + Assert
		final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {

			strategy.createPunktintervall(-1);
		});
		assertEquals("minVal muss zwischen 0 und 7500 liegen: minVal=-1", ex.getMessage());

	}

	@Test
	void should_createThrowException_when_minValTooLarge() {

		// Act + Assert
		final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {

			strategy.createPunktintervall(7501);
		});
		assertEquals("minVal muss zwischen 0 und 7500 liegen: minVal=7501", ex.getMessage());

	}

	@Test
	void should_create_produceCorrectIntervalls() {

		{

			final Punktintervall intervall = strategy.createPunktintervall(7500);
			assertEquals(7500, intervall.getMaxVal());
		}

		{

			final Punktintervall intervall = strategy.createPunktintervall(7000);
			assertEquals(7200, intervall.getMaxVal());
		}

		{

			final Punktintervall intervall = strategy.createPunktintervall(6500);
			assertEquals(6900, intervall.getMaxVal());
		}
	}

	@Test
	void should_getPunktintervalleDescending_work() {

		final List<Punktintervall> actual = strategy.getPunktintervalleDescending();
		final List<Punktintervall> expected = getExpectedPunktintervalle();
		assertEquals(expected.size(), actual.size());

		for (int i = 0; i < expected.size(); i++) {

			assertEquals(expected.get(i), actual.get(i), "Fehler bei " + i);
		}
	}

	private List<Punktintervall> getExpectedPunktintervalle() {

		final List<Punktintervall> result = new ArrayList<>();
		result.add(strategy.createPunktintervall(7500));
		result.add(strategy.createPunktintervall(7000));
		result.add(strategy.createPunktintervall(6500));
		result.add(strategy.createPunktintervall(6000));
		result.add(strategy.createPunktintervall(5500));
		result.add(strategy.createPunktintervall(5000));
		result.add(strategy.createPunktintervall(4500));
		result.add(strategy.createPunktintervall(4000));
		result.add(strategy.createPunktintervall(3500));
		result.add(strategy.createPunktintervall(3000));
		result.add(strategy.createPunktintervall(2500));
		result.add(strategy.createPunktintervall(2000));
		result.add(strategy.createPunktintervall(1500));
		result.add(strategy.createPunktintervall(1000));
		result.add(strategy.createPunktintervall(500));
		result.add(strategy.createPunktintervall(0));
		return result;
	}

}
