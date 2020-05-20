// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_wettbewerb.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_wettbewerb.domain.Identifier;

/**
 * AddLehrerServiceTest
 */
public class AddLehrerServiceTest extends AbstractDomainServiceTest {

	private AddLehrerService service;

	@BeforeEach
	@Override
	protected void setUp() {

		super.setUp();
		service = AddLehrerService.createServiceForTest(getVeranstalterRepository());

	}

	@Test
	void should_DoNothing_when_LehrerExists() {

		// Arrange
		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(UUID_LEHRER, "Irgendein Name",
			SCHULKUERZEL);
		int anzahlVorher = getCountLehrerAdded();

		// Act
		service.addLehrer(command);

		// Assert
		int anzahlNachher = getCountLehrerAdded();
		assertEquals(anzahlVorher, anzahlNachher);

	}

	@Test
	void should_AddLehrerAndCreateEvent_when_NeuerLehrer() {

		// Arrange
		final String uuid = "hklhasshdiha";
		final String fullName = "Professor Proton";
		final String schulkuerzel = "GHKFFDFF";

		final Identifier identifier = new Identifier(uuid);

		Optional<Veranstalter> optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertFalse(optVeranstalter.isPresent());

		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(uuid, fullName,
			schulkuerzel);
		int expectedAnzahl = getCountLehrerAdded() + 1;

		// Act
		service.addLehrer(command);

		// Assert
		int anzahlNachher = getCountLehrerAdded();
		assertEquals(expectedAnzahl, anzahlNachher);

		optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertTrue(optVeranstalter.isPresent());

		Veranstalter veranstalter = optVeranstalter.get();

		assertEquals(Rolle.LEHRER, veranstalter.rolle());
		assertEquals(uuid, veranstalter.uuid());
		assertEquals(fullName, veranstalter.fullName());

		Lehrer lehrer = (Lehrer) veranstalter;
		assertEquals(schulkuerzel, lehrer.persistierbareTeilnahmenummern());

		LehrerRegisteredForSchule event = service.event();
		assertNotNull(event);

		assertEquals(lehrer.person(), event.person());
		assertEquals(schulkuerzel, event.schulkuerzel());
		assertEquals("LehrerRegisteredForSchule", event.typeName());
	}

}
