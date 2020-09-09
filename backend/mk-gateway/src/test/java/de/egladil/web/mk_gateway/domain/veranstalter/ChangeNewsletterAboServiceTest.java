// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.ws.rs.BadRequestException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * ChangeNewsletterAboServiceTest
 */
public class ChangeNewsletterAboServiceTest extends AbstractDomainServiceTest {

	private ChangeNewsletterAboService service;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		this.service = ChangeNewsletterAboService.createForTest(getVeranstalterRepository());
	}

	@Test
	void should_CreateSecurityIncidentAndThrowException_when_VeranstalterNotPresent() {

		// Arrange
		String uuid = "987676H";

		// Act + Assert
		try {

			service.changeStatusNewsletter(uuid);
			fail("BadRequestException");
		} catch (BadRequestException e) {

			assertNotNull(service.securityIncidentEventPayload());
			assertEquals("Versuch, einen nicht existierenden Veranstalter zu ändern: 987676H",
				service.securityIncidentEventPayload().message());
		}

	}

	@Test
	void should_changeStatusNewsletterToggleTheFlag_when_VeranstalterPresent() {

		// Arrange
		Veranstalter veranstalter = this.getVeranstalterRepository().ofId(new Identifier(UUID_LEHRER_1)).get();
		boolean expected = !veranstalter.isNewsletterEmpfaenger();

		// Act
		Veranstalter changed = service.changeStatusNewsletter(UUID_LEHRER_1);

		// Assert
		assertEquals(expected, changed.isNewsletterEmpfaenger());
		assertEquals(1, this.getVeranstalterRepository().getCountLehrerChanged());
	}

}
