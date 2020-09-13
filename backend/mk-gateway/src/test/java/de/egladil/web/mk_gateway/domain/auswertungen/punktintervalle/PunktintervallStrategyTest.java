// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen.punktintervalle;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * PunktintervallStrategyTest
 */
public class PunktintervallStrategyTest {

	@Test
	void should_createStrategyProduceKlasseZwei_when_KlassenstufeEinsAndJahrVor2017() {

		// Arrange
		Klassenstufe klassenstufe = Klassenstufe.EINS;
		Integer jahr = Math.abs(new Random().nextInt(12)) + 2005;
		WettbewerbID wettbewerbId = new WettbewerbID(jahr);

		// Act
		PunktintervallStrategy strategy = PunktintervallStrategy.createStrategy(klassenstufe, wettbewerbId);

		// Assert
		assertEquals(Klassenstufe.ZWEI, strategy.klassenstufe());

	}

	@Test
	void should_createStrategyProduceKlasseEins_when_KlassenstufeEinsAndJahrAb2017() {

		// Arrange
		Klassenstufe klassenstufe = Klassenstufe.EINS;
		Integer jahr = Math.abs(new Random().nextInt()) + 2017;
		WettbewerbID wettbewerbId = new WettbewerbID(jahr);

		// Act
		PunktintervallStrategy strategy = PunktintervallStrategy.createStrategy(klassenstufe, wettbewerbId);

		// Assert
		assertEquals(Klassenstufe.EINS, strategy.klassenstufe());

	}

	@Test
	void should_createStrategyProduceKlasseZwei_when_KlassenstufeZwei() {

		// Arrange
		Klassenstufe klassenstufe = Klassenstufe.ZWEI;
		Integer jahr = Math.abs(new Random().nextInt()) + 2005;
		WettbewerbID wettbewerbId = new WettbewerbID(jahr);

		// Act
		PunktintervallStrategy strategy = PunktintervallStrategy.createStrategy(klassenstufe, wettbewerbId);

		// Assert
		assertEquals(Klassenstufe.ZWEI, strategy.klassenstufe());

	}

	@Test
	void should_createStrategyProduceIKids_when_KlassenstufeIKids() {

		// Arrange
		Klassenstufe klassenstufe = Klassenstufe.IKID;
		Integer jahr = Math.abs(new Random().nextInt()) + 2005;
		WettbewerbID wettbewerbId = new WettbewerbID(jahr);

		// Act
		PunktintervallStrategy strategy = PunktintervallStrategy.createStrategy(klassenstufe, wettbewerbId);

		// Assert
		assertEquals(Klassenstufe.IKID, strategy.klassenstufe());

	}

}
