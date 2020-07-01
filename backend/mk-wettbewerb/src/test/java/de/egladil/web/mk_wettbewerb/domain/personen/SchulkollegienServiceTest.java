// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.personen.LehrerRegisteredForSchule;
import de.egladil.web.mk_wettbewerb.domain.personen.SchulkollegienRepository;
import de.egladil.web.mk_wettbewerb.domain.personen.SchulkollegienService;
import de.egladil.web.mk_wettbewerb.domain.personen.Schulkollegium;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.impl.SchulkollegienHibernateRepository;

/**
 * SchulkollegienServiceTest
 */
public class SchulkollegienServiceTest {

	private static final String SCHULKUERZEL = "VHKFKFFZFZ";

	private static final String UUID_LEHRER = "kslawhdqh";

	private SchulkollegienRepository repository;

	private Map<String, Schulkollegium> schulkollegienMap;

	private SchulkollegienService service;

	@BeforeEach
	void setUp() {

		schulkollegienMap = new HashMap<>();

		Person[] personen = new Person[1];
		personen[0] = new Person(UUID_LEHRER, "Professor Proton");
		Schulkollegium kollegium = new Schulkollegium(new Identifier(SCHULKUERZEL), personen);

		schulkollegienMap.put(SCHULKUERZEL, kollegium);

		repository = new SchulkollegienHibernateRepository() {

			@Override
			public Optional<Schulkollegium> ofSchulkuerzel(final Identifier identifier) {

				Schulkollegium treffer = schulkollegienMap.get(identifier.identifier());

				return treffer != null ? Optional.of(treffer) : Optional.empty();
			}

			@Override
			public void addKollegium(final Schulkollegium schulkollegium) {

				schulkollegienMap.put(schulkollegium.schulkuerzel().identifier(), schulkollegium);
			}

			@Override
			public void replaceKollegen(final Schulkollegium schulkollegium) {

				schulkollegienMap.put(schulkollegium.schulkuerzel().identifier(), schulkollegium);
			}

			@Override
			public void replaceKollegien(final List<Schulkollegium> geaenderteSchulkollegien) {

				//
			}

		};

		service = SchulkollegienService.createForTest(repository);
	}

	@Test
	void should_HandleSchuleLehrerAddedCreateAndPersistNewKollegium_when_SchuleUnknown() {

		// Arrange
		String schulkuerzel = "JLKHDRSTDTU";
		String uuidLehrer = "sjqhhih";
		String fullName = "Hans Wurst";
		Identifier identifier = new Identifier(schulkuerzel);

		Person person = new Person(uuidLehrer, fullName);

		LehrerRegisteredForSchule event = new LehrerRegisteredForSchule(person, schulkuerzel);

		// Act
		service.handleSchuleLehrerAdded(event);

		// Assert
		Optional<Schulkollegium> opt = repository.ofSchulkuerzel(identifier);
		assertTrue(opt.isPresent());

		Schulkollegium neues = opt.get();
		assertEquals(identifier, neues.schulkuerzel());
		List<Person> alleLehrer = neues.alleLehrerUnmodifiable();
		assertEquals(1, alleLehrer.size());
		Person derLehrer = alleLehrer.get(0);
		assertEquals(uuidLehrer, derLehrer.uuid());
		assertEquals(fullName, derLehrer.fullName());

		assertEquals("[{\"uuid\":\"sjqhhih\",\"fullName\":\"Hans Wurst\"}]", neues.personenAlsJSON());

	}

	@Test
	void should_HandleSchuleLehrerAddedChangeExisting_when_SchuleKnown() {

		// Arrange
		String uuidLehrer = "sjqhhih";
		String fullName = "Hans Wurst";
		Identifier identifier = new Identifier(SCHULKUERZEL);

		Person person = new Person(uuidLehrer, fullName);

		LehrerRegisteredForSchule event = new LehrerRegisteredForSchule(person, SCHULKUERZEL);

		// Act
		service.handleSchuleLehrerAdded(event);

		// Assert
		Optional<Schulkollegium> opt = repository.ofSchulkuerzel(identifier);
		assertTrue(opt.isPresent());

		Schulkollegium neues = opt.get();
		assertEquals(identifier, neues.schulkuerzel());
		List<Person> alleLehrer = neues.alleLehrerUnmodifiable();
		assertEquals(2, alleLehrer.size());

		{

			Person derLehrer = alleLehrer.stream().filter(p -> uuidLehrer.equals(p.uuid())).findFirst().get();
			assertEquals(uuidLehrer, derLehrer.uuid());
			assertEquals(fullName, derLehrer.fullName());
		}

		{

			Person derLehrer = alleLehrer.stream().filter(p -> UUID_LEHRER.equals(p.uuid())).findFirst().get();
			assertEquals(UUID_LEHRER, derLehrer.uuid());
			assertEquals("Professor Proton", derLehrer.fullName());
		}

		assertEquals(
			"[{\"uuid\":\"kslawhdqh\",\"fullName\":\"Professor Proton\"},{\"uuid\":\"sjqhhih\",\"fullName\":\"Hans Wurst\"}]",
			neues.personenAlsJSON());

	}

}
