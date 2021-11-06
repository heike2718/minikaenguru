// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.KlassenRepository;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.klassenlisten.impl.KindImportDaten;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.OnlineLoesungszettelService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * KinderServiceImplTest
 */
@ExtendWith(MockitoExtension.class)
public class KinderServiceImplTest {

	@Mock
	AuthorizationService authService;

	@Mock
	KinderRepository kinderRepository;

	@Mock
	TeilnahmenRepository teilnahmenRepository;

	@Mock
	VeranstalterRepository veranstalterRepository;

	@Mock
	KlassenRepository klassenRepository;

	@Mock
	WettbewerbService wettbewerbService;

	@Mock
	OnlineLoesungszettelService loesungszettelService;

	@Mock
	DomainEventHandler domainEventHandler;

	@InjectMocks
	KinderServiceImpl service;

	@Test
	void should_importiereKinderDetectAndMarkDubletten() {

		// Arrange
		Identifier veranstalterID = new Identifier("gadgiqwg");
		String kuerzelLand = "DE-HH";
		String schulkuerzel = "HGTFR45D";
		String klasseUUID = "hasfhioahijdq";
		Identifier kindID = new Identifier("hhohpjp");
		Identifier kindIDNeuesKind = new Identifier("kpdoupooquo");
		List<KindImportDaten> importDaten = new ArrayList<>();
		List<Kind> vorhandeneKinder = new ArrayList<>();

		KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, Sprache.de).withKlasseUuid(klasseUUID)
			.withNachname("Kröte").withVorname("Holger");

		{

			KindRequestData kindRequestData = new KindRequestData().withKind(kindEditorModel).withKuerzelLand(kuerzelLand)
				.withUuid("neu");
			KindImportDaten kindImportDaten = new KindImportDaten(kindRequestData);
			importDaten.add(kindImportDaten);
		}

		Kind vorhandenesKind = new Kind().withDaten(kindEditorModel).withIdentifier(kindID);
		vorhandeneKinder.add(vorhandenesKind);

		when(kinderRepository.addKind(any())).thenReturn(new Kind().withDaten(kindEditorModel).withIdentifier(kindIDNeuesKind));
		doNothing().when(domainEventHandler).handleEvent(any());
		when(kinderRepository.changeKind(vorhandenesKind)).thenReturn(Boolean.TRUE);

		// Act
		List<Kind> importergebnis = service.importiereKinder(veranstalterID, schulkuerzel, importDaten, vorhandeneKinder);

		// Assert
		assertEquals(1, importergebnis.size());
		verify(kinderRepository).changeKind(vorhandenesKind);
		verify(kinderRepository).addKind(any());
		verify(domainEventHandler).handleEvent(any());
		assertTrue(vorhandenesKind.isDublettePruefen());
	}

}
