// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.veranstalter.Kollege;
import de.egladil.web.mk_gateway.domain.veranstalter.Schulkollegium;

/**
 * KollegiumFilterTest
 */
public class KollegiumFilterTest {

	@Test
	void should_constructorAcceptNullRequestingLehrer() {

		new KollegiumFilter(null, "qgugdwug");
	}

	void should_constructorAcceptNullTestLehrer() {

		new KollegiumFilter(new Identifier("qgugdwug"), null);
	}

	@Test
	void should_notReturnTheTestLehrer_when_lehrerIdentifierNotNull() {

		// Arrange
		final String uuidTestlehrer = "jakhdhq";
		final String uuidRequestingLehrer = "jkashdkhq";
		String uuidOtherLehrer = "shjkahakjshdq";
		Kollege expectedKollege = new Kollege(uuidOtherLehrer, "Dieter");

		List<Kollege> kollegen = new ArrayList<>();
		kollegen.add(expectedKollege);
		kollegen.add(new Kollege(uuidRequestingLehrer, "Herta"));
		kollegen.add(new Kollege(uuidTestlehrer, "Iche"));

		Schulkollegium kollegium = new Schulkollegium(new Identifier("AVBGTFG7"), kollegen.toArray(new Kollege[] {}));

		KollegiumFilter kollegiumFilter = new KollegiumFilter(new Identifier(uuidRequestingLehrer), uuidTestlehrer);

		// Act
		List<Kollege> resultingKollegen = kollegiumFilter.apply(kollegium);

		// Assert
		assertEquals(1, resultingKollegen.size());
		assertEquals(expectedKollege, resultingKollegen.get(0));
	}

	@Test
	void should_returnOtherLehrers_when_lehrerIdentifierNotNullButTestlehrerNull() {

		// Arrange
		final String uuidTestlehrer = "jakhdhq";
		final String uuidRequestingLehrer = "jkashdkhq";
		String uuidOtherLehrer = "shjkahakjshdq";
		Kollege expectedKollege1 = new Kollege(uuidOtherLehrer, "Dieter");
		Kollege expectedKollege2 = new Kollege(uuidTestlehrer, "Iche");

		List<Kollege> kollegen = new ArrayList<>();
		kollegen.add(expectedKollege1);
		kollegen.add(new Kollege(uuidRequestingLehrer, "Herta"));
		kollegen.add(expectedKollege2);

		Schulkollegium kollegium = new Schulkollegium(new Identifier("AVBGTFG7"), kollegen.toArray(new Kollege[] {}));

		KollegiumFilter kollegiumFilter = new KollegiumFilter(new Identifier(uuidRequestingLehrer), null);

		// Act
		List<Kollege> resultingKollegen = kollegiumFilter.apply(kollegium);

		// Assert
		assertEquals(2, resultingKollegen.size());
		assertTrue(resultingKollegen.contains(expectedKollege1));
		assertTrue(resultingKollegen.contains(expectedKollege2));
	}

	@Test
	void should_returnAllTestLehrer_when_lehrerIdentifierNull() {

		// Arrange
		final String uuidTestlehrer = "jakhdhq";
		final String uuidRequestingLehrer = "jkashdkhq";
		String uuidOtherLehrer = "shjkahakjshdq";
		Kollege expectedKollege1 = new Kollege(uuidOtherLehrer, "Dieter");
		Kollege expectedKollege2 = new Kollege(uuidRequestingLehrer, "Herta");
		Kollege expectedKollege3 = new Kollege(uuidTestlehrer, "Iche");

		List<Kollege> kollegen = new ArrayList<>();
		kollegen.add(expectedKollege1);
		kollegen.add(expectedKollege2);
		kollegen.add(expectedKollege3);

		Schulkollegium kollegium = new Schulkollegium(new Identifier("AVBGTFG7"), kollegen.toArray(new Kollege[] {}));

		KollegiumFilter kollegiumFilter = new KollegiumFilter(null, uuidTestlehrer);

		// Act
		List<Kollege> resultingKollegen = kollegiumFilter.apply(kollegium);

		// Assert
		assertEquals(3, resultingKollegen.size());
		assertTrue(resultingKollegen.contains(expectedKollege1));
		assertTrue(resultingKollegen.contains(expectedKollege2));
		assertTrue(resultingKollegen.contains(expectedKollege3));
	}

}
