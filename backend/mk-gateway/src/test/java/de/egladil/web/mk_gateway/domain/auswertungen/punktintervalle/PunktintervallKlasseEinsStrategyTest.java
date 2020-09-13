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
 * PunktintervallKlasseEinsStrategyTest
 */
public class PunktintervallKlasseEinsStrategyTest {

	private PunktintervallKlasseEinsStrategy factory = new PunktintervallKlasseEinsStrategy();

	@Test
	void should_createThrowException_when_minValNegative() {

		// Act + Assert
		final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {

			factory.createPunktintervall(-1);
		});
		assertEquals("minVal muss zwischen 0 und 6000 liegen: minVal=-1", ex.getMessage());

	}

	@Test
	void should_createThrowException_when_minValTooLarge() {

		// Act + Assert
		final Throwable ex = assertThrows(IllegalArgumentException.class, () -> {

			factory.createPunktintervall(6001);
		});
		assertEquals("minVal muss zwischen 0 und 6000 liegen: minVal=6001", ex.getMessage());

	}

	@Test
	void should_create_produceCorrectIntervalls() {

		{

			final Punktintervall intervall = factory.createPunktintervall(6000);
			assertEquals(6000, intervall.getMaxVal());
		}

		{

			final Punktintervall intervall = factory.createPunktintervall(5000);
			assertEquals(5400, intervall.getMaxVal());
		}

		{

			final Punktintervall intervall = factory.createPunktintervall(5500);
			assertEquals(5700, intervall.getMaxVal());
		}
	}

	@Test
	void should_getPunktintervalleDescending_work() {

		final List<Punktintervall> actual = factory.getPunktintervalleDescending();
		final List<Punktintervall> expected = getExpectedPunktintervalle();
		assertEquals(expected.size(), actual.size());

		for (int i = 0; i < expected.size(); i++) {

			assertEquals("Fehler bei " + i, expected.get(i), actual.get(i));
		}
	}

	private List<Punktintervall> getExpectedPunktintervalle() {

		final List<Punktintervall> result = new ArrayList<>();
		result.add(factory.createPunktintervall(6000));
		result.add(factory.createPunktintervall(5500));
		result.add(factory.createPunktintervall(5000));
		result.add(factory.createPunktintervall(4500));
		result.add(factory.createPunktintervall(4000));
		result.add(factory.createPunktintervall(3500));
		result.add(factory.createPunktintervall(3000));
		result.add(factory.createPunktintervall(2500));
		result.add(factory.createPunktintervall(2000));
		result.add(factory.createPunktintervall(1500));
		result.add(factory.createPunktintervall(1000));
		result.add(factory.createPunktintervall(500));
		result.add(factory.createPunktintervall(0));
		return result;
	}
}
