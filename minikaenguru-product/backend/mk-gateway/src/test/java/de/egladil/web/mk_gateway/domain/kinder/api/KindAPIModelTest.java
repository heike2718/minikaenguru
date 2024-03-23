// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.KlassenstufeAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.SpracheAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * KindAPIModelTest
 */
public class KindAPIModelTest {

	@Test
	void should_createWithAttributes_initTheAttributes() {

		// Arrange
		Klassenstufe klassenstufe = Klassenstufe.ZWEI;
		Sprache sprache = Sprache.en;
		String expectedVorname = "Klaus";
		String expectedNachname = "Klabauter";
		String expectedZusatz = "zusatz";
		String expectedLoesungszettelId = "GGGOHOHHP6";
		String expectedUuid = "JLHDRSRZDU";

		KlassenstufeAPIModel expectedKlassenstufe = KlassenstufeAPIModel.create(klassenstufe);
		SpracheAPIModel expectedSprache = SpracheAPIModel.create(sprache);

		LoesungszettelpunkteAPIModel punkte = new LoesungszettelpunkteAPIModel().withPunkte("48,5").withLaengeKaengurusprung(4)
			.withLoesungszettelId("GGGOHOHHP6");

		// Act
		KindAPIModel apiModel = KindAPIModel.create(klassenstufe, sprache).withPunkte(punkte)
			.withNachname("Klabauter").withVorname("Klaus").withZusatz("zusatz").withUuid("JLHDRSRZDU");

		// Assert
		assertEquals(expectedKlassenstufe, apiModel.klassenstufe());
		assertEquals(expectedSprache, apiModel.sprache());
		assertEquals(expectedUuid, apiModel.uuid());
		assertEquals(expectedLoesungszettelId, apiModel.punkte().loesungszettelId());
		assertEquals(expectedNachname, apiModel.nachname());
		assertEquals(expectedVorname, apiModel.vorname());
		assertEquals(expectedZusatz, apiModel.zusatz());
	}

	@Test
	void should_createFromKind_initTheAttributes() {

		// Arrange
		Klassenstufe klassenstufe = Klassenstufe.IKID;
		Sprache sprache = Sprache.de;
		String expectedVorname = "Rudi";
		String expectedNachname = "Rettich";
		String expectedZusatz = "zusatz";
		String expectedLoesungszettelId = "LOES-ID";
		String expectedUuid = "TFGRTH7";

		LoesungszettelpunkteAPIModel punkte = new LoesungszettelpunkteAPIModel().withLoesungszettelId("LOES-ID")
			.withLaengeKaengurusprung(3).withPunkte("28,5");
		Optional<LoesungszettelpunkteAPIModel> optPunkte = Optional.of(punkte);

		KlassenstufeAPIModel expectedKlassenstufe = KlassenstufeAPIModel.create(klassenstufe);
		SpracheAPIModel expectedSprache = SpracheAPIModel.create(sprache);

		Kind kind = new Kind(new Identifier("TFGRTH7"))
			.withVorname("Rudi")
			.withNachname("Rettich")
			.withKlasseID(new Identifier("TFGRTH7"))
			.withKlassenstufe(Klassenstufe.IKID)
			.withLoesungszettelID(new Identifier("LOES-ID"))
			.withSprache(Sprache.de)
			.withZusatz("zusatz");

		// Act
		KindAPIModel apiModel = KindAPIModel.createFromKind(kind, optPunkte);

		// Assert
		assertNotNull(apiModel.punkte());

		assertEquals(expectedKlassenstufe, apiModel.klassenstufe());
		assertEquals(expectedSprache, apiModel.sprache());
		assertEquals(expectedUuid, apiModel.uuid());
		assertEquals(expectedLoesungszettelId, apiModel.punkte().loesungszettelId());
		assertEquals(3, apiModel.punkte().laengeKaengurusprung());
		assertEquals("28,5", apiModel.punkte().punkte());
		assertEquals(expectedNachname, apiModel.nachname());
		assertEquals(expectedVorname, apiModel.vorname());
		assertEquals(expectedZusatz, apiModel.zusatz());

	}

}
