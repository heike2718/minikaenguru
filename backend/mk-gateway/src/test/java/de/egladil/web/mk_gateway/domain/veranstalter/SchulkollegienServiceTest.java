// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.SchulkollegienHibernateRepository;

/**
 * SchulkollegienServiceTest
 */
public class SchulkollegienServiceTest {

	private static final String SCHULKUERZEL_1 = "VHKFKFFZFZ";

	private static final String SCHULKUERZEL_2 = "KUZHGTRFTV";

	private static final String UUID_LEHRER = "kslawhdqh";

	private SchulkollegienRepository repository;

	private Map<String, Schulkollegium> schulkollegienMap;

	private SchulkollegienService service;

	@BeforeEach
	void setUp() {

		schulkollegienMap = new HashMap<>();

		Person[] personen = new Person[1];
		personen[0] = new Person(UUID_LEHRER, "Professor Proton");
		Schulkollegium kollegium = new Schulkollegium(new Identifier(SCHULKUERZEL_1), personen);

		schulkollegienMap.put(SCHULKUERZEL_1, kollegium);
		schulkollegienMap.put(SCHULKUERZEL_2, kollegium);

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

		};

		service = SchulkollegienService.createForTest(repository);
	}

	@Test
	void should_HandleSchuleLehrerAddedCreateAndPersistNewKollegium_when_SchuleUnknown() {

		// Arrange
		String neuesSchulkuerzel = "JLKHDRSTDTU";
		String uuidLehrer = "sjqhhih";
		String fullName = "Hans Wurst";
		Identifier identifier = new Identifier(neuesSchulkuerzel);

		Person person = new Person(uuidLehrer, fullName);

		LehrerChanged event = new LehrerChanged(person, "", neuesSchulkuerzel, false);

		// Act
		service.handleLehrerChanged(event);

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

		assertEquals("[{\"uuid\":\"sjqhhih\",\"fullName\":\"Hans Wurst\",\"email\":null}]", neues.personenAlsJSON());

	}

	@Test
	void should_HandleSchuleLehrerAddedChangeExisting_when_SchuleKnown() {

		// Arrange
		String uuidLehrer = "sjqhhih";
		String fullName = "Hans Wurst";
		Identifier identifier = new Identifier(SCHULKUERZEL_1);

		Person person = new Person(uuidLehrer, fullName);

		LehrerChanged event = new LehrerChanged(person, "", SCHULKUERZEL_1, false);

		// Act
		service.handleLehrerChanged(event);

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
			"[{\"uuid\":\"kslawhdqh\",\"fullName\":\"Professor Proton\",\"email\":null},{\"uuid\":\"sjqhhih\",\"fullName\":\"Hans Wurst\",\"email\":null}]",
			neues.personenAlsJSON());

	}

	@Test
	void should_HandleSchulenLehrerAddedChangeExisting_when_moreThanOneKuerzel() {

		// Arrange
		String uuidLehrer = "sjqhhih";
		String fullName = "Hans Wurst";
		Identifier identifier1 = new Identifier(SCHULKUERZEL_1);
		Identifier identifier2 = new Identifier(SCHULKUERZEL_2);

		Person person = new Person(uuidLehrer, fullName);

		LehrerChanged event = new LehrerChanged(person, "", SCHULKUERZEL_1 + "," + SCHULKUERZEL_2, false);

		// Act
		service.handleLehrerChanged(event);

		// Assert
		{

			Optional<Schulkollegium> opt = repository.ofSchulkuerzel(identifier1);
			assertTrue(opt.isPresent());

			Schulkollegium neues = opt.get();
			assertEquals(identifier1, neues.schulkuerzel());
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
				"[{\"uuid\":\"kslawhdqh\",\"fullName\":\"Professor Proton\",\"email\":null},{\"uuid\":\"sjqhhih\",\"fullName\":\"Hans Wurst\",\"email\":null}]",
				neues.personenAlsJSON());
		}

		{

			Optional<Schulkollegium> opt = repository.ofSchulkuerzel(identifier2);
			assertTrue(opt.isPresent());

			Schulkollegium neues = opt.get();
			assertEquals(identifier2, neues.schulkuerzel());
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
				"[{\"uuid\":\"kslawhdqh\",\"fullName\":\"Professor Proton\",\"email\":null},{\"uuid\":\"sjqhhih\",\"fullName\":\"Hans Wurst\",\"email\":null}]",
				neues.personenAlsJSON());
		}

	}

	@Nested
	class SchulkuerzelDefaultSettingTest {

		final String alteSchulkuerzel = "Z5,B3,A1,Q7";

		final String neueSchulkuerzel = "B2,Z5,D1,Q7";

		// unchanged: Q7, Z5
		// registered: B2, D1
		// deregistered: A1, B3

		@Test
		void should_getDeregisterList_work_inDefaultSetting() {

			// Arrange

			List<Identifier> alteSchulen = Arrays.stream(alteSchulkuerzel.split(",")).map(k -> new Identifier(k))
				.collect(Collectors.toList());

			List<Identifier> neueSchulen = Arrays.stream(neueSchulkuerzel.split(",")).map(k -> new Identifier(k))
				.collect(Collectors.toList());

			List<Identifier> expected = Arrays.asList(new Identifier[] { new Identifier("A1"), new Identifier("B3") });

			// Act
			List<Identifier> actual = service.getDeregisterList(alteSchulen, neueSchulen);

			// Assert
			assertEquals(expected.size(), actual.size());
			assertEquals(expected.get(0), actual.get(0));
			assertEquals(expected.get(1), actual.get(1));
		}

		@Test
		void should_getRegisterList_work_inDefaultSetting() {

			// Arrange

			List<Identifier> alteSchulen = Arrays.stream(alteSchulkuerzel.split(",")).map(k -> new Identifier(k))
				.collect(Collectors.toList());

			List<Identifier> neueSchulen = Arrays.stream(neueSchulkuerzel.split(",")).map(k -> new Identifier(k))
				.collect(Collectors.toList());

			List<Identifier> expected = Arrays.asList(new Identifier[] { new Identifier("B2"), new Identifier("D1") });

			// Act
			List<Identifier> actual = service.getRegisterList(alteSchulen, neueSchulen);

			// Assert
			assertEquals(expected.size(), actual.size());
			assertEquals(expected.get(0), actual.get(0));
			assertEquals(expected.get(1), actual.get(1));
		}

		@Test
		void should_getUnchangedList_work_inDefaultSetting() {

			// Arrange

			List<Identifier> alteSchulen = Arrays.stream(alteSchulkuerzel.split(",")).map(k -> new Identifier(k))
				.collect(Collectors.toList());

			List<Identifier> neueSchulen = Arrays.stream(neueSchulkuerzel.split(",")).map(k -> new Identifier(k))
				.collect(Collectors.toList());

			List<Identifier> expected = Arrays.asList(new Identifier[] { new Identifier("Q7"), new Identifier("Z5") });

			// Act
			List<Identifier> actual = service.getUnchangedList(alteSchulen, neueSchulen);

			// Assert
			assertEquals(expected.size(), actual.size());
			assertEquals(expected.get(0), actual.get(0));
			assertEquals(expected.get(1), actual.get(1));
		}
	}

	@Nested
	class SchulkuerzelAlteSchulenLeerTest {

		final List<Identifier> alteSchulen = new ArrayList<>();

		final List<Identifier> neueSchulen = Arrays.stream("B2,Z5,D1,Q7".split(",")).map(k -> new Identifier(k))
			.collect(Collectors.toList());

		// unchanged: leer
		// registered: B2, D1, Q7, Z5
		// deregistered: leer

		@Test
		void should_getDeregisterListWork_when_neuerLehrer() {

			// Act
			List<Identifier> actual = service.getDeregisterList(alteSchulen, neueSchulen);

			// Assert
			assertEquals(0, actual.size());
		}

		@Test
		void should_getRegisterListWork_when_neuerLehrer() {

			// Arrange
			List<Identifier> expected = Arrays.asList(
				new Identifier[] { new Identifier("B2"), new Identifier("D1"), new Identifier("Q7"), new Identifier("Z5") });

			// Act
			List<Identifier> actual = service.getRegisterList(alteSchulen, neueSchulen);

			// Assert
			assertEquals(expected.size(), actual.size());
			assertEquals(expected.get(0), actual.get(0));
			assertEquals(expected.get(1), actual.get(1));
			assertEquals(expected.get(2), actual.get(2));
			assertEquals(expected.get(3), actual.get(3));
		}

		@Test
		void should_getUnchanedListWork_when_neuerLehrer() {

			// Act
			List<Identifier> actual = service.getUnchangedList(alteSchulen, neueSchulen);

			// Assert
			assertEquals(0, actual.size());
		}
	}

}
