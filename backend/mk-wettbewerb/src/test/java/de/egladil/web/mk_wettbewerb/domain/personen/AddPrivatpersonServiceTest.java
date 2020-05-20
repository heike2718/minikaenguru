// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_wettbewerb.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_wettbewerb.domain.Identifier;

/**
 * AddPrivatpersonServiceTest
 */
public class AddPrivatpersonServiceTest extends AbstractDomainServiceTest {

	private AddPrivatpersonService service;

	@BeforeEach
	@Override
	protected void setUp() {

		super.setUp();
		this.service = AddPrivatpersonService.createForTest(getVeranstalterRepository());
	}

	@Test
	void should_NotAddPrivatperson_when_PrivatpersonExists() {

		// Arrange
		CreateOrUpdatePrivatpersonCommand command = CreateOrUpdatePrivatpersonCommand.create(UUID_PRIVAT, "Herta Grütze");
		int anzahlVorher = getCountPrivatpersonAdded();

		// Act
		service.addPrivatperson(command);

		// Assert
		int anzahlNachher = getCountPrivatpersonAdded();
		assertEquals(anzahlVorher, anzahlNachher);

	}

	@Test
	void should_AddPrivatperson_when_PrivatpersonUnknown() {

		// Arrange
		final String uuid = "dd97e1bf-f52a-4429-9443-0de9d96dac37";
		final String fullName = "Herta Kirsch-Grüzte";
		CreateOrUpdatePrivatpersonCommand command = CreateOrUpdatePrivatpersonCommand.create(uuid,
			fullName);
		int anzahlExcpected = getCountPrivatpersonAdded() + 1;

		Identifier identifier = new Identifier(uuid);
		Optional<Veranstalter> optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertFalse(optVeranstalter.isPresent());

		// Act
		service.addPrivatperson(command);

		// Assert
		int anzahlNachher = getCountPrivatpersonAdded();
		assertEquals(anzahlExcpected, anzahlNachher);

		optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertTrue(optVeranstalter.isPresent());
		Veranstalter veranstalter = optVeranstalter.get();

		assertEquals(Rolle.PRIVAT, veranstalter.rolle());
		assertEquals(uuid, veranstalter.uuid());
		assertEquals(fullName, veranstalter.fullName());
		assertEquals(1, veranstalter.teilnahmeIdentifier().size());

		Identifier teilnahmenummer = veranstalter.teilnahmeIdentifier().get(0);
		assertEquals("9D96DAC37", teilnahmenummer.identifier());

	}

}
