// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VertragAdvAPIModel;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

/**
 * AdvServiceTest
 */
@QuarkusTest
public class AdvServiceTest {

	private static final String LEHRER_UUID = "guowdgqog";

	@InjectMock
	private AuthorizationService authService;

	@InjectMock
	private VertragAuftragsverarbeitungRepository vertragRepository;

	@Inject
	private AdvService advService;

	@Test
	void should_initVertrag_initAllAttributes() {

		// Arrange
		String schulname = "Hijahsdho";
		String plz = "76545";
		String ort = "Ggagdu";
		String strasse = "Ggu-Htfzu-Straße";
		String hausnummer = "13-15";

		String schulkuerzel = "ASDERS";

		VertragAdvAPIModel apiModel = new VertragAdvAPIModel()
			.withHausnummer(hausnummer)
			.withOrt(ort)
			.withPlz(plz)
			.withSchulkuerzel(schulkuerzel)
			.withSchulname(schulname)
			.withStrasse(strasse);

		Map<String, Object> schuleKatalogeMap = new HashMap<>();

		schuleKatalogeMap.put("kuerzel", schulkuerzel);
		schuleKatalogeMap.put("name", "Schule 98765");
		schuleKatalogeMap.put("ort", ort);
		schuleKatalogeMap.put("land", "Hessen");
		schuleKatalogeMap.put("kuerzelLand", "DE-HE");

		SchuleAPIModel schuleAPIModel = SchuleAPIModel.withAttributes(schuleKatalogeMap);

		Vertragstext vertragstext = new Vertragstext().withIdentifier(new Identifier("gasdgqoug"));

		String lehrerUuid = "GUIguigigzfzfi";
		String unterzeichnetAm = "11.09.2020 17:29:13";

		// Act
		VertragAuftragsdatenverarbeitung vertrag = new AdvService().initVertrag(apiModel, lehrerUuid,
			new PostleitzahlLand(plz, Optional.of(schuleAPIModel)), vertragstext,
			unterzeichnetAm);

		// Assert
		assertEquals(new Identifier(schulkuerzel), vertrag.schulkuerzel());
		assertNull(vertrag.identifier());
		assertEquals(new Identifier(lehrerUuid), vertrag.unterzeichnenderLehrer());
		assertEquals(unterzeichnetAm, vertrag.unterzeichnetAm());
		assertEquals("11.09.2020", vertrag.unterzeichnetAmDruck());
		assertEquals(vertragstext, vertrag.vertragstext());

		Anschrift anschrift = vertrag.anschrift();
		assertEquals(hausnummer, anschrift.hausnummer());
		assertEquals(strasse, anschrift.strasse());
		assertEquals(ort, anschrift.ort());
		assertEquals(plz, anschrift.plz());
		assertEquals(schulname, anschrift.schulname());
		assertEquals("DE", anschrift.laendercode());

	}

	@Test
	void should_getVertragAuftragsdatenverarbeitung_callAuthService() {

		// Arrange
		String schulkuerzel = "bjkasgca";

		Identifier lehrerId = new Identifier(LEHRER_UUID);
		Identifier teilnahmeId = new Identifier(schulkuerzel);

		when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(lehrerId, teilnahmeId,
			"[getVertragAuftragsdatenverarbeitung - " + schulkuerzel + "]"))
				.thenThrow(new AccessDeniedException());

		// Act
		try {

			advService.getVertragAuftragsdatenverarbeitung(schulkuerzel, LEHRER_UUID);
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			// nüscht
		}

	}

	@Test
	void should_createVertragAuftragsdatenverarbeitung_callAuthService() {

		// Arrange
		String schulname = "Hijahsdho";
		String plz = "76545";
		String ort = "Ggagdu";
		String strasse = "Ggu-Htfzu-Straße";
		String hausnummer = "13-15";

		String schulkuerzel = "ASDERS";

		VertragAdvAPIModel apiModel = new VertragAdvAPIModel()
			.withHausnummer(hausnummer)
			.withOrt(ort)
			.withPlz(plz)
			.withSchulkuerzel(schulkuerzel)
			.withSchulname(schulname)
			.withStrasse(strasse);

		Identifier lehrerId = new Identifier(LEHRER_UUID);
		Identifier teilnahmeId = new Identifier(schulkuerzel);

		Mockito
			.when(authService.checkPermissionForTeilnahmenummerAndReturnRolle(lehrerId, teilnahmeId,
				"[createVertragAuftragsdatenverarbeitung - " + schulkuerzel + "]"))
			.thenThrow(new AccessDeniedException());

		// Act
		try {

			advService.createVertragAuftragsdatenverarbeitung(apiModel, LEHRER_UUID);
			fail("keine AccessDeniedException");
		} catch (AccessDeniedException e) {

			// nüscht
		}

	}

}
