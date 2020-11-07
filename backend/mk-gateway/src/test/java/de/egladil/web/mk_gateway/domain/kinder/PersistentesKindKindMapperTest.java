// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistentesKind;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistentesKindKindMapper;

/**
 * PersistentesKindKindMapperTest
 */
public class PersistentesKindKindMapperTest {

	@Test
	void should_mapAllAttributes() {

		// Arrange
		PersistentesKind persistentesKind = new PersistentesKind();
		persistentesKind.setKlassenstufe(Klassenstufe.EINS);
		persistentesKind.setKlasseUUID("ffd1859f-cbd5-41bd-b168-7cb4864da38e");
		persistentesKind.setLandkuerzel("DE-ST");
		persistentesKind.setLoesungszettelUUID("fff1a487-4d55-4ef4-85ce-58c67642bba9");
		persistentesKind.setNachname("Walter");
		persistentesKind.setSprache(Sprache.en);
		persistentesKind.setTeilnahmeart(Teilnahmeart.SCHULE);
		persistentesKind.setTeilnahmenummer("G1HDI46O");
		persistentesKind.setUuid("6ee4e726-b7a2-4554-9f15-74b07d835e16");
		persistentesKind.setVorname("Fiona");
		persistentesKind.setZusatz("blond");

		// Act
		Kind kind = new PersistentesKindKindMapper().apply(persistentesKind);

		// Assert
		assertEquals(Klassenstufe.EINS, kind.klassenstufe());
		assertEquals(new Identifier("ffd1859f-cbd5-41bd-b168-7cb4864da38e"), kind.klasseID());
		assertEquals("DE-ST", kind.landkuerzel());
		assertEquals(new Identifier("fff1a487-4d55-4ef4-85ce-58c67642bba9"), kind.loesungszettelID());
		assertEquals("Walter", kind.nachname());
		assertEquals(Sprache.en, kind.sprache());
		assertEquals(TeilnahmeIdentifierAktuellerWettbewerb.createForSchulteilnahme("G1HDI46O"), kind.teilnahmeIdentifier());
		assertEquals(new Identifier("6ee4e726-b7a2-4554-9f15-74b07d835e16"), kind.identifier());
		assertEquals("Fiona", kind.vorname());
		assertEquals("blond", kind.zusatz());
	}

}
