// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.KlassenstufeAPIModel;
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

		// Act
		KindAPIModel apiModel = KindAPIModel.create(klassenstufe, sprache).withLoesungszettelId("GGGOHOHHP6")
			.withNachname("Klabauter").withVorname("Klaus").withZusatz("zusatz").withUuid("JLHDRSRZDU");

		// Assert
		assertEquals(expectedKlassenstufe, apiModel.klassenstufe());
		assertEquals(expectedSprache, apiModel.sprache());
		assertEquals(expectedUuid, apiModel.uuid());
		assertEquals(expectedLoesungszettelId, apiModel.loesungszettelId());
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
		KindAPIModel apiModel = KindAPIModel.createFromKind(kind);

		// Assert
		assertEquals(expectedKlassenstufe, apiModel.klassenstufe());
		assertEquals(expectedSprache, apiModel.sprache());
		assertEquals(expectedUuid, apiModel.uuid());
		assertEquals(expectedLoesungszettelId, apiModel.loesungszettelId());
		assertEquals(expectedNachname, apiModel.nachname());
		assertEquals(expectedVorname, apiModel.vorname());
		assertEquals(expectedZusatz, apiModel.zusatz());

	}

}