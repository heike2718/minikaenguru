// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;
import de.egladil.web.mk_gateway.domain.veranstalter.admin.VeranstalterSuchkriterium;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterSuchanfrage;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VeranstalterHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * VeranstalterHibernateRepositoryIT
 */
public class VeranstalterHibernateRepositoryIT extends AbstractIntegrationTest {

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
		assertEquals(6, result.size());
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
	void should_findByUuid_work_and_readTheZugangUnterlagenStatus() {

		// Arrange
		VeranstalterSuchanfrage suchanfrage = new VeranstalterSuchanfrage(VeranstalterSuchkriterium.UUID, "feae6094");

		// Act
		List<Veranstalter> result = veranstalterRepository.findVeranstalter(suchanfrage);

		// Assert
		assertEquals(1, result.size());
		Veranstalter veranstalter = result.get(0);
		Person person = veranstalter.person();
		assertNotNull(person.email());
		assertEquals(ZugangUnterlagen.ENTZOGEN, veranstalter.zugangUnterlagen());

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
	void should_loadVeranstalterByUuids_work() {

		// Arrange
		List<String> uuids = Arrays
			.asList(new String[] { "a6bf38f2-5450-4720-9688-9c239a2e87c8", "eea92cc4-65b9-48b2-b30c-8f7b0b72c8de",
				"412b67dc-132f-465a-a3c3-468269e866cb" });

		// Act
		List<Veranstalter> result = veranstalterRepository.loadVeranstalterByUuids(uuids);

		// Assert
		assertEquals(3, result.size());
	}

	@Test
	void should_findEmailsNewsletterAbonnentenWork_when_Alle() {

		// Act
		List<String> mailadressen = veranstalterRepository.findEmailsNewsletterAbonnenten(Empfaengertyp.ALLE);

		// Assert
		assertEquals(124, mailadressen.size());

	}

	@Test
	void should_findEmailsNewsletterAbonnentenWork_when_Lehrer() {

		// Act
		List<String> mailadressen = veranstalterRepository.findEmailsNewsletterAbonnenten(Empfaengertyp.LEHRER);

		// Assert
		assertEquals(79, mailadressen.size());

	}

	@Test
	void should_findEmailsNewsletterAbonnentenWork_when_Privatveranstalter() {

		// Act
		List<String> mailadressen = veranstalterRepository.findEmailsNewsletterAbonnenten(Empfaengertyp.PRIVATVERANSTALTER);

		// Assert
		assertEquals(45, mailadressen.size());

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
