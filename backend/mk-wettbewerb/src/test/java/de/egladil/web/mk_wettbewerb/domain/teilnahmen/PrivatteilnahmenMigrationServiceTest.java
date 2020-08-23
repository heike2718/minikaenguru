// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.personen.Privatperson;
import de.egladil.web.mk_wettbewerb.domain.personen.Veranstalter;
import de.egladil.web.mk_wettbewerb.domain.personen.VeranstalterRepository;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.TemporaerePersistentePrivatteilnahme;

/**
 * PrivatteilnahmenMigrationServiceTest
 */
public class PrivatteilnahmenMigrationServiceTest {

	@Test
	void should_importieren_LogWarn_when_veranstalterNotPresent() {

		// Arrange
		PrivatteilnahmenMigrationService service = new PrivatteilnahmenMigrationService();

		List<TemporaerePersistentePrivatteilnahme> temporaereTeilnahmen = new ArrayList<>();
		List<Veranstalter> veranstalter = new ArrayList<>();
		List<Teilnahme> vorhandenePrivatteilnahmen = new ArrayList<>();

		temporaereTeilnahmen.add(TemporaerePersistentePrivatteilnahme.createForTest(1l,
			"veranstalterUuid", "teilnahmenummerAlt", "2017"));

		// Act
		service.importieren(temporaereTeilnahmen, veranstalter, vorhandenePrivatteilnahmen);

	}

	@Test
	void should_importieren_LogWarn_when_veranstalterOhneTeilnahmenummer() {

		// Arrange
		PrivatteilnahmenMigrationService service = new PrivatteilnahmenMigrationService();

		List<TemporaerePersistentePrivatteilnahme> temporaereTeilnahmen = new ArrayList<>();
		List<Veranstalter> veranstalter = new ArrayList<>();
		List<Teilnahme> vorhandenePrivatteilnahmen = new ArrayList<>();

		temporaereTeilnahmen.add(TemporaerePersistentePrivatteilnahme.createForTest(1l,
			"veranstalterUuid", "teilnahmenummerAlt", "2017"));

		veranstalter.add(new Privatperson(new Person("veranstalterUuid", "Heinz"), false, new ArrayList<>()));

		// Act
		service.importieren(temporaereTeilnahmen, veranstalter, vorhandenePrivatteilnahmen);

	}

	@Test
	void should_importieren_LogWarn_when_veranstalterMitMehrAlsEinerTeilnahmenummer() {

		// Arrange
		PrivatteilnahmenMigrationService service = new PrivatteilnahmenMigrationService();

		List<TemporaerePersistentePrivatteilnahme> temporaereTeilnahmen = new ArrayList<>();
		List<Veranstalter> veranstalter = new ArrayList<>();
		List<Teilnahme> vorhandenePrivatteilnahmen = new ArrayList<>();

		temporaereTeilnahmen.add(TemporaerePersistentePrivatteilnahme.createForTest(1l,
			"veranstalterUuid", "teilnahmenummerAlt", "2017"));

		List<Identifier> teilnahmenummern = Arrays.asList(new Identifier[] { new Identifier("asdgq"), new Identifier("ashguigq") });
		veranstalter.add(new Privatperson(new Person("veranstalterUuid", "Heinz"), false, teilnahmenummern));

		// Act
		service.importieren(temporaereTeilnahmen, veranstalter, vorhandenePrivatteilnahmen);

	}

	@Test
	void should_importieren_LogWarn_when_teilnahmeBereitsVorhanden() {

		// Arrange
		PrivatteilnahmenMigrationService service = new PrivatteilnahmenMigrationService();

		List<TemporaerePersistentePrivatteilnahme> temporaereTeilnahmen = new ArrayList<>();
		List<Veranstalter> veranstalter = new ArrayList<>();
		List<Teilnahme> vorhandenePrivatteilnahmen = new ArrayList<>();

		temporaereTeilnahmen.add(TemporaerePersistentePrivatteilnahme.createForTest(1l,
			"veranstalterUuid", "teilnahmenummerAlt", "2017"));

		List<Identifier> teilnahmenummern = Arrays.asList(new Identifier[] { new Identifier("teilnahmenummerNeu") });
		veranstalter.add(new Privatperson(new Person("veranstalterUuid", "Heinz"), false, teilnahmenummern));

		vorhandenePrivatteilnahmen.add(new Privatteilnahme(new WettbewerbID(2017), new Identifier("teilnahmenummerNeu")));

		// Act
		service.importieren(temporaereTeilnahmen, veranstalter, vorhandenePrivatteilnahmen);

	}

	@Test
	void should_importieren_work_when_allesOk() {

		// Arrange
		VeranstalterRepository veranstalterRepository = Mockito.mock(VeranstalterRepository.class);
		TeilnahmenRepository teilnahmenRepository = Mockito.mock(TeilnahmenRepository.class);

		PrivatteilnahmenMigrationService service = PrivatteilnahmenMigrationService.createForTest(teilnahmenRepository,
			veranstalterRepository);

		List<TemporaerePersistentePrivatteilnahme> temporaereTeilnahmen = new ArrayList<>();
		List<Veranstalter> veranstalter = new ArrayList<>();
		List<Teilnahme> vorhandenePrivatteilnahmen = new ArrayList<>();

		TemporaerePersistentePrivatteilnahme theTemporaereTeilnahme = TemporaerePersistentePrivatteilnahme.createForTest(1l,
			"veranstalterUuid", "teilnahmenummerAlt", "2017");
		temporaereTeilnahmen.add(theTemporaereTeilnahme);

		Mockito.when(teilnahmenRepository.save(theTemporaereTeilnahme)).thenReturn(theTemporaereTeilnahme);

		List<Identifier> teilnahmenummern = Arrays.asList(new Identifier[] { new Identifier("teilnahmenummerNeu") });
		veranstalter.add(new Privatperson(new Person("veranstalterUuid", "Heinz"), false, teilnahmenummern));

		vorhandenePrivatteilnahmen.add(new Privatteilnahme(new WettbewerbID(2016), new Identifier("teilnahmenummerNeu")));

		Privatteilnahme neuePrivatteilnahme = new Privatteilnahme(new WettbewerbID(2017), new Identifier("teilnahmenummerNeu"));

		Mockito.when(teilnahmenRepository.addTeilnahme(neuePrivatteilnahme)).thenReturn(Boolean.TRUE);

		// Act
		service.importieren(temporaereTeilnahmen, veranstalter, vorhandenePrivatteilnahmen);

		// Assert
		Mockito.verify(teilnahmenRepository, Mockito.times(1)).addTeilnahme(neuePrivatteilnahme);
		Mockito.verify(teilnahmenRepository, Mockito.times(1)).save(theTemporaereTeilnahme);

	}

}
