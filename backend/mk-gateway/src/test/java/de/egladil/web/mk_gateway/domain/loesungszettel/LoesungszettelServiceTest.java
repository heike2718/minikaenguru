// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * LoesungszettelServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class LoesungszettelServiceTest {

	@Mock
	private KinderRepository kinderRepository;

	@Mock
	private LoesungszettelRepository loesungszettelRepository;

	@Mock
	private AuthorizationService authService;

	@Mock
	private WettbewerbService wettbewerbService;

	@InjectMocks
	private LoesungszettelService service;

	@Test
	void should_loesungszettelAnlegenReturnTheConcurrentlySavedEntity_when_concurrentInsert() {

		// Arrange
		String kindID = "ahdgqug";
		Identifier loesungszettelID = new Identifier("asdqgg");
		Identifier veranstalterID = new Identifier("ldjdjw-wjofjp");

		Wettbewerb aktuellerWettbewerb = new Wettbewerb(new WettbewerbID(2021));

		Kind kind = new Kind(new Identifier(kindID)).withKlassenstufe(Klassenstufe.EINS).withLandkuerzel("DE-HE")
			.withSprache(Sprache.de).withLoesungszettelID(loesungszettelID)
			.withTeilnahmeIdentifier(TeilnahmeIdentifierAktuellerWettbewerb.createForSchulteilnahme("JUJHUTR"))
			.withKlasseID(new Identifier("dqguiguq"));

		LoesungszettelAPIModel loesungszettelDaten = new LoesungszettelAPIModel().withKindID(kindID)
			.withKlassenstufe(Klassenstufe.EINS).withUuid("neu");

		LoesungszettelRohdaten rohdaten = new LoesungszettelRohdaten().withNutzereingabe("EBCACCBDBNBN");

		Loesungszettel vorhandener = new Loesungszettel(loesungszettelID).withKindID(new Identifier(kindID)).withPunkte(5000)
			.withLaengeKaengurusprung(5).withRohdaten(rohdaten).withKlassenstufe(Klassenstufe.EINS);
		when(kinderRepository.ofId(new Identifier(kindID))).thenReturn(Optional.of(kind));
		when(loesungszettelRepository.ofID(loesungszettelID)).thenReturn(Optional.of(vorhandener));
		when(authService.checkPermissionForTeilnahmenummer(any(), any(), any())).thenReturn(Boolean.TRUE);
		when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.of(aktuellerWettbewerb));

		// Act
		ResponsePayload responsePayload = service.loesungszettelAnlegen(loesungszettelDaten, veranstalterID);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals(
			"Ein Lösungszettel für dieses Kind wurde bereits durch eine andere Person gespeichert. Bitte prüfen Sie die neuen Daten. Punkte 50,00, Länge Kängurusprung 5.",
			messagePayload.getMessage());

		assertNotNull(responsePayload.getData());

	}

}
