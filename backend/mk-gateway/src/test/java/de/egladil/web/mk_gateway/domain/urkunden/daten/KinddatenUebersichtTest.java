// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.daten;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * KinddatenUebersichtTest
 */
public class KinddatenUebersichtTest {

	@Test
	void should_sortPunkte_descending() {

		// Arrange
		List<KinddatenUebersicht> daten = new ArrayList<>();

		{

			KinddatenUebersicht kind = new KinddatenUebersicht().withUuid("UUID-1").withFullName("Max Uber").withPunkte(3875);
			daten.add(kind);
		}

		{

			KinddatenUebersicht kind = new KinddatenUebersicht().withUuid("UUID-2").withFullName("Max Überlauf").withPunkte(3875);
			daten.add(kind);
		}

		{

			KinddatenUebersicht kind = new KinddatenUebersicht().withUuid("UUID-3").withFullName("Anne Susanne").withPunkte(2800);
			daten.add(kind);
		}

		{

			KinddatenUebersicht kind = new KinddatenUebersicht().withUuid("UUID-4").withFullName("Anne Susanne").withPunkte(4000);
			daten.add(kind);
		}

		// Act
		Collections.sort(daten, new KinddatenUebersichtComparator());

		// Assert
		assertEquals("UUID-4", daten.get(0).uuid());
		assertEquals("UUID-1", daten.get(1).uuid());
		assertEquals("UUID-2", daten.get(2).uuid());
		assertEquals("UUID-3", daten.get(3).uuid());

	}
}
