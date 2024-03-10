// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;

/**
 * KlassenServiceImplTest
 */
@QuarkusTest
public class KlassenServiceImplTest {

	@InjectMock
	AuthorizationService authService;

	@InjectMock
	KlassenRepository klassenRepository;

	@InjectMock
	KinderServiceImpl kinderService;

	@InjectMock
	WettbewerbService wettbewerbService;

	@InjectMock
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	KlassenServiceImpl service;

	@Nested
	class DeleteKlassenTests {

		@Test
		void should_alleKlassenLoeschenCallAuthorize() {

			// Arrange
			final Identifier schuleId = new Identifier("KIUHFRT6");
			final String lehrerUuid = "aabbcc";

			final Identifier userIdentifier = new Identifier(lehrerUuid);
			final String kontext = "[alleKlassenLoeschen - schulkuerzel=KIUHFRT6]";

			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(userIdentifier, schuleId, kontext))
				.thenThrow(new AccessDeniedException("Nö"));

			// Act
			try {

				service.alleKlassenLoeschen(schuleId, lehrerUuid);

				fail("keine AccessDeniedException");
			} catch (AccessDeniedException e) {

				// Assert
				assertEquals("Nö", e.getMessage());

				verify(authService).checkPermissionForTeilnahmenummerAndReturnRolle(userIdentifier, schuleId, kontext);
			}
		}

		@Test
		void should_alleKlassenLoeschenCallAllMethods_when_authorized() {

			// Arrange
			WettbewerbID wettbewerbID = new WettbewerbID(Integer.valueOf(2020));

			Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
			when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(wettbewerb));

			final Identifier schuleId = new Identifier("KIUHFRT6");
			final String lehrerUuid = "aabbcc";

			final Identifier userIdentifier = new Identifier(lehrerUuid);
			final String kontext = "[alleKlassenLoeschen - schulkuerzel=KIUHFRT6]";

			when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(userIdentifier, schuleId, kontext))
				.thenReturn(Rolle.LEHRER);

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(schuleId.identifier()).withWettbewerbID(wettbewerbID);

			Teilnahme teilnahme = new Schulteilnahme(wettbewerbID, schuleId, "Gartenschule", userIdentifier);
			when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier)).thenReturn(Optional.of(teilnahme));

			TeilnahmeIdentifierAktuellerWettbewerb idAktuellerWettbewerb = TeilnahmeIdentifierAktuellerWettbewerb
				.createForSchulteilnahme(schuleId.identifier());

			List<Klasse> klassen = new ArrayList<>();

			Identifier idKlasse1 = new Identifier("klasse-1");
			Identifier idKlasse2 = new Identifier("klasse-2");

			klassen.add(new Klasse(idKlasse1).withName("1a"));
			klassen.add(new Klasse(idKlasse2).withName("2b"));

			when(klassenRepository.findKlassenWithSchule(schuleId)).thenReturn(klassen);

			List<Kind> kinderKlasse1 = new ArrayList<>();
			kinderKlasse1.add(new Kind().withKlasseID(idKlasse1).withIdentifier(new Identifier("klasse-1-kind-1")));

			List<Kind> kinderKlasse2 = new ArrayList<>();

			when(kinderService.findKinderMitKlasseWithoutAuthorization(idKlasse1, idAktuellerWettbewerb)).thenReturn(kinderKlasse1);
			when(kinderService.findKinderMitKlasseWithoutAuthorization(idKlasse2, idAktuellerWettbewerb)).thenReturn(kinderKlasse2);

			when(klassenRepository.removeKlasse(klassen.get(0))).thenReturn(Boolean.TRUE);
			when(klassenRepository.removeKlasse(klassen.get(1))).thenReturn(Boolean.TRUE);

			when(kinderService.kindLoeschenWithoutAuthorizationCheck(kinderKlasse1.get(0), lehrerUuid)).thenReturn(Boolean.TRUE);

			// Act
			ResponsePayload result = service.alleKlassenLoeschen(schuleId, lehrerUuid);

			// Asssert
			assertTrue(result.isOk());

			assertEquals("Alle Klassen wurden erfolgreich gelöscht.", result.getMessage().getMessage());

			verify(teilnahmenRepository).ofTeilnahmeIdentifier(teilnahmeIdentifier);
			verify(wettbewerbService).aktuellerWettbewerb();
			verify(klassenRepository).findKlassenWithSchule(schuleId);
			verify(kinderService).findKinderMitKlasseWithoutAuthorization(idKlasse1, idAktuellerWettbewerb);
			verify(kinderService).findKinderMitKlasseWithoutAuthorization(idKlasse2, idAktuellerWettbewerb);
			verify(kinderService).kindLoeschenWithoutAuthorizationCheck(kinderKlasse1.get(0), lehrerUuid);
		}
	}
}
