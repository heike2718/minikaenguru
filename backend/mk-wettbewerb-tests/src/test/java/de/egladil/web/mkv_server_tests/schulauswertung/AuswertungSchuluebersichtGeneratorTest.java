// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.schulauswertung;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.urkunden.Farbschema;
import de.egladil.web.mk_gateway.domain.urkunden.SchuleUrkundenservice;
import de.egladil.web.mk_gateway.domain.urkunden.api.UrkundenauftragSchule;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AuswertungDatenRepository;
import de.egladil.web.mk_gateway.domain.urkunden.generator.uebersicht.AuswertungSchuluebersichtGenerator;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.TeilnahmenHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * AuswertungSchuluebersichtGeneratorTest
 */
public class AuswertungSchuluebersichtGeneratorTest extends AbstractIT {

	private SchuleUrkundenservice urkundenservice;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		urkundenservice = SchuleUrkundenservice.createForIntegrationTests(entityManager);

	}

	@Test
	void generateUebersicht() throws Exception {

		// Arrange
		UrkundenauftragSchule auftrag = new UrkundenauftragSchule()
			.withDateString("30.12.2020")
			.withFarbschema(Farbschema.GREEN)
			.withSchulkuerzel("G1HDI46O");

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer("G1HDI46O").withWettbewerbID(new WettbewerbID(2020));

		AuswertungDatenRepository datenRepository = urkundenservice.bereiteDatenVor(auftrag);

		Optional<Teilnahme> optTeilnahme = TeilnahmenHibernateRepository.createForIntegrationTest(entityManager)
			.ofTeilnahmeIdentifier(teilnahmeIdentifier);

		Schulteilnahme schulteilnahme = (Schulteilnahme) optTeilnahme.get();

		// Act
		byte[] daten = new AuswertungSchuluebersichtGenerator().generiereSchuluebersicht(schulteilnahme, datenRepository);

		// jetzt in Datei schreiben
		DownloadData downloadData = new DownloadData("name.pdf", daten);

		this.print(downloadData, false);

	}

}
