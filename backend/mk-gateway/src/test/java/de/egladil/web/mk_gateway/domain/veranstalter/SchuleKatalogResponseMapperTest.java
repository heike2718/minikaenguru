// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.SchuleAPIModel;
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

	@Test
	void should_getSchulenFromKatalogeAPIMapReturnList_when_PayloadAList() {

		List<Map<String, Object>> data = new ArrayList<>();

		{

			Map<String, Object> schuleWettbewerbMap = new HashMap<>();

			schuleWettbewerbMap.put("kuerzel", "12345");
			schuleWettbewerbMap.put("name", "David-Hilbert-Schule");
			schuleWettbewerbMap.put("ort", "Göttingen");
			schuleWettbewerbMap.put("land", "Niedersachsen");
			schuleWettbewerbMap.put("kuerzelLand", "DE-NI");
			schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.FALSE);

			data.add(schuleWettbewerbMap);
		}

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), data)).build();

		// Act
		List<SchuleAPIModel> result = new SchuleKatalogResponseMapper().getSchulenFromKatalogeAPI(response);

		// Assert
		assertEquals(1, result.size());

		SchuleAPIModel model = result.get(0);
		assertEquals("12345", model.kuerzel());
		assertEquals("David-Hilbert-Schule", model.name());
		assertEquals("Göttingen", model.ort());
		assertEquals("Niedersachsen", model.land());
		assertEquals("DE-NI", model.kuerzelLand());
		assertFalse(model.aktuellAngemeldet());

	}

	@Test
	void should_getSchulenFromKatalogeAPIMapReturnAnEmptyList_when_ResponsePayloadNotOk() {

		Response response = Response.ok(ResponsePayload.messageOnly(MessagePayload.error("Serverfehler"))).build();

		// Act
		List<SchuleAPIModel> result = new SchuleKatalogResponseMapper().getSchulenFromKatalogeAPI(response);

		// Assert
		assertEquals(0, result.size());
	}

}
