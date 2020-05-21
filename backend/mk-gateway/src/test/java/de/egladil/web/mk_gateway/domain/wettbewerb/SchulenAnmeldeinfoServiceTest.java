// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;

/**
 * SchulenAnmeldeinfoServiceTest
 */
public class SchulenAnmeldeinfoServiceTest {

	private static final String LEHRER_UUID = "jahflhwl";

	private MkKatalogeResourceAdapter katalogeAdapter;

	private MkWettbewerbResourceAdapter wettbewerbAdapter;

	private SchulenAnmeldeinfoService service;

	@BeforeEach
	void setUp() {

		katalogeAdapter = Mockito.mock(MkKatalogeResourceAdapter.class);
		wettbewerbAdapter = Mockito.mock(MkWettbewerbResourceAdapter.class);
		service = SchulenAnmeldeinfoService.createForTest(katalogeAdapter, wettbewerbAdapter);
	}

	@Test
	void should_FindSchulenMitAnmeldeinfoThrowMkGatewayRuntimeException_when_WettbewerbEndpointNotPresent() {

		// Arrange
		Response response = Response.status(400).entity(ResponsePayload.messageOnly(MessagePayload.error("ResourceNotFound")))
			.build();

		Mockito.when(wettbewerbAdapter.findSchulen(LEHRER_UUID)).thenReturn(response);

		// Act
		try {

			service.findSchulenMitAnmeldeinfo(LEHRER_UUID);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Fehler beim Laden der Schulen des Lehrers", e.getMessage());
		}

	}

}
