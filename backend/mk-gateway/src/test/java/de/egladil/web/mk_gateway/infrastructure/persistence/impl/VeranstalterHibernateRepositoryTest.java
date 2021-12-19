// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.Lehrer;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Privatveranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterVeranstalter;

/**
 * VeranstalterHibernateRepositoryTest
 */
public class VeranstalterHibernateRepositoryTest {

	@Test
	void should_mapFromVeranstalter_work_forLehrer() {

		// Arrange

		String uuid = "HUugaugduo";
		String fullName = "Hans Wurst";
		List<Identifier> schulen = Arrays.asList(new Identifier[] { new Identifier("1"), new Identifier("2") });
		Lehrer lehrer = new Lehrer(new Person(uuid, fullName), true, schulen);

		// Act
		PersistenterVeranstalter persistenter = new VeranstalterHibernateRepository().mapFromVeranstalter(lehrer);

		// Assert
		assertEquals(fullName, persistenter.getFullName());
		assertEquals(uuid, persistenter.getImportierteUuid());
		assertEquals(Rolle.LEHRER, persistenter.getRolle());
		assertEquals("1,2", persistenter.getTeilnahmenummern());
		assertEquals(ZugangUnterlagen.DEFAULT, persistenter.getZugangsberechtigungUnterlagen());
		assertTrue(persistenter.isNewsletterEmpfaenger());

	}

	@Test
	void should_mapFromVeranstalter_work_forPrivatmensch() {

		// Arrange

		String uuid = "HUugaugduo";
		String fullName = "Hans Wurst";
		List<Identifier> teilnahmeIds = Arrays.asList(new Identifier[] { new Identifier("1"), new Identifier("2") });
		Privatveranstalter lehrer = new Privatveranstalter(new Person(uuid, fullName), true, teilnahmeIds);

		// Act
		PersistenterVeranstalter persistenter = new VeranstalterHibernateRepository().mapFromVeranstalter(lehrer);

		// Assert
		assertEquals(fullName, persistenter.getFullName());
		assertEquals(uuid, persistenter.getImportierteUuid());
		assertEquals(Rolle.PRIVAT, persistenter.getRolle());
		assertEquals("1,2", persistenter.getTeilnahmenummern());
		assertEquals(ZugangUnterlagen.DEFAULT, persistenter.getZugangsberechtigungUnterlagen());
		assertTrue(persistenter.isNewsletterEmpfaenger());

	}

	@Test
	void should_mergeFromLehrer_mapOnlyChangeableAttributes() {

		// Arrange
		String uuid = "UIUIUI";

		PersistenterVeranstalter vorhandener = new PersistenterVeranstalter();
		vorhandener.setFullName("Klaus");
		vorhandener.setRolle(Rolle.LEHRER);
		vorhandener.setTeilnahmenummern("1,2");
		vorhandener.setUuid(uuid);
		vorhandener.setZugangsberechtigungUnterlagen(ZugangUnterlagen.ERTEILT);

		assertFalse(vorhandener.isNewsletterEmpfaenger());

		List<Identifier> schulen = Arrays.asList(new Identifier[] { new Identifier("1"), new Identifier("3") });

		Lehrer lehrer = new Lehrer(new Person("OIOIOI", "Erna"), true, schulen);
		lehrer.verwehreZugangUnterlagen();

		// Act
		new VeranstalterHibernateRepository().mergeFromVeranstalter(vorhandener, lehrer);

		// Assert
		assertEquals(uuid, vorhandener.getUuid());
		assertNull(vorhandener.getImportierteUuid());
		assertEquals(Rolle.LEHRER, vorhandener.getRolle());
		assertEquals("1,3", vorhandener.getTeilnahmenummern());
		assertEquals("Erna", vorhandener.getFullName());
		assertEquals(ZugangUnterlagen.ENTZOGEN, vorhandener.getZugangsberechtigungUnterlagen());
		assertTrue(vorhandener.isNewsletterEmpfaenger());

	}

	@Test
	void should_mergeFromPrivatThrowException_when_vorhandenerIsLehrer() {

		// Arrange
		String uuid = "UIUIUI";

		PersistenterVeranstalter vorhandener = new PersistenterVeranstalter();
		vorhandener.setFullName("Klaus");
		vorhandener.setRolle(Rolle.LEHRER);
		vorhandener.setTeilnahmenummern("1,2");
		vorhandener.setUuid(uuid);
		vorhandener.setZugangsberechtigungUnterlagen(ZugangUnterlagen.ERTEILT);

		List<Identifier> schulen = Arrays.asList(new Identifier[] { new Identifier("1"), new Identifier("3") });

		Privatveranstalter lehrer = new Privatveranstalter(new Person("OIOIOI", "Erna"), true, schulen);
		lehrer.verwehreZugangUnterlagen();

		// Act
		try {

			new VeranstalterHibernateRepository().mergeFromVeranstalter(vorhandener, lehrer);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Die Rolle darf nicht geändert werden! (rolle.persistent=LEHRER, rolle.veranstalter=PRIVAT",
				e.getMessage());
		}

	}

}
