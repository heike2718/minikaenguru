// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;

/**
 * SchulkatalogServiceTest
 */
public class SchulkatalogServiceTest {

	private MkKatalogeResourceAdapter katalogeResourceAdapter;

	private SchulkatalogService service;

	@BeforeEach
	void setUp() {

		katalogeResourceAdapter = Mockito.mock(MkKatalogeResourceAdapter.class);
		service = SchulkatalogService.createForTest(katalogeResourceAdapter);

	}

	@Test
	void should_findSchuleQuietlyReturnOptionalNotEmpty_when_mkKatalogeReturnsTheSchule() {

		// Arrange
		String schulkuerzel = "12345";
		List<Map<String, Object>> data = new ArrayList<>();

		{

			Map<String, Object> schuleWettbewerbMap = new HashMap<>();

			schuleWettbewerbMap.put("kuerzel", schulkuerzel);
			schuleWettbewerbMap.put("name", "David-Hilbert-Schule");
			schuleWettbewerbMap.put("ort", "Göttingen");
			schuleWettbewerbMap.put("land", "Niedersachsen");
			schuleWettbewerbMap.put("kuerzelLand", "DE-NI");
			schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.FALSE);

			data.add(schuleWettbewerbMap);
		}

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), data)).build();

		Mockito.when(katalogeResourceAdapter.findSchulen(schulkuerzel)).thenReturn(response);

		// Act
		Optional<SchuleAPIModel> opt = service.findSchuleQuietly(schulkuerzel);

		// Assert
		assertTrue(opt.isPresent());

	}

	@Test
	void should_findSchuleQuietlyReturnOptionalEmpty_when_mkKatalogeReturnsEmptyList() {

		// Arrange
		String schulkuerzel = "12345";
		List<Map<String, Object>> data = new ArrayList<>();

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), data)).build();

		Mockito.when(katalogeResourceAdapter.findSchulen(schulkuerzel)).thenReturn(response);

		// Act
		Optional<SchuleAPIModel> opt = service.findSchuleQuietly(schulkuerzel);

		// Assert
		assertTrue(opt.isEmpty());

	}

	@Test
	void should_findSchuleQuietlyReturnOptionalEmpty_when_mkKatalogeReturnsThrowsException() {

		// Arrange
		String schulkuerzel = "12345";

		Mockito.when(katalogeResourceAdapter.findSchulen(schulkuerzel))
			.thenThrow(new MkGatewayRuntimeException("schlimm schlim schlimm"));

		// Act
		Optional<SchuleAPIModel> opt = service.findSchuleQuietly(schulkuerzel);

		// Assert
		assertTrue(opt.isEmpty());

	}

}
