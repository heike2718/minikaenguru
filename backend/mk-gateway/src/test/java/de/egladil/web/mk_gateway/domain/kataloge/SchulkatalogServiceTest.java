// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulePayload;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;

/**
 * SchulkatalogServiceTest
 */
@QuarkusTest
public class SchulkatalogServiceTest {

	@InjectMock
	private MkKatalogeResourceAdapter katalogeResourceAdapter;

	@InjectMock
	WettbewerbService wettbewerbService;

	@InjectMock
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	private SchulkatalogService service;

	@Nested
	class FindSchulenTests {

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

	@Nested
	class ChangeSchulnameTests {

		@Test
		void should_changeNameNotUpdateTeilnahme_when_aktuellerWettbewerbStatusErfasst() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2020));

			assertEquals(WettbewerbStatus.ERFASST, wettbewerb.status());
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			String schulkuerzel = "ZHGT5R43";
			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schulkuerzel).withWettbewerbID(wettbewerb.id());

			SchulePayload payload = SchulePayload.create(schulkuerzel, "Neuer Name", "KUERZELORT", "Name Ort", "DE-HH", "Hamburg");

			when(katalogeResourceAdapter.renameSchule(schulkuerzel, "secret", payload)).thenReturn(Response.ok().build());

			// Act
			service.renameSchule(schulkuerzel, "secret", payload);

			// Assert
			verify(teilnahmenRepository, never()).ofTeilnahmeIdentifier(teilnahmeIdentifier);
			verify(teilnahmenRepository, never()).changeTeilnahme(any());

		}

		@Test
		void should_changeNameUpdateTeilnahme_when_aktuellerWettbewerbStatusAnmeldung_andTeilnahme_vorhanden() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2020));
			wettbewerb.naechsterStatus();

			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			String schulkuerzel = "ZHGT5R43";
			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schulkuerzel).withWettbewerbID(wettbewerb.id());

			String veranstalterId = "qwuiwui-qwuoqo";

			Schulteilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), new Identifier(schulkuerzel), "alter Schulname",
				new Identifier(veranstalterId));

			when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(teilnahme));
			doNothing().when(teilnahmenRepository).changeTeilnahme(any());

			SchulePayload payload = SchulePayload.create(schulkuerzel, "Neuer Name", "KUERZELORT", "Name Ort", "DE-HH", "Hamburg");

			when(katalogeResourceAdapter.renameSchule(schulkuerzel, "secret", payload)).thenReturn(Response.ok().build());

			// Act
			service.renameSchule(schulkuerzel, "secret", payload);

			// Assert
			verify(teilnahmenRepository).ofTeilnahmeIdentifier(teilnahmeIdentifier);
			verify(teilnahmenRepository).changeTeilnahme(any());

		}

		@Test
		void should_changeNameUpdateTeilnahme_when_aktuellerWettbewerbStatusDownloadLehrer_andTeilnahme_vorhanden() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2020));
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();

			assertEquals(WettbewerbStatus.DOWNLOAD_LEHRER, wettbewerb.status());
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			String schulkuerzel = "ZHGT5R43";
			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schulkuerzel).withWettbewerbID(wettbewerb.id());

			String veranstalterId = "qwuiwui-qwuoqo";

			Schulteilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), new Identifier(schulkuerzel), "alter Schulname",
				new Identifier(veranstalterId));

			when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(teilnahme));
			doNothing().when(teilnahmenRepository).changeTeilnahme(any());

			SchulePayload payload = SchulePayload.create(schulkuerzel, "Neuer Name", "KUERZELORT", "Name Ort", "DE-HH", "Hamburg");

			when(katalogeResourceAdapter.renameSchule(schulkuerzel, "secret", payload)).thenReturn(Response.ok().build());

			// Act
			service.renameSchule(schulkuerzel, "secret", payload);

			// Assert
			verify(teilnahmenRepository).ofTeilnahmeIdentifier(teilnahmeIdentifier);
			verify(teilnahmenRepository).changeTeilnahme(any());

		}

		@Test
		void should_changeNameUpdateTeilnahme_when_aktuellerWettbewerbStatusDownloadPrivat_andTeilnahme_vorhanden() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2020));
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();

			assertEquals(WettbewerbStatus.DOWNLOAD_PRIVAT, wettbewerb.status());
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			String schulkuerzel = "ZHGT5R43";
			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schulkuerzel).withWettbewerbID(wettbewerb.id());

			String veranstalterId = "qwuiwui-qwuoqo";

			Schulteilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), new Identifier(schulkuerzel), "alter Schulname",
				new Identifier(veranstalterId));

			when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(teilnahme));
			doNothing().when(teilnahmenRepository).changeTeilnahme(any());

			SchulePayload payload = SchulePayload.create(schulkuerzel, "Neuer Name", "KUERZELORT", "Name Ort", "DE-HH", "Hamburg");

			when(katalogeResourceAdapter.renameSchule(schulkuerzel, "secret", payload)).thenReturn(Response.ok().build());

			// Act
			service.renameSchule(schulkuerzel, "secret", payload);

			// Assert
			verify(teilnahmenRepository).ofTeilnahmeIdentifier(teilnahmeIdentifier);
			verify(teilnahmenRepository).changeTeilnahme(any());

		}

		@Test
		void should_changeNameNotUpdateTeilnahme_when_aktuellerWettbewerbStatusBeendet() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2020));
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();

			assertEquals(WettbewerbStatus.BEENDET, wettbewerb.status());
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			String schulkuerzel = "ZHGT5R43";
			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schulkuerzel).withWettbewerbID(wettbewerb.id());

			SchulePayload payload = SchulePayload.create(schulkuerzel, "Neuer Name", "KUERZELORT", "Name Ort", "DE-HH", "Hamburg");

			when(katalogeResourceAdapter.renameSchule(schulkuerzel, "secret", payload)).thenReturn(Response.ok().build());

			// Act
			service.renameSchule(schulkuerzel, "secret", payload);

			// Assert
			verify(teilnahmenRepository, never()).ofTeilnahmeIdentifier(teilnahmeIdentifier);
			verify(teilnahmenRepository, never()).changeTeilnahme(any());

		}

		@Test
		void should_changeNameNotUpdateTeilnahme_when_aktuellerWettbewerbStatusAnmeldung_andKeineTeilnahme_vorhanden() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2020));
			wettbewerb.naechsterStatus();

			assertEquals(WettbewerbStatus.ANMELDUNG, wettbewerb.status());
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			String schulkuerzel = "ZHGT5R43";
			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schulkuerzel).withWettbewerbID(wettbewerb.id());

			when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.empty());

			SchulePayload payload = SchulePayload.create(schulkuerzel, "Neuer Name", "KUERZELORT", "Name Ort", "DE-HH", "Hamburg");

			when(katalogeResourceAdapter.renameSchule(schulkuerzel, "secret", payload)).thenReturn(Response.ok().build());

			// Act
			service.renameSchule(schulkuerzel, "secret", payload);

			// Assert
			verify(teilnahmenRepository).ofTeilnahmeIdentifier(teilnahmeIdentifier);
			verify(teilnahmenRepository, never()).changeTeilnahme(any());

		}

		@Test
		void should_changeNameNotSearchTelnahme_when_aktuellerWettbewerbMissing() {

			// Arrange
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.empty());

			String schulkuerzel = "ZHGT5R43";

			SchulePayload payload = SchulePayload.create(schulkuerzel, "Neuer Name", "KUERZELORT", "Name Ort", "DE-HH", "Hamburg");

			when(katalogeResourceAdapter.renameSchule(schulkuerzel, "secret", payload)).thenReturn(Response.ok().build());

			// Act
			service.renameSchule(schulkuerzel, "secret", payload);

			// Assert
			verify(teilnahmenRepository, never()).ofTeilnahmeIdentifier(any());
			verify(teilnahmenRepository, never()).changeTeilnahme(any());

		}

		@Test
		void should_changeNameUpdateTeilnahmeReturnWarning_when_ExceptionOnPersist() {

			// Arrange
			Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(2020));
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();
			wettbewerb.naechsterStatus();

			assertEquals(WettbewerbStatus.DOWNLOAD_PRIVAT, wettbewerb.status());
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			String schulkuerzel = "ZHGT5R43";
			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schulkuerzel).withWettbewerbID(wettbewerb.id());

			String veranstalterId = "qwuiwui-qwuoqo";

			Schulteilnahme teilnahme = new Schulteilnahme(wettbewerb.id(), new Identifier(schulkuerzel), "alter Schulname",
				new Identifier(veranstalterId));

			when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(teilnahme));
			doThrow(RuntimeException.class).when(teilnahmenRepository).changeTeilnahme(any());

			SchulePayload payload = SchulePayload.create(schulkuerzel, "Neuer Name", "KUERZELORT", "Name Ort", "DE-HH", "Hamburg");

			when(katalogeResourceAdapter.renameSchule(schulkuerzel, "secret", payload)).thenReturn(Response.ok().build());

			// Act
			Response response = service.renameSchule(schulkuerzel, "secret", payload);

			// Assert
			ResponsePayload responsePayload = (ResponsePayload) response.getEntity();

			assertEquals("WARN", responsePayload.getMessage().getLevel());
			assertEquals("Umbenennung im Schulkatalog erfolgreich, aber in Schulteilnahme nicht",
				responsePayload.getMessage().getMessage());

			verify(teilnahmenRepository).ofTeilnahmeIdentifier(teilnahmeIdentifier);
			verify(teilnahmenRepository).changeTeilnahme(any());

		}

		@Test
		void should_changeNameNotCallAnyServices_when_ResponseFromKatalogeNotOk() {

			// Arrange
			String schulkuerzel = "ZHGT5R43";

			SchulePayload payload = SchulePayload.create(schulkuerzel, "Neuer Name", "KUERZELORT", "Name Ort", "DE-HH", "Hamburg");

			when(katalogeResourceAdapter.renameSchule(schulkuerzel, "secret", payload))
				.thenReturn(Response.status(500).entity(ResponsePayload.messageOnly(MessagePayload.error("schlimm"))).build());

			// Act
			Response response = service.renameSchule(schulkuerzel, "secret", payload);

			// Assert
			ResponsePayload responsePayload = (ResponsePayload) response.getEntity();
			assertEquals("ERROR", responsePayload.getMessage().getLevel());
			assertEquals("schlimm", responsePayload.getMessage().getMessage());

			verify(wettbewerbService, never()).aktuellerWettbewerb();
			verify(teilnahmenRepository, never()).ofTeilnahmeIdentifier(any());
			verify(teilnahmenRepository, never()).changeTeilnahme(any());

		}
	}

}
