// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.uploads;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.klassenlisten.UploadKlassenlisteContext;
import de.egladil.web.mk_gateway.domain.klassenlisten.api.KlassenlisteImportReport;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.uploads.UploadData;
import de.egladil.web.mk_gateway.domain.uploads.UploadManager;
import de.egladil.web.mk_gateway.domain.uploads.UploadRequestPayload;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.uploads.impl.UploadManagerImpl;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * UploadManagerImplIT
 */
public class UploadManagerImplIT extends AbstractIntegrationTest {

	private UploadManager uploadManager;

	private KinderRepository kinderRepository;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		uploadManager = UploadManagerImpl.createForIntegrationTests(entityManager);
		kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);

	}

	@Test
	void should_upload_work() {

		// Arrange
		String lehrerUuid = "2f09da36-07c6-4033-a2f1-5e110c804026";
		String schulkuerzel = "2EX6DENW";
		TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier = TeilnahmeIdentifierAktuellerWettbewerb
			.createForSchulteilnahme(schulkuerzel);

		byte[] data = loadData("/klassenlisten/klassenliste.csv");
		UploadData uploadData = new UploadData("Bilinguale_Grundschule_Altmark.xlsx", data);

		UploadKlassenlisteContext contextObject = new UploadKlassenlisteContext().withKuerzelLand("DE-ST")
			.withNachnameAlsZusatz(false).withSprache(Sprache.de);

		UploadRequestPayload uploadRequestPayload = new UploadRequestPayload().withContext(contextObject).withUploadData(uploadData)
			.withSchuleID(new Identifier(schulkuerzel)).withUploadType(UploadType.KLASSENLISTE)
			.withVeranstalterID(new Identifier(lehrerUuid));

		// Act
		EntityTransaction transaction = startTransaction();
		ResponsePayload result = uploadManager.processUpload(uploadRequestPayload);
		commit(transaction);

		// Assert
		MessagePayload messagePayload = result.getMessage();
		assertEquals("WARN", messagePayload.getLevel());
		assertEquals(
			"Bei einigen Kindern war die Klassenstufe nicht korrekt und wurde automatisch auf 2 gesetzt. Es gab möglicherweise Doppeleinträge. Alle betroffenen Kinder wurden markiert.",
			messagePayload.getMessage());

		KlassenlisteImportReport importReport = (KlassenlisteImportReport) result.getData();
		assertEquals(Long.valueOf(1L), Long.valueOf(importReport.getAnzahlDubletten()));
		assertEquals(10, importReport.getAnzahlKinderImportiert());
		assertEquals(3, importReport.getAnzahlKlassen());
		assertEquals(Long.valueOf(2L), importReport.getAnzahlKlassenstufeUnklar());
		assertEquals(0, importReport.getAnzahlNichtImportiert());
		List<KlasseAPIModel> klassen = importReport.getKlassen();

		assertEquals(3, klassen.size());

		{

			Optional<KlasseAPIModel> optKlasse = klassen.stream().filter(k -> "1a".equals(k.name())).findAny();
			assertTrue(optKlasse.isPresent());
			KlasseAPIModel klasse = optKlasse.get();
			assertEquals(3, klasse.anzahlKinder());
			assertEquals(schulkuerzel, klasse.schulkuerzel());
			assertEquals(0, klasse.anzahlLoesungszettel());

		}

		{

			Optional<KlasseAPIModel> optKlasse = klassen.stream().filter(k -> "2a".equals(k.name())).findAny();
			assertTrue(optKlasse.isPresent());
			KlasseAPIModel klasse = optKlasse.get();
			assertEquals(3, klasse.anzahlKinder());
			assertEquals(schulkuerzel, klasse.schulkuerzel());
			assertEquals(0, klasse.anzahlLoesungszettel());

		}

		{

			Optional<KlasseAPIModel> optKlasse = klassen.stream().filter(k -> "2b".equals(k.name())).findAny();
			assertTrue(optKlasse.isPresent());
			KlasseAPIModel klasse = optKlasse.get();
			assertEquals(4, klasse.anzahlKinder());
			assertEquals(schulkuerzel, klasse.schulkuerzel());
			assertEquals(0, klasse.anzahlLoesungszettel());

		}

		List<Kind> kinder = kinderRepository.withTeilnahme(teilnahmeIdentifier);
		assertEquals(10, kinder.size());
	}

	/**
	 * @param  string
	 * @return
	 */
	private byte[] loadData(final String classpathData) {

		try (InputStream in = getClass().getResourceAsStream(classpathData)) {

			return in.readAllBytes();
		} catch (IOException e) {

			e.printStackTrace();
			throw new RuntimeException("Test nicht möglich: " + e.getMessage());
		}
	}

}
