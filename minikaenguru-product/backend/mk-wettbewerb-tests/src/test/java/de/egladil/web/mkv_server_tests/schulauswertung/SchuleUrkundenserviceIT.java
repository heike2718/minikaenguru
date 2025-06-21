// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.schulauswertung;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
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
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.TeilnahmenHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * SchuleUrkundenserviceIT
 */
public class SchuleUrkundenserviceIT extends AbstractIntegrationTest {

	private SchuleUrkundenservice urkundenservice;

	private LoesungszettelRepository loesungszettelRepository;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		urkundenservice = SchuleUrkundenservice.createForIntegrationTests(entityManager);
		loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManager);

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

		List<Loesungszettel> alleLoesungszettel = loesungszettelRepository.loadAll(teilnahmeIdentifier);

		Optional<Teilnahme> optTeilnahme = TeilnahmenHibernateRepository.createForIntegrationTest(entityManager)
			.ofTeilnahmeIdentifier(teilnahmeIdentifier);

		Schulteilnahme schulteilnahme = (Schulteilnahme) optTeilnahme.get();

		AuswertungDatenRepository datenRepository = urkundenservice.bereiteDatenVor(schulteilnahme, auftrag, alleLoesungszettel);

		// Act
		byte[] daten = new AuswertungSchuluebersichtGenerator().generiereSchuluebersicht(schulteilnahme, datenRepository);

		// jetzt in Datei schreiben
		DownloadData downloadData = new DownloadData("name.pdf", daten);

		this.print(downloadData, true);

	}

	@Test
	void should_generateAll() throws Exception {

		// Arrange
		UrkundenauftragSchule urkundenauftrag = new UrkundenauftragSchule()
			.withDateString("30.12.2020")
			.withFarbschema(Farbschema.ORANGE)
			.withSchulkuerzel("KI7PLSUB");

		Identifier veranstalterID = new Identifier("412b67dc-132f-465a-a3c3-468269e866cb");

		// Act
		DownloadData downloadData = urkundenservice.generiereSchulauswertung(urkundenauftrag, veranstalterID);

		this.print(downloadData, false);

	}

}
