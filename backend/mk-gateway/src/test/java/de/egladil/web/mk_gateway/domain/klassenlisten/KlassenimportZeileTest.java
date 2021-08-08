// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * KlassenimportZeileTest
 */
public class KlassenimportZeileTest {

	@Test
	void should_mapKlassenstufeReturnEmpty_when_klassenstufeNaN() {

		// Arrange
		String klassenstufe = "1A";
		KlassenimportZeile zeile = new KlassenimportZeile().withKlassenstufe(klassenstufe);

		// Act + Assert
		assertTrue(zeile.mapKlassenstufe().isEmpty());

	}

	@Test
	void should_mapKlassenstufeReturnOptZWEI_when_klassenstufe2() {

		// Arrange
		String klassenstufe = "2";
		KlassenimportZeile zeile = new KlassenimportZeile().withKlassenstufe(klassenstufe);

		// Act + Assert
		assertEquals(Klassenstufe.ZWEI, zeile.mapKlassenstufe().get());

	}

	@Test
	void should_equalsAndHashCode_baseOnAllAttributes() {

		// Arrange
		KlassenimportZeile zeile1 = new KlassenimportZeile().withKlasse("2A").withKlassenstufe("2").withNachname("GeiEr")
			.withVorname("VolKer");
		KlassenimportZeile zeile2 = new KlassenimportZeile().withKlasse("2A").withKlassenstufe("2").withNachname("GeiEr")
			.withVorname("VolKer");
		KlassenimportZeile zeile3 = new KlassenimportZeile().withKlasse("2B").withKlassenstufe("2").withNachname("GeiEr")
			.withVorname("VolKer");

		// Assert
		assertEquals(zeile1, zeile1);
		assertEquals(zeile1, zeile2);
		assertFalse(zeile1.equals(zeile3));

		assertEquals(zeile1.hashCode(), zeile2.hashCode());
		assertFalse(zeile1.hashCode() == zeile3.hashCode());

		assertFalse(zeile1.equals(null));
		assertFalse(zeile1.equals(new Object()));

	}

}
