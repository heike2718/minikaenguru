// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.klassenlisten.KlassenlisteImportService;
import de.egladil.web.mk_gateway.domain.klassenlisten.UploadKlassenlisteContext;
import de.egladil.web.mk_gateway.domain.loesungszettel.upload.AuswertungImportService;
import de.egladil.web.mk_gateway.domain.loesungszettel.upload.UploadAuswertungContext;
import de.egladil.web.mk_gateway.domain.statistik.AuswertungsmodusInfoService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadAuthorizationService;
import de.egladil.web.mk_gateway.domain.uploads.UploadData;
import de.egladil.web.mk_gateway.domain.uploads.UploadIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.uploads.scan.FileScanResult;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.api.Auswertungsmodus;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * UploadManagerImplTest
 */
@QuarkusTest
public class UploadManagerImplTest {

	@InjectMock
	AuthorizationService authService;

	@InjectMock
	UploadAuthorizationService uploadAuthService;

	@InjectMock
	UploadScannerDelegate uploadScannerDelegate;

	@InjectMock
	KlassenlisteImportService klassenlisteImportService;

	@InjectMock
	AuswertungImportService auswertungImportService;

	@InjectMock
	UploadRepository uploadRepository;

	@InjectMock
	DomainEventHandler domainEventHandler;

	@InjectMock
	AuswertungsmodusInfoService auswertungsmodusInfoService;

