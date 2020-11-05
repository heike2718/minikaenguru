// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.klassen.Klasse;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistentesKind;

/**
 * TestdatenGenerator
 */
public class TestdatenGenerator {

	public static void main(final String[] args) {

		try {

			new TestdatenGenerator().generateTestdaten();
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
	}

	private void generateTestdaten() throws JsonProcessingException {

		List<Klasse> klassen = new ArrayList<>();
		final String schulkuerzel = "SCHULKUERZEL_1";

		{

			Klasse klasse = new Klasse(new Identifier("SCHULKUERZEL_1_KLASSE_2A"))
				.withName("2a")
				.withSchuleID(new Identifier(schulkuerzel));

			klassen.add(klasse);
		}

		{

			Klasse klasse = new Klasse(new Identifier("SCHULKUERZEL_1_KLASSE_2B"))
				.withName("2b")
				.withSchuleID(new Identifier(schulkuerzel));

			klassen.add(klasse);
		}

		List<Kind> kinder = new ArrayList<>();
		Klassenstufe klassenstufe = Klassenstufe.ZWEI;
		Sprache sprache = Sprache.de;
		Teilnahmeart teilnahmeart = Teilnahmeart.SCHULE;
		WettbewerbID wettbewerbID = new WettbewerbID(2020);

		{

			PersistentesKind kind = new PersistentesKind();
			kind.setUuid("SCHULKUERZEL_1_KIND_1");
			kind.setKlassenstufe(klassenstufe);
			kind.setKlasseUUID("SCHULKUERZEL_1_KLASSE_2A");
			kind.setLandkuerzel("DE-HH");
			kind.setLoesungszettelUUID("4054ed58-9074-43cb-bc76-c9eeeccc0e60");
			kind.setNachname("Rutsche");
			kind.setVorname("Rudi");
			kind.setSprache(sprache);
			kind.setTeilnahmeart(teilnahmeart);
			kind.setTeilnahmenummer(schulkuerzel);

			kinder.add(mapFromDB(kind,
				new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart).withTeilnahmenummer("SCHULKUERZEL_1_KLASSE_2A")
					.withWettbewerbID(wettbewerbID)));
		}

		{

			PersistentesKind kind = new PersistentesKind();
			kind.setUuid("SCHULKUERZEL_1_KIND_2");
			kind.setKlassenstufe(klassenstufe);
			kind.setKlasseUUID("SCHULKUERZEL_1_KLASSE_2A");
			kind.setLandkuerzel("DE-HH");
			kind.setLoesungszettelUUID("2e2bb45f-d333-468b-ac93-5715851d5843");
			kind.setNachname("Mayer");
			kind.setVorname("Marie");
			kind.setSprache(sprache);
			kind.setTeilnahmeart(teilnahmeart);
			kind.setTeilnahmenummer(schulkuerzel);

			kinder.add(mapFromDB(kind,
				new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart).withTeilnahmenummer("SCHULKUERZEL_1_KLASSE_2A")
					.withWettbewerbID(wettbewerbID)));
		}

		{

			PersistentesKind kind = new PersistentesKind();
			kind.setUuid("SCHULKUERZEL_1_KIND_3");
			kind.setKlassenstufe(klassenstufe);
			kind.setKlasseUUID("SCHULKUERZEL_1_KLASSE_2B");
			kind.setLandkuerzel("DE-HH");
			kind.setLoesungszettelUUID("472fba58-939a-4166-afd6-09cc7b619e2d");
			kind.setNachname("Mayer");
			kind.setVorname("Marie");
			kind.setSprache(sprache);
			kind.setTeilnahmeart(teilnahmeart);
			kind.setTeilnahmenummer(schulkuerzel);

			kinder.add(mapFromDB(kind,
				new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart).withTeilnahmenummer("SCHULKUERZEL_1_KLASSE_2B")
					.withWettbewerbID(wettbewerbID)));
		}

		{

			PersistentesKind kind = new PersistentesKind();
			kind.setUuid("SCHULKUERZEL_1_KIND_4");
			kind.setKlassenstufe(klassenstufe);
			kind.setKlasseUUID("SCHULKUERZEL_1_KLASSE_2B");
			kind.setLandkuerzel("DE-HH");
			kind.setLoesungszettelUUID("a969178b-a848-4ee4-83be-2c083e576c44");
			kind.setNachname("Walter");
			kind.setVorname("Sophie");
			kind.setSprache(sprache);
			kind.setTeilnahmeart(teilnahmeart);
			kind.setTeilnahmenummer(schulkuerzel);

			kinder.add(mapFromDB(kind,
				new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart).withTeilnahmenummer("SCHULKUERZEL_1_KLASSE_2B")
					.withWettbewerbID(wettbewerbID)));
		}

		{

			PersistentesKind kind = new PersistentesKind();
			kind.setUuid("TEILNAHMENUMMER_PRIVAT_KIND_1");
			kind.setKlassenstufe(klassenstufe);
			kind.setLoesungszettelUUID("bffa4611-69e0-48d5-b483-2c829d3e534d");
			kind.setNachname("Traubenzucker");
			kind.setVorname("Rainer");
			kind.setSprache(sprache);
			kind.setTeilnahmeart(Teilnahmeart.PRIVAT);
			kind.setTeilnahmenummer("TEILNAHMENUMMER_PRIVAT");

			kinder.add(mapFromDB(kind,
				new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.PRIVAT).withTeilnahmenummer("TEILNAHMENUMMER_PRIVAT")
					.withWettbewerbID(wettbewerbID)));
		}

		ObjectMapper om = new ObjectMapper();

		String klassenSerialized = om.writeValueAsString(klassen);
		String kinderSerialized = om.writeValueAsString(kinder);

		System.out.println(klassenSerialized);

		System.out.println(kinderSerialized);

	}

	Kind mapFromDB(final PersistentesKind persistentesKind, final TeilnahmeIdentifier teilnahmeIdentifier) {

		Kind result = new Kind(new Identifier(persistentesKind.getUuid()))
			.withKlassenstufe(persistentesKind.getKlassenstufe())
			.withLandkuerzel(persistentesKind.getLandkuerzel())
			.withNachname(persistentesKind.getNachname())
			.withSprache(persistentesKind.getSprache())
			.withTeilnahmeIdentifier(teilnahmeIdentifier)
			.withVorname(persistentesKind.getVorname())
			.withZusatz(persistentesKind.getZusatz());

		if (persistentesKind.getLoesungszettelUUID() != null) {

			result = result.withLoesungszettelID(new Identifier(persistentesKind.getLoesungszettelUUID()));
		}

		if (persistentesKind.getKlasseUUID() != null) {

			result = result.withKlasseID(new Identifier(persistentesKind.getKlasseUUID()));
		}

		return result;
	}

}
