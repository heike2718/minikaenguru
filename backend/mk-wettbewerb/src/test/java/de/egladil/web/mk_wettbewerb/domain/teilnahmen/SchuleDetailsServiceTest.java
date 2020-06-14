// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_wettbewerb.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.apimodel.SchuleDetails;
import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.personen.SchulkollegienRepository;
import de.egladil.web.mk_wettbewerb.domain.personen.Schulkollegium;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbService;

/**
 * SchuleDetailsServiceTest
 */
public class SchuleDetailsServiceTest extends AbstractDomainServiceTest {

	private SchuleDetailsService service;

	@BeforeEach
	@Override
	protected void setUp() {

		super.setUp();
		WettbewerbService wettbewerbService = WettbewerbService.createForTest(getWettbewerbRepository());
		AktuelleTeilnahmeService aktuelleTeilnahmeService = AktuelleTeilnahmeService.createForTest(getTeilnahmenRepository(),
			wettbewerbService);

		SchulkollegienRepository schulkollegienRepository = createSchulkollegienRepo();

		service = SchuleDetailsService.createForTest(aktuelleTeilnahmeService, schulkollegienRepository, getTeilnahmenRepository(),
			getVeranstalterRepository());
	}

	/**
	 * @return
	 */
	private SchulkollegienRepository createSchulkollegienRepo() {

		return new SchulkollegienRepository() {

			@Override
			public void replaceKollegien(final List<Schulkollegium> geaenderteSchulkollegien) {

			}

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
		assertEquals("Christaschule", details.nameUrkunde());
		assertEquals("AGDFWGJOPJ", details.kuerzel());

	}

	@Test
	void should_ermittleSchuldetailsWork_when_NichAngemeldet() {

		// Act
		SchuleDetails details = service.ermittleSchuldetails(new Identifier(SCHULKUERZEL_2), new Identifier(UUID_LEHRER_2));

		// Assert
		assertNull(details.angemeldetDurch());
		assertEquals(0, details.anzahlTeilnahmen());
		assertEquals("Hans Wurst", details.kollegen());
		assertNull(details.nameUrkunde());
		assertEquals("UZTGF65FR3", details.kuerzel());

	}

	@Test
	void should_ermittleSchuldetailsWork_when_NichAngemeldetUNdKeineKollegen() {

		// Act
		SchuleDetails details = service.ermittleSchuldetails(new Identifier(SCHULKUERZEL_3), new Identifier(UUID_LEHRER_3));

		// Assert
		assertNull(details.angemeldetDurch());
		assertEquals(0, details.anzahlTeilnahmen());
		assertNull(details.kollegen());
		assertNull(details.nameUrkunde());
		assertEquals("TRF65FED7O", details.kuerzel());

	}

}
