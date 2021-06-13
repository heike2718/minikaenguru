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
import de.egladil.web.mk_gateway.domain.klassenlisten.UploadKlassenlisteContext;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;

/**
 * KlassenlisteCSVImportServiceTest
 */
@ExtendWith(MockitoExtension.class)
public class KlassenlisteCSVImportServiceTest {

	@Mock
	private KlassenService klassenService;;

	@InjectMocks
	private KlassenlisteCSVImportService service;

	@Test
	void should() throws IOException {

		// Arrange
		String veranstalterUuid = "hallo";
		String schulkuerzel = "ZUTFG654F";
		boolean nachnamenAlsZusatz = false;
		Sprache sprache = Sprache.de;
		String kuerzelLand = "DE-HE";

		UploadKlassenlisteContext uploadKlassenlisteContext = new UploadKlassenlisteContext().withKuerzelLand(kuerzelLand)
			.withNachnameAlsZusatz(nachnamenAlsZusatz).withSprache(sprache);

		service.setPathUploadDir("/home/heike/upload/klassenlisten-testdaten/korrekt");

		PersistenterUpload persistenterUpload = new PersistenterUpload();
		persistenterUpload.setUuid("klassenliste");
		persistenterUpload.setVeranstalterUuid(veranstalterUuid);
		persistenterUpload.setTeilnahmenummer(schulkuerzel);

		List<Klasse> klassen = new ArrayList<>();
		klassen.add(new Klasse(new Identifier("uuid-2a")).withName("2a").withSchuleID(new Identifier(schulkuerzel)));
		klassen.add(new Klasse(new Identifier("uuid-2b")).withName("2b").withSchuleID(new Identifier(schulkuerzel)));

		when(klassenService.importiereKlassen(any(), any(), anyList())).thenReturn(klassen);

		// Act
		ResponsePayload responsePayload = service.importiereKinder(uploadKlassenlisteContext, persistenterUpload);
		assertNotNull(responsePayload.getData());
		// List<KindAPIModel> kinder = (List<KindAPIModel>) responsePayload.getData();

	}

}
