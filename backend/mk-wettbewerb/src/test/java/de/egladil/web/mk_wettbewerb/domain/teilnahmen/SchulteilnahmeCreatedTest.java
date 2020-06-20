// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_wettbewerb.domain.teilnahmen.SchulteilnahmeCreated;

/**
 * SchulteilnahmeCreatedTest
 */
public class SchulteilnahmeCreatedTest {

	@Test
	void should_ConstructorSetAllAttributes_and_SerializationBeOk() throws JsonProcessingException {

		// Arrange
		Integer wettbewerbsjahr = Integer.valueOf(2020);
		String teilnahmenummer = "sahgfqggqu";
		String createdBy = "xjkcgsjdgfw";
		String schulname = "Baumschule";

		// Act
		SchulteilnahmeCreated event = new SchulteilnahmeCreated(wettbewerbsjahr, teilnahmenummer, schulname, createdBy);

		String serialization = new ObjectMapper().writeValueAsString(event);

		// System.out.println(serialization);

		// Assert
		assertEquals(
			"{\"wettbewerbsjahr\":2020,\"teilnahmenummer\":\"sahgfqggqu\",\"triggeringUser\":\"xjkcgsjdgfw\",\"schulname\":\"Baumschule\"}",
			serialization);
		assertNotNull(event.occuredOn());
		assertEquals(createdBy, event.triggeringUser());
		assertEquals(wettbewerbsjahr, event.wettbewerbsjahr());
		assertEquals(teilnahmenummer, event.teilnahmenummer());
		assertEquals(schulname, event.schulname());
		assertEquals("SchulteilnahmeCreated", event.typeName());
	}

	@Test
	void should_ContructorThrowException_when_SchulnameNull() {

		// Arrange
		Integer wettbewerbsjahr = Integer.valueOf(2020);
		String teilnahmenummer = "bsjfga";
		String createdBy = "asjkkgqg";
		String schulname = null;

		// Act
		try {

			new SchulteilnahmeCreated(wettbewerbsjahr, teilnahmenummer, schulname, createdBy);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("schulname darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ContructorThrowException_when_SchulnameBlank() {

		// Arrange
		Integer wettbewerbsjahr = Integer.valueOf(2020);
		String teilnahmenummer = "bsjfga";
		String createdBy = "asjkkgqg";
		String schulname = "  ";

		// Act
		try {

			new SchulteilnahmeCreated(wettbewerbsjahr, teilnahmenummer, schulname, createdBy);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("schulname darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ContructorThrowException_when_CreatedByNull() {

		// Arrange
		Integer wettbewerbsjahr = Integer.valueOf(2020);
		String teilnahmenummer = "bsjfga";
		String createdBy = null;
		String schulname = "Baumschule";

		// Act
		try {

			new SchulteilnahmeCreated(wettbewerbsjahr, teilnahmenummer, schulname, createdBy);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("triggeringUser darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ContructorThrowException_when_CreatedByBlank() {

		// Arrange
		Integer wettbewerbsjahr = Integer.valueOf(2020);
		String teilnahmenummer = "bsjfga";
		String createdBy = " ";
		String schulname = "Baumschule";

		// Act
		try {

			new SchulteilnahmeCreated(wettbewerbsjahr, teilnahmenummer, schulname, createdBy);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("triggeringUser darf nicht blank sein.", e.getMessage());
		}
	}
}
