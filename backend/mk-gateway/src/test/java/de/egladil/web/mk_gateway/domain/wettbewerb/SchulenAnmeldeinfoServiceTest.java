// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.SchuleWettbewerbeDetails;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
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
		Response response = Response.status(404).entity(ResponsePayload.messageOnly(MessagePayload.error("ResourceNotFound")))
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

	@Test
	void should_FindSchulenMitAnmeldeinfoThrowMkGatewayRuntimeException_when_KatalogEndpointNotPresent() {

		// Arrange
		Map<String, Object> schuleWettbewerbMap = new HashMap<>();

		schuleWettbewerbMap.put("kuerzel", "12345");
		schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.FALSE);

		List<Map<String, Object>> data = new ArrayList<>();
		data.add(schuleWettbewerbMap);

		Response responseWettbewerb = Response.ok(new ResponsePayload(MessagePayload.ok(), data)).build();

		Mockito.when(wettbewerbAdapter.findSchulen(LEHRER_UUID)).thenReturn(responseWettbewerb);

		Response responseKataloge = Response.status(400)
			.entity(ResponsePayload.messageOnly(MessagePayload.error("Bad Request")))
			.build();

		Mockito.when(katalogeAdapter.findSchulen("12345")).thenReturn(responseKataloge);

		// Act
		try {

			service.findSchulenMitAnmeldeinfo(LEHRER_UUID);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Fehler beim Laden der Schulen aus dem Katalog", e.getMessage());
		}

	}

	@Test
	void should_getSchulenFromKatalogeAPIThrowException_when_PayloadNotAList() {

		Map<String, Object> schuleWettbewerbMap = new HashMap<>();

		schuleWettbewerbMap.put("kuerzel", "12345");
		schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.FALSE);

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), schuleWettbewerbMap)).build();

		try {

			service.getSchulenFromKatalogeAPI(response);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Konnte ResponsePayload von mk-kataloge nicht verarbeiten", e.getMessage());
		}

	}

	@Test
	void should_getSchulenFromWettbewerbAPIThrowException_when_PayloadNotAList() {

		Map<String, Object> schuleWettbewerbMap = new HashMap<>();

		schuleWettbewerbMap.put("kuerzel", "12345");
		schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.FALSE);

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), schuleWettbewerbMap)).build();

		try {

			service.getSchulenFromWettbewerbAPI(response);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Konnte ResponsePayload von mk-wettbewerbe nicht verarbeiten", e.getMessage());
		}

	}

	@Test
	void should_FindSchulenMitAnmeldeinfo_work() {

		// Arrange
		List<Map<String, Object>> dataWettbewerb = new ArrayList<>();

		{

			Map<String, Object> schuleWettbewerbMap = new HashMap<>();

			schuleWettbewerbMap.put("kuerzel", "12345");
			schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.TRUE);

			dataWettbewerb.add(schuleWettbewerbMap);
		}

		{

			Map<String, Object> schuleWettbewerbMap = new HashMap<>();

			schuleWettbewerbMap.put("kuerzel", "98765");
			schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.FALSE);

			dataWettbewerb.add(schuleWettbewerbMap);
		}

		List<Map<String, Object>> dataKataloge = new ArrayList<>();

		{

			Map<String, Object> schuleKatalogeMap = new HashMap<>();

			schuleKatalogeMap.put("kuerzel", "12345");
			schuleKatalogeMap.put("name", "Schule 12345");
			schuleKatalogeMap.put("ort", "Darmstadt");
			schuleKatalogeMap.put("land", "Hessen");

			dataKataloge.add(schuleKatalogeMap);
		}

		{

			Map<String, Object> schuleKatalogeMap = new HashMap<>();

			schuleKatalogeMap.put("kuerzel", "98765");
			schuleKatalogeMap.put("name", "Schule 98765");
			schuleKatalogeMap.put("ort", "Darmstadt");
			schuleKatalogeMap.put("land", "Hessen");

			dataKataloge.add(schuleKatalogeMap);
		}

		Response responseWettbewerb = Response.ok(new ResponsePayload(MessagePayload.ok(), dataWettbewerb)).build();

		Mockito.when(wettbewerbAdapter.findSchulen(LEHRER_UUID)).thenReturn(responseWettbewerb);

		Response responseKataloge = Response.ok(new ResponsePayload(MessagePayload.ok(), dataKataloge)).build();

		Mockito.when(katalogeAdapter.findSchulen("12345,98765")).thenReturn(responseKataloge);

		// Act
		List<SchuleAPIModel> result = service.findSchulenMitAnmeldeinfo(LEHRER_UUID);

		assertEquals(2, result.size());

		{

			SchuleAPIModel schule = result.get(0);
			assertEquals("12345", schule.kuerzel());
			assertEquals("Schule 12345", schule.name());
			assertEquals("Darmstadt", schule.ort());
			assertEquals("Hessen", schule.land());
			assertEquals(true, schule.aktuellAngemeldet());
		}

		{

			SchuleAPIModel schule = result.get(1);
			assertEquals("98765", schule.kuerzel());
			assertEquals("Schule 98765", schule.name());
			assertEquals("Darmstadt", schule.ort());
			assertEquals("Hessen", schule.land());
			assertEquals(false, schule.aktuellAngemeldet());
		}

	}

	@Test
	void should_mergeDataFromSchulenOfLehrer_work_when_passt() {

		List<SchuleAPIModel> schulenWettbewerb = new ArrayList<>();

		{

			Map<String, Object> schuleWettbewerbMap = new HashMap<>();

			schuleWettbewerbMap.put("kuerzel", "12345");
			schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.TRUE);

			List<Map<String, Object>> data = new ArrayList<>();
			data.add(schuleWettbewerbMap);

			schulenWettbewerb.add(SchuleAPIModel.withAttributes(schuleWettbewerbMap));
		}

		{

			Map<String, Object> schuleWettbewerbMap = new HashMap<>();

			schuleWettbewerbMap.put("kuerzel", "98765");
			schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.FALSE);

			List<Map<String, Object>> data = new ArrayList<>();
			data.add(schuleWettbewerbMap);

			schulenWettbewerb.add(SchuleAPIModel.withAttributes(schuleWettbewerbMap));
		}

		List<SchuleAPIModel> schulenKataloge = new ArrayList<>();

		{

			Map<String, Object> schuleKatalogeMap = new HashMap<>();

			schuleKatalogeMap.put("kuerzel", "12345");
			schuleKatalogeMap.put("name", "Schule 12345");
			schuleKatalogeMap.put("ort", "Darmstadt");
			schuleKatalogeMap.put("land", "Hessen");

			List<Map<String, Object>> data = new ArrayList<>();
			data.add(schuleKatalogeMap);

			schulenKataloge.add(SchuleAPIModel.withAttributes(schuleKatalogeMap));
		}

		{

			Map<String, Object> schuleKatalogeMap = new HashMap<>();

			schuleKatalogeMap.put("kuerzel", "98765");
			schuleKatalogeMap.put("name", "Schule 98765");
			schuleKatalogeMap.put("ort", "Darmstadt");
			schuleKatalogeMap.put("land", "Hessen");

			List<Map<String, Object>> data = new ArrayList<>();
			data.add(schuleKatalogeMap);

			schulenKataloge.add(SchuleAPIModel.withAttributes(schuleKatalogeMap));
		}

		// Act
		List<SchuleAPIModel> result = service.mergeDataFromSchulenOfLehrer(schulenKataloge, schulenWettbewerb);

		// Assert
		assertEquals(2, result.size());

		{

			SchuleAPIModel schule = result.get(0);
			assertEquals("12345", schule.kuerzel());
			assertEquals("Schule 12345", schule.name());
			assertEquals("Darmstadt", schule.ort());
			assertEquals("Hessen", schule.land());
			assertEquals(true, schule.aktuellAngemeldet());
		}

		{

			SchuleAPIModel schule = result.get(1);
			assertEquals("98765", schule.kuerzel());
			assertEquals("Schule 98765", schule.name());
			assertEquals("Darmstadt", schule.ort());
			assertEquals("Hessen", schule.land());
			assertEquals(false, schule.aktuellAngemeldet());
		}

	}

	@Test
	void should_mergeDataFromSchulenOfLehrer_work_when_LehrerWenigerSchulen() {

		List<SchuleAPIModel> schulenWettbewerb = new ArrayList<>();

		{

			Map<String, Object> schuleWettbewerbMap = new HashMap<>();

			schuleWettbewerbMap.put("kuerzel", "12345");
			schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.TRUE);

			List<Map<String, Object>> data = new ArrayList<>();
			data.add(schuleWettbewerbMap);

			schulenWettbewerb.add(SchuleAPIModel.withAttributes(schuleWettbewerbMap));
		}

		List<SchuleAPIModel> schulenKataloge = new ArrayList<>();

		{

			Map<String, Object> schuleKatalogeMap = new HashMap<>();

			schuleKatalogeMap.put("kuerzel", "12345");
			schuleKatalogeMap.put("name", "Schule 12345");
			schuleKatalogeMap.put("ort", "Darmstadt");
			schuleKatalogeMap.put("land", "Hessen");

			List<Map<String, Object>> data = new ArrayList<>();
			data.add(schuleKatalogeMap);

			schulenKataloge.add(SchuleAPIModel.withAttributes(schuleKatalogeMap));
		}

		{

			Map<String, Object> schuleKatalogeMap = new HashMap<>();

			schuleKatalogeMap.put("kuerzel", "98765");
			schuleKatalogeMap.put("name", "Schule 98765");
			schuleKatalogeMap.put("ort", "Darmstadt");
			schuleKatalogeMap.put("land", "Hessen");

			List<Map<String, Object>> data = new ArrayList<>();
			data.add(schuleKatalogeMap);

			schulenKataloge.add(SchuleAPIModel.withAttributes(schuleKatalogeMap));
		}

		// Act
		List<SchuleAPIModel> result = service.mergeDataFromSchulenOfLehrer(schulenKataloge, schulenWettbewerb);

		// Assert
		assertEquals(1, result.size());

		{

			SchuleAPIModel schule = result.get(0);
			assertEquals("12345", schule.kuerzel());
			assertEquals("Schule 12345", schule.name());
			assertEquals("Darmstadt", schule.ort());
			assertEquals("Hessen", schule.land());
			assertEquals(true, schule.aktuellAngemeldet());
		}

	}

	@Test
	void should_mergeDataFromSchulenOfLehrer_work_when_LehrerMehrSchulen() {

		List<SchuleAPIModel> schulenWettbewerb = new ArrayList<>();

		{

			Map<String, Object> schuleWettbewerbMap = new HashMap<>();

			schuleWettbewerbMap.put("kuerzel", "12345");
			schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.TRUE);

			List<Map<String, Object>> data = new ArrayList<>();
			data.add(schuleWettbewerbMap);

			schulenWettbewerb.add(SchuleAPIModel.withAttributes(schuleWettbewerbMap));
		}

		{

			Map<String, Object> schuleWettbewerbMap = new HashMap<>();

			schuleWettbewerbMap.put("kuerzel", "98765");
			schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.FALSE);

			List<Map<String, Object>> data = new ArrayList<>();
			data.add(schuleWettbewerbMap);

			schulenWettbewerb.add(SchuleAPIModel.withAttributes(schuleWettbewerbMap));
		}

		List<SchuleAPIModel> schulenKataloge = new ArrayList<>();

		{

			Map<String, Object> schuleKatalogeMap = new HashMap<>();

			schuleKatalogeMap.put("kuerzel", "98765");
			schuleKatalogeMap.put("name", "Schule 98765");
			schuleKatalogeMap.put("ort", "Darmstadt");
			schuleKatalogeMap.put("land", "Hessen");

			List<Map<String, Object>> data = new ArrayList<>();
			data.add(schuleKatalogeMap);

			schulenKataloge.add(SchuleAPIModel.withAttributes(schuleKatalogeMap));
		}

		// Act
		List<SchuleAPIModel> result = service.mergeDataFromSchulenOfLehrer(schulenKataloge, schulenWettbewerb);

		// Assert
		assertEquals(2, result.size());

		{

			SchuleAPIModel schule = result.get(0);
			assertEquals("98765", schule.kuerzel());
			assertEquals("Schule 98765", schule.name());
			assertEquals("Darmstadt", schule.ort());
			assertEquals("Hessen", schule.land());
			assertEquals(false, schule.aktuellAngemeldet());
		}

		{

			SchuleAPIModel schule = result.get(1);
			assertEquals("12345", schule.kuerzel());
			assertEquals("unbekannter Schulname", schule.name());
			assertEquals("unbekannter Ort", schule.ort());
			assertEquals("unbekanntes Land / Bundesland", schule.land());
			assertEquals(true, schule.aktuellAngemeldet());
		}

		DataInconsistencyRegistered event = service.getDataInconsistencyRegistered();
		assertNotNull(event);
		assertNotNull(event.occuredOn());
		assertEquals(
			"Nicht alle Schulen auf beiden Seiten gefunden: Kataloge: [SchuleAPIModel [kuerzel=98765]], Lehrer: [SchuleAPIModel [kuerzel=12345], SchuleAPIModel [kuerzel=98765]]",
			event.message());
		assertEquals("DataInconsistencyRegistered", event.typeName());

	}

	@Test
	void should_getSchuleAusWettbewerbAPIResponse_Work() {

		// Arrange
		Map<String, Object> schuleWettbewerbMap = new HashMap<>();

		schuleWettbewerbMap.put("kuerzel", "12345");
		schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.TRUE);

		Map<String, Object> detailsMap = new HashMap<>();
		detailsMap.put("kuerzel", "12345");
		detailsMap.put("nameUrkunde", "12345-Schule");
		detailsMap.put("kollegen", "Herta Meier");
		detailsMap.put("angemeldetDurch", "John Doe");
		detailsMap.put("anzahlTeilnahmen", Integer.valueOf(1));

		schuleWettbewerbMap.put("details", detailsMap);

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), schuleWettbewerbMap)).build();

		// Act
		SchuleAPIModel schule = service.getSchuleAusWettbewerbAPIResponse(response);

		// Assert
		assertEquals(true, schule.aktuellAngemeldet());
		assertEquals("12345", schule.kuerzel());

		SchuleWettbewerbeDetails details = schule.getDetails();
		assertNotNull(details);
		assertEquals("John Doe", details.angemeldetDurch());
		assertEquals("Herta Meier", details.kollegen());
		assertEquals("12345", details.kuerzel());
		assertEquals("12345-Schule", details.nameUrkunde());

	}

	@Test
	void should_getSchuleAusWettbewerbAPIResponseThrowException_when_PayloadNotAMap() {

		// Arrange
		Map<String, Object> schuleWettbewerbMap = new HashMap<>();

		schuleWettbewerbMap.put("kuerzel", "12345");
		schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.TRUE);

		Map<String, Object> detailsMap = new HashMap<>();
		detailsMap.put("kuerzel", "12345");
		detailsMap.put("nameUrkunde", "12345-Schule");
		detailsMap.put("kollegen", "Herta Meier");
		detailsMap.put("angemeldetDurch", "John Doe");
		detailsMap.put("anzahlTeilnahmen", Integer.valueOf(1));

		schuleWettbewerbMap.put("details", detailsMap);

		List<Map<String, Object>> list = new ArrayList<>();
		list.add(schuleWettbewerbMap);

		Response response = Response.ok(new ResponsePayload(MessagePayload.ok(), list)).build();

		// Act
		try {

			service.getSchuleAusWettbewerbAPIResponse(response);
			fail("Keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals(
				"Konnte ResponsePayload von mk-wettbewerbe nicht verarbeiten",
				e.getMessage());
		}

	}

	@Test
	void should_getSchuleWithWettbewerbsdetails_work() {

		// Arrange
		Map<String, Object> schuleWettbewerbMap = new HashMap<>();

		schuleWettbewerbMap.put("kuerzel", "12345");
		schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.TRUE);

		Map<String, Object> detailsMap = new HashMap<>();
		detailsMap.put("kuerzel", "12345");
		detailsMap.put("nameUrkunde", "12345-Schule");
		detailsMap.put("kollegen", "Herta Meier");
		detailsMap.put("angemeldetDurch", "John Doe");
		detailsMap.put("anzahlTeilnahmen", Integer.valueOf(1));

		schuleWettbewerbMap.put("details", detailsMap);

		Map<String, Object> schuleKatalogeMap = new HashMap<>();

		schuleKatalogeMap.put("kuerzel", "12345");
		schuleKatalogeMap.put("name", "Schule 12345");
		schuleKatalogeMap.put("ort", "Darmstadt");
		schuleKatalogeMap.put("land", "Hessen");

		Response wettbewerbeResponse = Response.ok(new ResponsePayload(MessagePayload.ok(), schuleWettbewerbMap)).build();
		Response katalogeResponse = Response
			.ok(new ResponsePayload(MessagePayload.ok(), Arrays.asList(new Map[] { schuleKatalogeMap }))).build();

		Mockito.when(wettbewerbAdapter.getSchuleDashboardModel("12345", LEHRER_UUID)).thenReturn(wettbewerbeResponse);
		Mockito.when(katalogeAdapter.findSchulen("12345")).thenReturn(katalogeResponse);

		// Act
		SchuleAPIModel schule = service.getSchuleWithWettbewerbsdetails("12345", LEHRER_UUID);

		// Assert
		assertTrue(schule.aktuellAngemeldet());
		assertEquals("12345", schule.kuerzel());
		assertEquals("Schule 12345", schule.name());
		assertEquals("Darmstadt", schule.ort());
		assertEquals("Hessen", schule.land());

		SchuleWettbewerbeDetails details = schule.getDetails();
		assertEquals("John Doe", details.angemeldetDurch());
		assertEquals("Herta Meier", details.kollegen());
		assertEquals("12345", details.kuerzel());
		assertEquals("12345-Schule", details.nameUrkunde());

	}

	@Test
	void should_getSchuleWithWettbewerbsdetailsThrowException_when_KatalogItemsResponseError() {

		// Arrange
		Response katalogeResponse = Response
			.status(400).entity(ResponsePayload.messageOnly(MessagePayload.error("bad request"))).build();

		Mockito.when(wettbewerbAdapter.getSchuleDashboardModel("12345", LEHRER_UUID)).thenReturn(null);
		Mockito.when(katalogeAdapter.findSchulen("12345")).thenReturn(katalogeResponse);

		// Act
		try {

			service.getSchuleWithWettbewerbsdetails("12345", LEHRER_UUID);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Fehler beim Laden der Schulen aus dem Katalog", e.getMessage());
		}

	}

	@Test
	void should_getSchuleWithWettbewerbsdetailsReturnIncompleteObject_when_KatalogeintragFehlt() {

		// Arrange
		Map<String, Object> schuleWettbewerbMap = new HashMap<>();

		schuleWettbewerbMap.put("kuerzel", "12345");
		schuleWettbewerbMap.put("aktuellAngemeldet", Boolean.TRUE);

		Map<String, Object> detailsMap = new HashMap<>();
		detailsMap.put("kuerzel", "12345");
		detailsMap.put("nameUrkunde", "12345-Schule");
		detailsMap.put("kollegen", "Herta Meier");
		detailsMap.put("angemeldetDurch", "John Doe");
		detailsMap.put("anzahlTeilnahmen", Integer.valueOf(1));

		schuleWettbewerbMap.put("details", detailsMap);

		Map<String, Object> schuleKatalogeMap = new HashMap<>();

		schuleKatalogeMap.put("kuerzel", "12345");
		schuleKatalogeMap.put("name", "Schule 12345");
		schuleKatalogeMap.put("ort", "Darmstadt");
		schuleKatalogeMap.put("land", "Hessen");

		Response wettbewerbeResponse = Response.ok(new ResponsePayload(MessagePayload.ok(), schuleWettbewerbMap)).build();
		Response katalogeResponse = Response
			.ok(new ResponsePayload(MessagePayload.ok(), new ArrayList<>())).build();

		Mockito.when(wettbewerbAdapter.getSchuleDashboardModel("12345", LEHRER_UUID)).thenReturn(wettbewerbeResponse);
		Mockito.when(katalogeAdapter.findSchulen("12345")).thenReturn(katalogeResponse);

		// Act
		SchuleAPIModel schule = service.getSchuleWithWettbewerbsdetails("12345", LEHRER_UUID);

		// Assert
		assertTrue(schule.aktuellAngemeldet());
		assertEquals("12345", schule.kuerzel());
		assertEquals("unbekannter Schulname", schule.name());
		assertEquals("unbekannter Ort", schule.ort());
		assertEquals("unbekanntes Land / Bundesland", schule.land());

		SchuleWettbewerbeDetails details = schule.getDetails();
		assertEquals("John Doe", details.angemeldetDurch());
		assertEquals("Herta Meier", details.kollegen());
		assertEquals("12345", details.kuerzel());
		assertEquals("12345-Schule", details.nameUrkunde());

	}

	@Test
	void should_getSchuleWithWettbewerbsdetailsThrowAccessDenied_when_SchuleWettbewerbsdetailsStatus403() {

		// Arrange
		Map<String, Object> schuleKatalogeMap = new HashMap<>();

		schuleKatalogeMap.put("kuerzel", "12345");
		schuleKatalogeMap.put("name", "Schule 12345");
		schuleKatalogeMap.put("ort", "Darmstadt");
		schuleKatalogeMap.put("land", "Hessen");

		Response wettbewerbeResponse = Response.status(403)
			.entity(ResponsePayload.messageOnly(MessagePayload.error("Keine Berechrigung"))).build();
		Response katalogeResponse = Response
			.ok(new ResponsePayload(MessagePayload.ok(), Arrays.asList(new Map[] { schuleKatalogeMap }))).build();

		Mockito.when(wettbewerbAdapter.getSchuleDashboardModel("12345", LEHRER_UUID)).thenReturn(wettbewerbeResponse);
		Mockito.when(katalogeAdapter.findSchulen("12345")).thenReturn(katalogeResponse);

		// Act
		try {

			service.getSchuleWithWettbewerbsdetails("12345", LEHRER_UUID);
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			// nüscht
		}

	}

	@Test
	void should_getSchuleWithWettbewerbsdetailsThrowException_when_SchuleWettbewerbsdetailsStatus400() {

		// Arrange
		Map<String, Object> schuleKatalogeMap = new HashMap<>();

		schuleKatalogeMap.put("kuerzel", "12345");
		schuleKatalogeMap.put("name", "Schule 12345");
		schuleKatalogeMap.put("ort", "Darmstadt");
		schuleKatalogeMap.put("land", "Hessen");

		Response wettbewerbeResponse = Response.status(400)
			.entity(ResponsePayload.messageOnly(MessagePayload.error("bad request"))).build();
		Response katalogeResponse = Response
			.ok(new ResponsePayload(MessagePayload.ok(), Arrays.asList(new Map[] { schuleKatalogeMap }))).build();

		Mockito.when(wettbewerbAdapter.getSchuleDashboardModel("12345", LEHRER_UUID)).thenReturn(wettbewerbeResponse);
		Mockito.when(katalogeAdapter.findSchulen("12345")).thenReturn(katalogeResponse);

		// Act
		try {

			service.getSchuleWithWettbewerbsdetails("12345", LEHRER_UUID);
			fail("keine MkGatewayRuntimeException");
		} catch (MkGatewayRuntimeException e) {

			assertEquals("Fehler beim Laden Schulwettbewerbdetails des Lehrers", e.getMessage());
		}

	}

}
