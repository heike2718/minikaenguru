// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.auswertungen.StatistikTestUtils;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.statistik.AufgabeErgebnisItem;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * UebersichtAufgabenErgebnisseRechnerTest
 */
public class UebersichtAufgabenErgebnisseRechnerTest {

	private List<Loesungszettel> wettbewerbLoesungszettel;

	private UebersichtAufgabenErgebnisseRechner rechner;

	@BeforeEach
	void setUp() throws Exception {

		wettbewerbLoesungszettel = StatistikTestUtils.loadTheLoesungszettel();
		rechner = new UebersichtAufgabenErgebnisseRechner();

	}

	@Test
	void should_berechneAufgabeErgebnisItemsWork_when_KlassenstufeIKID() {

		// Arrange
		WettbewerbID wettbewerbID = new WettbewerbID(2018);
		Klassenstufe klassenstufe = Klassenstufe.IKID;
		List<Loesungszettel> loesungszettelKlassenstufe = wettbewerbLoesungszettel.stream()
			.filter(l -> l.klassenstufe() == klassenstufe).collect(Collectors.toList());

		// Act
		List<AufgabeErgebnisItem> items = rechner.berechneAufgabeErgebnisItems(wettbewerbID, klassenstufe,
			loesungszettelKlassenstufe);

		// Assert
		assertEquals(6, items.size());
	}

	@Test
	void should_berechneAufgabeErgebnisItemsWork_when_KlassenstufeEINS() {

		// Arrange
		WettbewerbID wettbewerbID = new WettbewerbID(2018);
		Klassenstufe klassenstufe = Klassenstufe.EINS;
		List<Loesungszettel> loesungszettelKlassenstufe = wettbewerbLoesungszettel.stream()
			.filter(l -> l.klassenstufe() == klassenstufe).collect(Collectors.toList());

		// Act
		List<AufgabeErgebnisItem> items = rechner.berechneAufgabeErgebnisItems(wettbewerbID, klassenstufe,
			loesungszettelKlassenstufe);

		// Assert
		assertEquals(12, items.size());
	}

	@Test
	void should_berechneAufgabeErgebnisItemsWork_when_KlassenstufeZWEI() {

		// Arrange
		WettbewerbID wettbewerbID = new WettbewerbID(2018);
		Klassenstufe klassenstufe = Klassenstufe.ZWEI;
		List<Loesungszettel> loesungszettelKlassenstufe = wettbewerbLoesungszettel.stream()
			.filter(l -> l.klassenstufe() == klassenstufe).collect(Collectors.toList());

		// Act
		List<AufgabeErgebnisItem> items = rechner.berechneAufgabeErgebnisItems(wettbewerbID, klassenstufe,
			loesungszettelKlassenstufe);

		// Assert
		assertEquals(15, items.size());

		for (AufgabeErgebnisItem item : items) {

			assertNotNull(item.anteilFalschText());
			assertNotNull(item.anteilNichtGeloestText());
			assertNotNull(item.anteilRichtigText());
			assertNotNull(item.getNummer());
		}
	}

}
