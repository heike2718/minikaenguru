// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.KlassenRepository;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.klassenlisten.KindImportVO;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenimportZeile;
import de.egladil.web.mk_gateway.domain.klassenlisten.impl.KindImportDaten;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.OnlineLoesungszettelService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

/**
 * KinderServiceImplTest
 */
@QuarkusTest
public class KinderServiceImplTest {

	@InjectMock
	AuthorizationService authService;

	@InjectMock
	KinderRepository kinderRepository;

	@InjectMock
	TeilnahmenRepository teilnahmenRepository;

	@InjectMock
	VeranstalterRepository veranstalterRepository;

	@InjectMock
	KlassenRepository klassenRepository;

	@InjectMock
	WettbewerbService wettbewerbService;

	@InjectMock
	OnlineLoesungszettelService loesungszettelService;

	@InjectMock
	DomainEventHandler domainEventHandler;

	@Inject
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
		List<KindImportVO> importDaten = new ArrayList<>();
		List<Kind> vorhandeneKinder = new ArrayList<>();

		KindEditorModel kindEditorModel = new KindEditorModel(Klassenstufe.EINS, Sprache.de).withKlasseUuid(klasseUUID)
			.withNachname("Kröte").withVorname("Holger");

		{

			KindRequestData kindRequestData = new KindRequestData().withKind(kindEditorModel).withKuerzelLand(kuerzelLand)
				.withUuid("neu");
			KindImportDaten kindImportDaten = new KindImportDaten(kindRequestData);

			KlassenimportZeile zeile = new KlassenimportZeile().withImportRohdaten("Holger;Kröte;1a;1").withKlassenstufe("1")
				.withKlasse("1a")
				.withVorname("Holger")
				.withNachname("Kröte");

			importDaten.add(new KindImportVO(zeile, kindImportDaten));
		}

		Kind vorhandenesKind = new Kind().withDaten(kindEditorModel).withIdentifier(kindID);
		vorhandeneKinder.add(vorhandenesKind);

		when(kinderRepository.addKind(any())).thenReturn(new Kind().withDaten(kindEditorModel).withIdentifier(kindIDNeuesKind));
		doNothing().when(domainEventHandler).handleEvent(any());

		// Act
		List<Kind> importergebnis = service.importiereKinder(veranstalterID, schulkuerzel, importDaten);

		// Assert
		assertEquals(1, importergebnis.size());
		verify(kinderRepository, never()).changeKind(vorhandenesKind);
		verify(kinderRepository).addKind(any());
		verify(domainEventHandler).handleEvent(any());
		assertFalse(vorhandenesKind.isDublettePruefen());
	}

}
