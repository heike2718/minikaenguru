// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * KlassenlisteImportServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class KlassenlisteImportServiceTest {

	@Mock
	private KlassenService klassenService;;

	@InjectMocks
	private KlassenlisteImportService service;

	@Test
	void should() throws IOException {

		// Arrange
		Identifier veranstalterID = new Identifier("hallo");
		String schulkuerzel = "ZUTFG654F";
		boolean nachnamenAlsZusatz = false;
		Sprache sprache = Sprache.de;
		String kuerzelLand = "DE-HE";
		String path = "/home/heike/upload/klassenlisten-testdaten/korrekt/klassenliste.csv";

		List<Klasse> klassen = new ArrayList<>();
		klassen.add(new Klasse(new Identifier("uuid-2a")).withName("2a").withSchuleID(new Identifier(schulkuerzel)));
		klassen.add(new Klasse(new Identifier("uuid-2b")).withName("2b").withSchuleID(new Identifier(schulkuerzel)));

		when(klassenService.importiereKlassen(any(), any(), anyList())).thenReturn(klassen);

		// Act
		ResponsePayload responsePayload = service.importiereKinder(veranstalterID, schulkuerzel, nachnamenAlsZusatz, sprache,
			kuerzelLand, path);
		assertNotNull(responsePayload.getData());
		// List<KindAPIModel> kinder = (List<KindAPIModel>) responsePayload.getData();

	}

}
