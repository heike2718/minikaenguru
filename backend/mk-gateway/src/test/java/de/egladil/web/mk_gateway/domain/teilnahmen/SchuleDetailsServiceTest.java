// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.adv.VertragAuftragsdatenverarbeitung;
import de.egladil.web.mk_gateway.domain.adv.VertragAuftragsverarbeitungRepository;
import de.egladil.web.mk_gateway.domain.adv.Vertragstext;
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.SchuleDetails;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.SchulkollegienRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.Schulkollegium;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * SchuleDetailsServiceTest
 */
public class SchuleDetailsServiceTest extends AbstractDomainServiceTest {

	private SchuleDetailsService service;

	@BeforeEach
	@Override
	protected void setUp() {

		super.setUp();
		WettbewerbService wettbewerbService = WettbewerbService.createForTest(getMockitoBasedWettbewerbRepository());
		AktuelleTeilnahmeService aktuelleTeilnahmeService = AktuelleTeilnahmeService.createForTest(getTeilnahmenRepository(),
			wettbewerbService, getVeranstalterRepository());

		SchulkollegienRepository schulkollegienRepository = createSchulkollegienRepo();

		VertragAuftragsverarbeitungRepository advRepository = createAdvRepository();

		service = SchuleDetailsService.createForTest(aktuelleTeilnahmeService, schulkollegienRepository, getTeilnahmenRepository(),
			getVeranstalterRepository(), advRepository);
	}

	/**
	 * @return
	 */
	private VertragAuftragsverarbeitungRepository createAdvRepository() {

		return new VertragAuftragsverarbeitungRepository() {

			private static final String ADV_UUID_SCHULE_1 = "guqgwugdq";

			private static final String ADVTEXT_CHECKSUMME = "aslhkah";

			private static final String ADVTEXT_VERSIONSNUMMER = "1.1";

			private static final String ADVTEXT_DATEINAME = "adv-vereinbarung-1.1.pdf";

			@Override
			public Optional<VertragAuftragsdatenverarbeitung> ofUuid(final String uuid) {

				if (ADV_UUID_SCHULE_1.equals(uuid)) {

					Vertragstext vertragstext = new Vertragstext().withChecksumme(ADVTEXT_CHECKSUMME)
						.withDateiname(ADVTEXT_DATEINAME)
						.withIdentifier(new Identifier(ADV_UUID_SCHULE_1)).withVersionsnummer(ADVTEXT_VERSIONSNUMMER);
					VertragAuftragsdatenverarbeitung result = new VertragAuftragsdatenverarbeitung()
						.withIdentifier(new Identifier(""))
						.withSchulkuerzel(new Identifier(SCHULKUERZEL_1)).withUnterzeichnenderLehrer(new Identifier(UUID_LEHRER_1))
						.withUnterzeichnetAm("14.08.2020").withVertragstext(vertragstext.identifier());

					return Optional.of(result);
				}

				return Optional.empty();
			}

			@Override
			public Optional<VertragAuftragsdatenverarbeitung> findVertragForSchule(final Identifier schuleIdentity) {

				Vertragstext vertragstext = new Vertragstext().withChecksumme(ADVTEXT_CHECKSUMME).withDateiname(ADVTEXT_DATEINAME)
					.withIdentifier(new Identifier(ADV_UUID_SCHULE_1)).withVersionsnummer(ADVTEXT_VERSIONSNUMMER);
				VertragAuftragsdatenverarbeitung result = new VertragAuftragsdatenverarbeitung()
					.withIdentifier(new Identifier(ADV_UUID_SCHULE_1))
					.withSchulkuerzel(schuleIdentity).withUnterzeichnenderLehrer(new Identifier(UUID_LEHRER_1))
					.withUnterzeichnetAm("14.08.2020").withVertragstext(vertragstext.identifier());

				return SCHULKUERZEL_1.equals(schuleIdentity.identifier()) ? Optional.of(result) : Optional.empty();
			}

			@Override
			public Identifier addVertrag(final VertragAuftragsdatenverarbeitung vertrag) {

				return new Identifier(UUID.randomUUID().toString());
			}
		};
	}

	/**
	 * @return
	 */
	private SchulkollegienRepository createSchulkollegienRepo() {

		return new SchulkollegienRepository() {

			@Override
			public void replaceKollegen(final Schulkollegium schulkollegium) {

			}

			@Override
			public Optional<Schulkollegium> ofSchulkuerzel(final Identifier identifier) {

				if (identifier.equals(new Identifier(SCHULKUERZEL_1))) {

					Person[] personen = new Person[] { new Person(UUID_LEHRER_1, "Hans Wurst"),
						new Person(UUID_LEHRER_2, "Olle Keule") };
					Schulkollegium schulkollegium = new Schulkollegium(identifier, personen);
					return Optional.of(schulkollegium);
				}

				if (identifier.equals(new Identifier(SCHULKUERZEL_2))) {

					Person[] personen = new Person[] { new Person(UUID_LEHRER_1, "Hans Wurst") };
					Schulkollegium schulkollegium = new Schulkollegium(identifier, personen);
					return Optional.of(schulkollegium);
				}

				return Optional.empty();
			}

			@Override
			public void addKollegium(final Schulkollegium schulkollegium) {

			}

			@Override
			public void deleteKollegium(final Schulkollegium kollegium) {

			}
		};
	}

	@Test
	void should_ermittleSchuldetailsWork_when_angemeldet() {

		// Act
		SchuleDetails details = service.ermittleSchuldetails(new Identifier(SCHULKUERZEL_1), new Identifier(UUID_LEHRER_1));

		// Assert
		assertEquals("Hans Wurst", details.angemeldetDurch());
		assertEquals(1, details.anzahlTeilnahmen());
		assertEquals("Olle Keule", details.kollegen());
		assertEquals("SCHULKUERZEL_1", details.kuerzel());
		assertNotNull(details.aktuelleTeilnahme());

	}

	@Test
	void should_ermittleSchuldetailsWork_when_NichAngemeldet() {

		// Act
		SchuleDetails details = service.ermittleSchuldetails(new Identifier(SCHULKUERZEL_2), new Identifier(UUID_LEHRER_2));

		// Assert
		assertNull(details.angemeldetDurch());
		assertEquals(0, details.anzahlTeilnahmen());
		assertEquals("Hans Wurst", details.kollegen());
		assertEquals("SCHULKUERZEL_2", details.kuerzel());
		assertNull(details.aktuelleTeilnahme());

	}

	@Test
	void should_ermittleSchuldetailsWork_when_NichAngemeldetUndKeineKollegen() {

		// Act
		SchuleDetails details = service.ermittleSchuldetails(new Identifier(SCHULKUERZEL_3), new Identifier(UUID_LEHRER_3));

		// Assert
		assertNull(details.angemeldetDurch());
		assertEquals(0, details.anzahlTeilnahmen());
		assertNull(details.kollegen());
		assertEquals("SCHULKUERZEL_3", details.kuerzel());
		assertNull(details.aktuelleTeilnahme());
	}

}
