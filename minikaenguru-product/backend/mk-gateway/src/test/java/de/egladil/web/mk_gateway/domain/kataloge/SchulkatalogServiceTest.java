// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulePayload;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.dao.KatalogeRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Schule;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * SchulkatalogServiceTest
 */
@QuarkusTest
public class SchulkatalogServiceTest {

	@InjectMock
	KatalogeRepository katalogeRepository;

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

			Schule schule = new Schule();
			schule.setKuerzel(schulkuerzel);
			schule.setName("David-Hilbert-Schule");
			schule.setLandKuerzel("DE-NI");
			schule.setLandName("Niedersachsen");
			schule.setOrtName("Göttingen");

			when(katalogeRepository.findSchuleWithKuerzel(schulkuerzel)).thenReturn(Optional.of(schule));


			// Act
			Optional<SchuleAPIModel> opt = service.findSchule(schulkuerzel);

			// Assert
			assertTrue(opt.isPresent());

		}

		@Test
		void should_findSchuleQuietlyReturnOptionalEmpty_when_mkKatalogeReturnsEmptyList() {

			// Arrange
			String schulkuerzel = "12345";
			when(katalogeRepository.findSchuleWithKuerzel(schulkuerzel)).thenReturn(Optional.empty());

			// Act
			Optional<SchuleAPIModel> opt = service.findSchule(schulkuerzel);

			// Assert
			assertTrue(opt.isEmpty());

		}
	}

	@Nested
	class FindSchulteilnahmeTests {

		Identifier veranstalterId = new Identifier("ajkgkw");

		@Test
		void should_findSchulteilnahmeReturnNull_when_aktuellerWettbewerbMissing() {

			// arrange
			String schulkuerzel = "ABCDEFGH";
			WettbewerbID wettbewerbId = new WettbewerbID(2025);

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schulkuerzel).withWettbewerbID(wettbewerbId);

			SchulePayload payload = new SchulePayload().withKuerzel(schulkuerzel);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.empty());

			// act
			Schulteilnahme result = service.findSchulteilnahme(payload);

			// assert
			assertNull(result);
			verify(wettbewerbService).aktuellerWettbewerb();
			verify(teilnahmenRepository, never()).ofTeilnahmeIdentifier(teilnahmeIdentifier);

		}

		@Test
		void should_findSchulteilnahmeReturnNull_when_statusWettbewerbErfasst() {

			// arrange
			String schulkuerzel = "ABCDEFGH";
			WettbewerbID wettbewerbId = new WettbewerbID(2025);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbId).withStatus(WettbewerbStatus.ERFASST);

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schulkuerzel).withWettbewerbID(wettbewerbId);

			SchulePayload payload = new SchulePayload().withKuerzel(schulkuerzel);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			// act
			Schulteilnahme result = service.findSchulteilnahme(payload);

			// assert
			assertNull(result);
			verify(wettbewerbService).aktuellerWettbewerb();
			verify(teilnahmenRepository, never()).ofTeilnahmeIdentifier(teilnahmeIdentifier);

		}

		@Test
		void should_findSchulteilnahmeReturnNotNull_when_statusWettbewerbAnmeldung() {

			// arrange
			String schulkuerzel = "ABCDEFGH";
			WettbewerbID wettbewerbId = new WettbewerbID(2025);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbId).withStatus(WettbewerbStatus.ANMELDUNG);

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schulkuerzel).withWettbewerbID(wettbewerbId);

			SchulePayload payload = new SchulePayload().withKuerzel(schulkuerzel);
			Teilnahme schulteilnahme = new Schulteilnahme(wettbewerbId, new Identifier(schulkuerzel), "Baumschule", veranstalterId);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(schulteilnahme));

			// act
			Schulteilnahme result = service.findSchulteilnahme(payload);

			// assert
			assertEquals(result, schulteilnahme);
			verify(wettbewerbService).aktuellerWettbewerb();
			verify(teilnahmenRepository).ofTeilnahmeIdentifier(teilnahmeIdentifier);

		}

		@Test
		void should_findSchulteilnahmeReturnNotNull_when_statusWettbewerbDownloadLehrer() {

			// arrange
			String schulkuerzel = "ABCDEFGH";
			WettbewerbID wettbewerbId = new WettbewerbID(2025);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbId).withStatus(WettbewerbStatus.DOWNLOAD_LEHRER);

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schulkuerzel).withWettbewerbID(wettbewerbId);

			SchulePayload payload = new SchulePayload().withKuerzel(schulkuerzel);
			Teilnahme schulteilnahme = new Schulteilnahme(wettbewerbId, new Identifier(schulkuerzel), "Baumschule", veranstalterId);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(schulteilnahme));

			// act
			Schulteilnahme result = service.findSchulteilnahme(payload);

			// assert
			assertEquals(result, schulteilnahme);
			verify(wettbewerbService).aktuellerWettbewerb();
			verify(teilnahmenRepository).ofTeilnahmeIdentifier(teilnahmeIdentifier);

		}

		@Test
		void should_findSchulteilnahmeReturnNotNull_when_statusWettbewerbDownloadPrivat() {

			// arrange
			String schulkuerzel = "ABCDEFGH";
			WettbewerbID wettbewerbId = new WettbewerbID(2025);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbId).withStatus(WettbewerbStatus.DOWNLOAD_PRIVAT);

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schulkuerzel).withWettbewerbID(wettbewerbId);

			SchulePayload payload = new SchulePayload().withKuerzel(schulkuerzel);
			Teilnahme schulteilnahme = new Schulteilnahme(wettbewerbId, new Identifier(schulkuerzel), "Baumschule", veranstalterId);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));
			when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(schulteilnahme));

			// act
			Schulteilnahme result = service.findSchulteilnahme(payload);

			// assert
			assertEquals(result, schulteilnahme);
			verify(wettbewerbService).aktuellerWettbewerb();
			verify(teilnahmenRepository).ofTeilnahmeIdentifier(teilnahmeIdentifier);

		}

		@Test
		void should_findSchulteilnahmeReturnNull_when_statusWettbewerbBeendet() {

			// arrange
			String schulkuerzel = "ABCDEFGH";
			WettbewerbID wettbewerbId = new WettbewerbID(2025);
			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbId).withStatus(WettbewerbStatus.BEENDET);

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schulkuerzel).withWettbewerbID(wettbewerbId);

			SchulePayload payload = new SchulePayload().withKuerzel(schulkuerzel);

			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			// act
			Schulteilnahme result = service.findSchulteilnahme(payload);

			// assert
			assertNull(result);
			verify(wettbewerbService).aktuellerWettbewerb();
			verify(teilnahmenRepository, never()).ofTeilnahmeIdentifier(teilnahmeIdentifier);

		}

	}
}
