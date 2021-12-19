// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenteTeilnahme;

/**
 * TeilnahmenHibernateRepositoryTest
 */
public class TeilnahmenHibernateRepositoryTest {

	@Test
	void should_mapToSchulteilnahme_work() {

		// Arrange
		TeilnahmenHibernateRepository repo = new TeilnahmenHibernateRepository();

		String lehrerUUID = "bsjkhah";
		String schulkuerzel = "asjhipfpi";
		String schulname = "HGggdgqu";

		PersistenteTeilnahme persistente = new PersistenteTeilnahme();
		persistente.setAngemeldetDurch(lehrerUUID);
		persistente.setSchulname(schulname);
		persistente.setTeilnahmeart(Teilnahmeart.SCHULE);
		persistente.setTeilnahmenummer(schulkuerzel);
		persistente.setUuid("ufgwuefuo");
		persistente.setWettbewerbUUID("2020");

		// Act
		Teilnahme t = repo.mapToTeilnahme(persistente);

		// Assert
		assertTrue(t instanceof Schulteilnahme);
		Schulteilnahme schulteilnahme = (Schulteilnahme) t;

		assertEquals(schulkuerzel, schulteilnahme.teilnahmenummer().identifier());
		assertEquals(lehrerUUID, schulteilnahme.angemeldetDurchVeranstalterId().identifier());
		assertEquals(schulname, schulteilnahme.nameSchule());
		assertEquals(Integer.valueOf(2020), schulteilnahme.wettbewerbID().jahr());

	}

	@Test
	void should_mapToPrivatteilnahme_work() {

		// Arrange
		TeilnahmenHibernateRepository repo = new TeilnahmenHibernateRepository();

		String teilnahmekuerzel = "asjhipfpi";

		PersistenteTeilnahme persistente = new PersistenteTeilnahme();
		persistente.setTeilnahmeart(Teilnahmeart.PRIVAT);
		persistente.setTeilnahmenummer(teilnahmekuerzel);
		persistente.setUuid("ufgwuefuo");
		persistente.setWettbewerbUUID("2020");

		// Act
		Teilnahme t = repo.mapToTeilnahme(persistente);

		// Assert
		assertTrue(t instanceof Privatteilnahme);
		Privatteilnahme privatteilnahme = (Privatteilnahme) t;

		assertEquals(teilnahmekuerzel, privatteilnahme.teilnahmenummer().identifier());
		assertEquals(Integer.valueOf(2020), privatteilnahme.wettbewerbID().jahr());

	}

	@Test
	void should_mapFromTeilnahmeWork_when_Schulteilnahme() {

		// Arrange
		WettbewerbID wettbewerb = new WettbewerbID(2020);

		Identifier schuleId = new Identifier("gasdguq");
		Identifier lehrerId = new Identifier("sgahpsahj");

		Schulteilnahme teilnahme = new Schulteilnahme(wettbewerb, schuleId, "Christaschule", lehrerId);

		// Act
		PersistenteTeilnahme persistente = new TeilnahmenHibernateRepository().mapFromTeilnahme(teilnahme);

		// Assert
		assertEquals(Teilnahmeart.SCHULE, persistente.getTeilnahmeart());
		assertEquals("gasdguq", persistente.getTeilnahmenummer());
		assertEquals("Christaschule", persistente.getSchulname());
		assertEquals("sgahpsahj", persistente.getAngemeldetDurch());
		assertEquals("2020", persistente.getWettbewerbUUID());
		assertNull(persistente.getUuid());
	}

	@Test
	void should_mapFromTeilnahmeWork_when_Privatteilnahme() {

		// Arrange
		WettbewerbID wettbewerb = new WettbewerbID(2020);

		Identifier teilnahmeId = new Identifier("gasdguq");

		Privatteilnahme teilnahme1 = new Privatteilnahme(wettbewerb, teilnahmeId);

		// Act
		PersistenteTeilnahme persistente = new TeilnahmenHibernateRepository().mapFromTeilnahme(teilnahme1);

		// Assert
		assertEquals(Teilnahmeart.PRIVAT, persistente.getTeilnahmeart());
		assertEquals("gasdguq", persistente.getTeilnahmenummer());
		assertNull(persistente.getSchulname());
		assertNull(persistente.getAngemeldetDurch());
		assertEquals("2020", persistente.getWettbewerbUUID());
		assertNull(persistente.getUuid());
	}

}
