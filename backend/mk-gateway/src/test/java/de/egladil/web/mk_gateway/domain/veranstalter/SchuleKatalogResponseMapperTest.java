// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * SchuleKatalogResponseMapperTest
 */
public class SchuleKatalogResponseMapperTest {

	@Test
	void should_getSchulenFromKatalogeAPIThrowException_when_PayloadNotAList() {

		Map<String, Object> schuleWettbewerbMap = new HashMap<>();

		schuleWettbewerbMap.put("kuerzel", "12345");
		schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.FALSE);

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), schuleWettbewerbMap)).build();

		try {

			new SchuleKatalogResponseMapper().getSchulenFromKatalogeAPI(response);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Konnte ResponsePayload von mk-kataloge nicht verarbeiten", e.getMessage());
		}

	}

}
