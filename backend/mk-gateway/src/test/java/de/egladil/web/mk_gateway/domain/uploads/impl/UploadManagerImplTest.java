// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.filescanner_service.scan.ScanService;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteImportService;
import de.egladil.web.mk_gateway.domain.loesungszettel.upload.AuswertungImportService;
import de.egladil.web.mk_gateway.domain.loesungszettel.upload.UploadAuswertungContext;
import de.egladil.web.mk_gateway.domain.statistik.AuswertungsmodusInfoService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadAuthorizationService;
import de.egladil.web.mk_gateway.domain.uploads.UploadData;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.api.Auswertungsmodus;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * UploadManagerImplTest
 */
@ExtendWith(MockitoExtension.class)
public class UploadManagerImplTest {

	@Mock
	AuthorizationService authService;

	@Mock
	UploadAuthorizationService uploadAuthService;

	@Mock
	ScanService scanService;

	@Mock
	KlassenlisteImportService klassenlisteImportService;

	@Mock
	AuswertungImportService auswertungImportService;

	@Mock
	UploadRepository uploadRepository;

	@Mock
	DomainEventHandler domainEventHandler;

	@Mock
	AuswertungsmodusInfoService auswertungsmodusInfoService;

	@InjectMocks
	UploadManagerImpl uploadManager;

	@Test
	void should_processUploadIgnoreAuswertung_when_auswertungstypOnline() {

		// Arrange
		UploadData uploadData = new UploadData("auswertung.xlsx", new byte[0]);

		Identifier veranstalterID = new Identifier("jchuewui");
		String schulkuerzel = "ZTGFRTRE";

		WettbewerbID wettbewerbID = new WettbewerbID(2022);
		Wettbewerb wettbewerb = new Wettbewerb(wettbewerbID);
		wettbewerb.naechsterStatus();
		wettbewerb.naechsterStatus();

		Rolle rolle = Rolle.LEHRER;

		UploadAuswertungContext contextObject = new UploadAuswertungContext().withKuerzelLand("DE-HH")
			.withWettbewerb(wettbewerb).withSprache(Sprache.de).withRolle(rolle);

		UploadRequestPayload uploadPayload = new UploadRequestPayload()
			.withContext(contextObject)
			.withBenutzerID(veranstalterID)
			.withTeilnahmenummer(schulkuerzel)
			.withUploadType(UploadType.AUSWERTUNG)
			.withUploadData(uploadData);

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmenummer(schulkuerzel)
			.withWettbewerbID(wettbewerbID);

		when(auswertungsmodusInfoService.ermittleAuswertungsmodusFuerTeilnahme(teilnahmeIdentifier))
			.thenReturn(Auswertungsmodus.ONLINE);

		// Act
		ResponsePayload responsePayload = uploadManager.processUpload(uploadPayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("ERROR", messagePayload.getLevel());
		assertEquals("Der Wettbewerb an dieser Schule wurde bereits online ausgewertet. Die Auswertungstabelle wird ignoriert.",
			messagePayload.getMessage());

		verify(auswertungsmodusInfoService).ermittleAuswertungsmodusFuerTeilnahme(teilnahmeIdentifier);
		verify(scanService, never()).scanFile(any());
		verify(uploadRepository, never()).findUploadByIdentifier(any());
		verify(auswertungImportService, never()).importiereAuswertung(any(), any());
	}
}
