// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_wettbewerb.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.apimodel.PrivatveranstalterAPIModel;
import de.egladil.web.mk_wettbewerb.domain.error.MkWettbewerbRuntimeException;

/**
 * PrivatpersonServiceTest
 */
public class PrivatpersonServiceTest extends AbstractDomainServiceTest {

	private PrivatpersonService service;

	@BeforeEach
	@Override
	protected void setUp() {

		super.setUp();
		this.service = PrivatpersonService.createForTest(getVeranstalterRepository(), getZugangUnterlagenService(),
			getWettbewerbService(), getTeilnahmenRepository());
	}

	@Test
	void should_NotAddPrivatperson_when_PrivatpersonExists() {

		// Arrange
		CreateOrUpdatePrivatpersonCommand command = CreateOrUpdatePrivatpersonCommand.create(UUID_PRIVAT, "Herta Grütze");
		int anzahlVorher = getVeranstalterRepository().getCountPrivatpersonAdded();

		// Act
		service.addPrivatperson(command);

		// Assert
		int anzahlNachher = getVeranstalterRepository().getCountPrivatpersonAdded();
		assertEquals(anzahlVorher, anzahlNachher);

	}

	@Test
	void should_AddPrivatperson_when_PrivatpersonUnknown() {

		// Arrange
		final String uuid = "dd97e1bf-f52a-4429-9443-0de9d96dac37";
		final String fullName = "Herta Kirsch-Grüzte";
		CreateOrUpdatePrivatpersonCommand command = CreateOrUpdatePrivatpersonCommand.create(uuid,
			fullName);

		Identifier identifier = new Identifier(uuid);
		Optional<Veranstalter> optVeranstalter = getVeranstalterRepository().ofId(identifier);
		assertFalse(optVeranstalter.isPresent());

		// Act
		service.addPrivatperson(command);

		// Assert
		int anzahlNachher = getVeranstalterRepository().getCountPrivatpersonAdded();
		assertEquals(1, anzahlNachher);

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

	@Test
	void shouldFindPrivatpersonThrowException_when_UuidNull() {

		try {

			service.findPrivatperson(null);
			fail("keine BadRequestException");
		} catch (BadRequestException e) {

			assertEquals("uuid darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void shouldFindPrivatpersonThrowException_when_UuidBlank() {

		try {

			service.findPrivatperson(" ");
			fail("keine BadRequestException");
		} catch (BadRequestException e) {

			assertEquals("uuid darf nicht blank sein.", e.getMessage());
		}
	}

	@Test
	void shouldFindPrivatpersonThrowException_when_VeranstalterUnknown() {

		// Arrange
		String uuid = "3523528285";

		try {

			service.findPrivatperson(uuid);
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			assertEquals("Kennen keinen Veranstalter mit dieser ID", e.getMessage());
		}
	}

	@Test
	void shouldFindPrivatpersonThrowException_when_KeinPrivatveranstalter() {

		// Arrange
		String uuid = UUID_LEHRER_1;

		try {

			service.findPrivatperson(uuid);

			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			assertEquals("Kennen keinen Privatveranstalter mit dieser ID", e.getMessage());
			assertNotNull(service.getSecurityIncidentRegistered());
		}
	}

	@Test
	void shouldFindPrivatperson_when_Present() {

		// Arrange
		String uuid = UUID_PRIVAT;

		// Act
		PrivatveranstalterAPIModel veranstalter = service.findPrivatperson(uuid);

		// Assert
		assertFalse(veranstalter.hatZugangZuUnterlangen());
		assertTrue(veranstalter.aktuellAngemeldet());
		assertEquals(0, veranstalter.anzahlVergangeneTeilnahmen());
		assertEquals(0, veranstalter.vergangeneTeilnahmen().size());
		assertNotNull(veranstalter.aktuelleTeilnahme());
	}

	@Test
	void shouldFindPrivatpersonThrowException_when_VeranstalterMitMehrerenTeilnahmekuerzeln() {

		try {

			service.findPrivatperson(UUID_PRIVAT_MEHRERE_TEILNAHMEKURZEL);
			fail("keine MkWettbewerbRuntimeException");
		} catch (MkWettbewerbRuntimeException e) {

			assertEquals("Kann aktuelle Teilnahme nicht ermitteln", e.getMessage());
			assertNotNull(service.getDataInconsistencyRegistered());
		}
	}

	@Test
	void shouldFindPrivatpersonThrowException_when_VeranstalterOhneTeilnahmekuerzel() {

		try {

			service.findPrivatperson(UUID_PRIVAT_KEIN_TEILNAHMEKURZEL);
			fail("keine MkWettbewerbRuntimeException");
		} catch (MkWettbewerbRuntimeException e) {

			assertEquals("Kann aktuelle Teilnahme nicht ermitteln", e.getMessage());
			assertNotNull(service.getDataInconsistencyRegistered());
		}
	}

}
