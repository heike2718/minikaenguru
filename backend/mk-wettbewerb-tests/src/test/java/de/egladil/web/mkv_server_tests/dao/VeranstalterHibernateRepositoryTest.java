// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.admin.VeranstalterSuchkriterium;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterSuchanfrage;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VeranstalterHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * VeranstalterHibernateRepositoryTest
 */
public class VeranstalterHibernateRepositoryTest extends AbstractIT {

	VeranstalterHibernateRepository veranstalterRepository;

	@BeforeEach
	protected void setUp() {

		super.setUp();
		veranstalterRepository = VeranstalterHibernateRepository.createForIntegrationTest(entityManager);

	}

	@Test
	void should_findByEmail_work() {

		// Arrange
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage(VeranstalterSuchkriterium.EMAIL, "tl");

		// Act
		List<Veranstalter> result = veranstalterRepository.findVeranstalter(suchanfrage);

		// Assert
		assertEquals(5, result.size());
	}

	@Test
	void should_findByName_work() {

		// Arrange
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage(VeranstalterSuchkriterium.NAME, "beutlin");

		// Act
		List<Veranstalter> result = veranstalterRepository.findVeranstalter(suchanfrage);

		// Assert
		assertEquals(2, result.size());
	}

	@Test
	void should_findByUuid_work() {

		// Arrange
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage(VeranstalterSuchkriterium.UUID, "5d89");

		// Act
		List<Veranstalter> result = veranstalterRepository.findVeranstalter(suchanfrage);

		// Assert
		assertEquals(1, result.size());
		Person person = result.get(0).person();
		assertNotNull(person.email());

	}

	@Test
	void should_findByUuid_beResilient_whenTeilnahmenummerNull() {

		// Arrange
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage(VeranstalterSuchkriterium.UUID, "c84e5769");

		// Act
		List<Veranstalter> result = veranstalterRepository.findVeranstalter(suchanfrage);

		// Assert
		assertEquals(1, result.size());
		Veranstalter veranstalter = result.get(0);
		assertEquals(0, veranstalter.teilnahmeIdentifier().size());
		assertNull(veranstalter.persistierbareTeilnahmenummern());
	}

	@Test
	void should_findByTeilnahmenummer_work() {

		// Arrange
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage(VeranstalterSuchkriterium.TEILNAHMENUMMER, "EEGEECP6");

		// Act
		List<Veranstalter> result = veranstalterRepository.findVeranstalter(suchanfrage);

		// Assert
		assertEquals(3, result.size());
	}

	@Test
	void should_findEmailsNewsletterAbonnentenWork_when_Alle() {

		// Act
		List<String> mailadressen = veranstalterRepository.findEmailsNewsletterAbonnenten(Empfaengertyp.ALLE);

		// Assert
		assertEquals(120, mailadressen.size());

	}

	@Test
	void should_findEmailsNewsletterAbonnentenWork_when_Lehrer() {

		// Act
		List<String> mailadressen = veranstalterRepository.findEmailsNewsletterAbonnenten(Empfaengertyp.LEHRER);

		// Assert
		assertEquals(77, mailadressen.size());

	}

	@Test
	void should_findEmailsNewsletterAbonnentenWork_when_Privatveranstalter() {

		// Act
		List<String> mailadressen = veranstalterRepository.findEmailsNewsletterAbonnenten(Empfaengertyp.PRIVATVERANSTALTER);

		// Assert
		assertEquals(43, mailadressen.size());

	}

	@Test
	void should_findEmailsNewsletterAbonnentenWork_when_Test() {

		// Act
		List<String> mailadressen = veranstalterRepository.findEmailsNewsletterAbonnenten(Empfaengertyp.TEST);

		// Assert
		assertEquals(2, mailadressen.size());
		assertTrue(mailadressen.contains("hdwinkel@egladil.de"));
		assertTrue(mailadressen.contains("info@egladil.de"));
	}

}
