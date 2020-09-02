// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * LehrerServiceTest
 */
public class LehrerServiceTest extends AbstractDomainServiceTest {

	private LehrerService service;

	@BeforeEach
	@Override
	protected void setUp() {

		super.setUp();
		service = LehrerService.createServiceForTest(getVeranstalterRepository());

	}

	@Test
	void should_doNothing_when_LehrerExists() throws Exception {

		// Arrange
		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(UUID_LEHRER_1, "Irgendein Name",
			SCHULKUERZEL_1);

		// Act
		service.addLehrer(command);

		// Assert
		int anzahlNachher = getVeranstalterRepository().getCountLehrerAdded();
		assertEquals(0, anzahlNachher);

		LehrerChanged event = service.event();
		assertNull(event);
	}

	@Test
	void should_AddLehrerAndCreateEvent_when_NeuerLehrer() throws Exception {

		// Arrange
		final String uuid = "hklhasshdiha";
		final String fullName = "Professor Proton";
		final String schulkuerzel = "GHKFFDFF";

		final Identifier identifier = new Identifier(uuid);

		Optional<Veranstalter> optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertFalse(optVeranstalter.isPresent());

		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.createForTest(uuid, fullName,
			schulkuerzel);

		// Act
		service.addLehrer(command);

		// Assert
		int anzahlNachher = getVeranstalterRepository().getCountLehrerAdded();
		assertEquals(1, anzahlNachher);

		optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertTrue(optVeranstalter.isPresent());

		Veranstalter veranstalter = optVeranstalter.get();

		assertEquals(Rolle.LEHRER, veranstalter.rolle());
		assertEquals(uuid, veranstalter.uuid());
		assertEquals(fullName, veranstalter.fullName());

		Lehrer lehrer = (Lehrer) veranstalter;
		assertEquals(schulkuerzel, lehrer.persistierbareTeilnahmenummern());

		LehrerChanged event = service.event();
		assertNotNull(event);

		assertEquals(lehrer.person(), event.person());
		assertEquals("", event.alteSchulkuerzel());
		assertEquals(schulkuerzel, event.neueSchulkuerzel());
		assertEquals("LehrerChanged", event.typeName());

		System.out.println("event: " + new ObjectMapper().writeValueAsString(event));
		System.out.println("command: " + new ObjectMapper().writeValueAsString(command));
	}

}
