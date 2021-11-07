// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistentesKind;

/**
 * KindPersistentesKindAttributeMapperTest
 */
public class KindPersistentesKindAttributeMapperTest {

	@Test
	void should_mapCopyAllAttributesButUUID() {

		// Arrange
		Kind source = new Kind().withDublettePruefen(true).withIdentifier(new Identifier("hallo"))
			.withImportiert(true).withKlasseID(new Identifier("KLASSE-ID"))
			.withKlassenstufe(Klassenstufe.ZWEI)
			.withKlassenstufePruefen(true).withLandkuerzel("DE-TH").withLoesungszettelID(new Identifier("LZ-ID"))
			.withNachname("Verwalter")
			.withSprache(Sprache.en)
			.withTeilnahmeIdentifier(TeilnahmeIdentifierAktuellerWettbewerb.createForSchulteilnahme("SCHULE"))
			.withVorname("Alter")
			.withZusatz("Fensterplatz");

		PersistentesKind target = new PersistentesKind();
		target.setUuid("holla");

		// Act
		new KindPersistentesKindAttributeMapper().copyAttributesFromKindWithoutUuid(source, target);

		// Assert
		assertEquals("holla", target.getUuid());
		assertEquals(Klassenstufe.ZWEI, target.getKlassenstufe());
		assertEquals("KLASSE-ID", target.getKlasseUUID());
		assertEquals("DE-TH", target.getLandkuerzel());
		assertEquals("LZ-ID", target.getLoesungszettelUUID());
		assertEquals("Verwalter", target.getNachname());
		assertEquals(Sprache.en, target.getSprache());
		assertEquals(Teilnahmeart.SCHULE, target.getTeilnahmeart());
		assertEquals("SCHULE", target.getTeilnahmenummer());
		assertEquals("Alter", target.getVorname());
		assertEquals("Fensterplatz", target.getZusatz());
		assertTrue(target.isDublettePruefen());
		assertTrue(target.isImportiert());
		assertTrue(target.isKlassenstufePruefen());

	}

}