	@Inject
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
		verify(uploadScannerDelegate, never()).scanUpload(any(UploadRequestPayload.class));
		verify(uploadRepository, never()).findUploadByIdentifier(any());
		verify(auswertungImportService, never()).importiereAuswertung(any(), any(), any());
	}

	@Test
	void should_processUploadAUSWERTUNGReturnWarnung_when_klassenliste() {

		// Arrange

		UploadData uploadData = new UploadData("auswertung.xlsx",
			MkGatewayFileUtils.readBytesFromClasspathPath("/upload/klassenlisten/korrekt/klassenliste.csv"));

		Identifier veranstalterID = new Identifier("jchuewui");
		String schulkuerzel = "ZTGFRTRE";

		PersistenterUpload persistenterUpload = new PersistenterUpload();
		persistenterUpload.setBenutzerUuid("adqgui");
		persistenterUpload.setDateiname("schule-1.csv");
		persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
		persistenterUpload.setUploadType(UploadType.AUSWERTUNG);
		persistenterUpload.setUuid(UUID.randomUUID().toString());

		when(uploadRepository.findUploadByIdentifier(any(UploadIdentifier.class)))
			.thenReturn(Optional.empty());

		when(uploadRepository.addUploadMetaData(any(PersistenterUpload.class))).thenReturn(persistenterUpload);

		doNothing().when(uploadRepository).deleteUpload(persistenterUpload.getUuid());

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

		FileScanResult scanResult = new FileScanResult().withMediaType("text/plain").withUserID("zoazodq");

		when(uploadScannerDelegate.scanUpload(any(UploadRequestPayload.class))).thenReturn(scanResult);

		when(auswertungsmodusInfoService.ermittleAuswertungsmodusFuerTeilnahme(teilnahmeIdentifier))
			.thenReturn(Auswertungsmodus.OFFLINE);

		// Act
		ResponsePayload responsePayload = uploadManager.processUpload(uploadPayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals(
			"Die Datei kann nicht verarbeitet werden. Haben Sie statt einer Auswertungstabelle versehentlich eine Klassenliste hochgeladen?",
			messagePayload.getMessage());

		verify(auswertungsmodusInfoService).ermittleAuswertungsmodusFuerTeilnahme(teilnahmeIdentifier);
		verify(uploadScannerDelegate).scanUpload(any(UploadRequestPayload.class));
		verify(uploadRepository).findUploadByIdentifier(any(UploadIdentifier.class));
		verify(uploadRepository).addUploadMetaData(any(PersistenterUpload.class));
		verify(uploadRepository).deleteUpload(persistenterUpload.getUuid());
		verify(auswertungImportService, never()).importiereAuswertung(any(), any(), any());

	}

	@Test
	void should_processUploadKLASSENLISTEReturnWarnung_when_auswertung() {

		// Arrange

		UploadData uploadData = new UploadData("klassenliste.xlsx",
			MkGatewayFileUtils.readBytesFromClasspathPath("/upload/auswertungen/auswertung-from-excel.csv"));

		Identifier veranstalterID = new Identifier("jchuewui");
		String schulkuerzel = "ZTGFRTRE";

		PersistenterUpload persistenterUpload = new PersistenterUpload();
		persistenterUpload.setBenutzerUuid("adqgui");
		persistenterUpload.setDateiname("schule-1.csv");
		persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
		persistenterUpload.setUploadType(UploadType.KLASSENLISTE);
		persistenterUpload.setUuid(UUID.randomUUID().toString());

		when(uploadRepository.findUploadByIdentifier(any(UploadIdentifier.class)))
			.thenReturn(Optional.empty());

		when(uploadRepository.addUploadMetaData(any(PersistenterUpload.class))).thenReturn(persistenterUpload);

		doNothing().when(uploadRepository).deleteUpload(persistenterUpload.getUuid());

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
			.withUploadType(UploadType.KLASSENLISTE)
			.withUploadData(uploadData);

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmenummer(schulkuerzel)
			.withWettbewerbID(wettbewerbID);

		FileScanResult scanResult = new FileScanResult().withMediaType("text/plain").withUserID("zoazodq");

		when(uploadScannerDelegate.scanUpload(any(UploadRequestPayload.class))).thenReturn(scanResult);

		when(auswertungsmodusInfoService.ermittleAuswertungsmodusFuerTeilnahme(teilnahmeIdentifier))
			.thenReturn(Auswertungsmodus.ONLINE);

		// Act
		ResponsePayload responsePayload = uploadManager.processUpload(uploadPayload);

		// Assert
		MessagePayload messagePayload = responsePayload.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals(
			"Die Datei kann nicht verarbeitet werden. Haben Sie statt einer Klassenliste versehentlich eine Auswertungstabelle hochgeladen?",
			messagePayload.getMessage());

		verify(auswertungsmodusInfoService, never()).ermittleAuswertungsmodusFuerTeilnahme(teilnahmeIdentifier);
		verify(uploadScannerDelegate).scanUpload(any(UploadRequestPayload.class));
		verify(uploadRepository).findUploadByIdentifier(any(UploadIdentifier.class));
		verify(uploadRepository).addUploadMetaData(any(PersistenterUpload.class));
		verify(uploadRepository).deleteUpload(persistenterUpload.getUuid());
		verify(klassenlisteImportService, never()).importiereKinder(any(UploadKlassenlisteContext.class),
			any(PersistenterUpload.class), any(String.class), any());

	}

	@Test
	void should_processUploadKLASSENLISTEThrowUploadFormatException_when_eineZeileAberBlank() {

		// Arrange
		UploadData uploadData = new UploadData("klassenliste.xlsx",
			MkGatewayFileUtils.readBytesFromClasspathPath("/upload/klassenlisten/klassenliste-eine-zeile-aber-blank.csv"));

		Identifier veranstalterID = new Identifier("jchuewui");
		String schulkuerzel = "ZTGFRTRE";

		PersistenterUpload persistenterUpload = new PersistenterUpload();
		persistenterUpload.setBenutzerUuid("adqgui");
		persistenterUpload.setDateiname("schule-1.csv");
		persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
		persistenterUpload.setUploadType(UploadType.KLASSENLISTE);
		persistenterUpload.setUuid(UUID.randomUUID().toString());

		when(uploadRepository.findUploadByIdentifier(any(UploadIdentifier.class)))
			.thenReturn(Optional.empty());

		when(uploadRepository.addUploadMetaData(any(PersistenterUpload.class))).thenReturn(persistenterUpload);

		doNothing().when(uploadRepository).deleteUpload(persistenterUpload.getUuid());

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
			.withUploadType(UploadType.KLASSENLISTE)
			.withUploadData(uploadData);

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmenummer(schulkuerzel)
			.withWettbewerbID(wettbewerbID);

		FileScanResult scanResult = new FileScanResult().withMediaType("text/plain").withUserID("zoazodq");

		when(uploadScannerDelegate.scanUpload(any(UploadRequestPayload.class))).thenReturn(scanResult);

		when(auswertungsmodusInfoService.ermittleAuswertungsmodusFuerTeilnahme(teilnahmeIdentifier))
			.thenReturn(Auswertungsmodus.ONLINE);

		// Act
		try {

			uploadManager.processUpload(uploadPayload);
			fail("keine UploadFormatException");

		} catch (UploadFormatException e) {

			assertEquals(
				"Die Klassenliste konnte nicht importiert werden: sie enthält keine Kinder. Bitte prüfen Sie die hochgeladene Datei.",
				e.getMessage());
			verify(auswertungsmodusInfoService, never()).ermittleAuswertungsmodusFuerTeilnahme(teilnahmeIdentifier);
			verify(uploadScannerDelegate).scanUpload(any(UploadRequestPayload.class));
			verify(uploadRepository).findUploadByIdentifier(any(UploadIdentifier.class));
			verify(uploadRepository).addUploadMetaData(any(PersistenterUpload.class));
			verify(uploadRepository).deleteUpload(persistenterUpload.getUuid());
			verify(klassenlisteImportService, never()).importiereKinder(any(UploadKlassenlisteContext.class),
				any(PersistenterUpload.class), any(String.class), any());

		}
	}

	@Test
	void should_processUploadKLASSENLISTEThrowUploadFormatException_when_eineZeileNurUeberschrift() {

		// Arrange
		UploadData uploadData = new UploadData("klassenliste.xlsx",
			MkGatewayFileUtils.readBytesFromClasspathPath("/upload/klassenlisten/klassenliste-nur-ueberschrift.csv"));

		Identifier veranstalterID = new Identifier("jchuewui");
		String schulkuerzel = "ZTGFRTRE";

		PersistenterUpload persistenterUpload = new PersistenterUpload();
		persistenterUpload.setBenutzerUuid("adqgui");
		persistenterUpload.setDateiname("schule-1.csv");
		persistenterUpload.setStatus(UploadStatus.HOCHGELADEN);
		persistenterUpload.setUploadType(UploadType.KLASSENLISTE);
		persistenterUpload.setUuid(UUID.randomUUID().toString());

		when(uploadRepository.findUploadByIdentifier(any(UploadIdentifier.class)))
			.thenReturn(Optional.empty());

		when(uploadRepository.addUploadMetaData(any(PersistenterUpload.class))).thenReturn(persistenterUpload);

		doNothing().when(uploadRepository).deleteUpload(persistenterUpload.getUuid());

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
			.withUploadType(UploadType.KLASSENLISTE)
			.withUploadData(uploadData);

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmenummer(schulkuerzel)
			.withWettbewerbID(wettbewerbID);

		FileScanResult scanResult = new FileScanResult().withMediaType("text/plain").withUserID("zoazodq");

		when(uploadScannerDelegate.scanUpload(any(UploadRequestPayload.class))).thenReturn(scanResult);

		when(auswertungsmodusInfoService.ermittleAuswertungsmodusFuerTeilnahme(teilnahmeIdentifier))
			.thenReturn(Auswertungsmodus.ONLINE);

		// Act
		try {

			uploadManager.processUpload(uploadPayload);
			fail("keine UploadFormatException");

		} catch (UploadFormatException e) {

			assertEquals(
				"Die Klassenliste konnte nicht importiert werden: sie enthält keine Kinder. Bitte prüfen Sie die hochgeladene Datei.",
				e.getMessage());
			verify(auswertungsmodusInfoService, never()).ermittleAuswertungsmodusFuerTeilnahme(teilnahmeIdentifier);
			verify(uploadScannerDelegate).scanUpload(any(UploadRequestPayload.class));
			verify(uploadRepository).findUploadByIdentifier(any(UploadIdentifier.class));
			verify(uploadRepository).addUploadMetaData(any(PersistenterUpload.class));
			verify(uploadRepository).deleteUpload(persistenterUpload.getUuid());
			verify(klassenlisteImportService, never()).importiereKinder(any(UploadKlassenlisteContext.class),
				any(PersistenterUpload.class), any(String.class), any());

		}
	}
}
