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

/**
 * PrivatteilnahmeCreatedTest
 */
public class PrivatteilnahmeCreatedTest {

	@Test
	void should_ConstructorSetAllAttributes_and_SerializationBeOk() throws JsonProcessingException {

		// Arrange
		Integer wettbewerbsjahr = Integer.valueOf(2020);
		String teilnahmenummer = "sahgfqggqu";
		String createdBy = "xjkcgsjdgfw";

		// Act
		PrivatteilnahmeCreated event = new PrivatteilnahmeCreated(wettbewerbsjahr, teilnahmenummer, createdBy);

		String serialization = new ObjectMapper().writeValueAsString(event);

		// System.out.println(serialization);

		// Assert
		assertEquals("{\"wettbewerbsjahr\":2020,\"teilnahmenummer\":\"sahgfqggqu\",\"triggeringUser\":\"xjkcgsjdgfw\"}",
			serialization);
		assertNotNull(event.occuredOn());
		assertEquals(createdBy, event.triggeringUser());
		assertEquals(wettbewerbsjahr, event.wettbewerbsjahr());
		assertEquals(teilnahmenummer, event.teilnahmenummer());
		assertEquals("PrivatteilnahmeCreated", event.typeName());
	}

	@Test
	void should_ContructorThrowException_when_TeilnahmenummerNull() {

		// Arrange
		Integer wettbewerbsjahr = Integer.valueOf(2020);
		String teilnahmenummer = null;
		String createdBy = "dhsjhw";

		// Act
		try {

			new PrivatteilnahmeCreated(wettbewerbsjahr, teilnahmenummer, createdBy);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("teilnahmenummer darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ContructorThrowException_when_TeilnahmenummerBlank() {

		// Arrange
		Integer wettbewerbsjahr = Integer.valueOf(2020);
		String teilnahmenummer = "   ";
		String createdBy = "dhsjhw";

		// Act
		try {

			new PrivatteilnahmeCreated(wettbewerbsjahr, teilnahmenummer, createdBy);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("teilnahmenummer darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ContructorThrowException_when_CreatedByNull() {

		// Arrange
		Integer wettbewerbsjahr = Integer.valueOf(2020);
		String teilnahmenummer = "bsjfga";
		String createdBy = null;

		// Act
		try {

			new PrivatteilnahmeCreated(wettbewerbsjahr, teilnahmenummer, createdBy);
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
		String createdBy = "";

		// Act
		try {

			new PrivatteilnahmeCreated(wettbewerbsjahr, teilnahmenummer, createdBy);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("triggeringUser darf nicht blank sein.", e.getMessage());
		}

	}

	@Test
	void should_ContructorThrowException_when_WettbewerbsjahrNull() {

		// Arrange
		Integer wettbewerbsjahr = null;
		String teilnahmenummer = "bsjfga";
		String createdBy = "avhsfguq";

		// Act
		try {

			new PrivatteilnahmeCreated(wettbewerbsjahr, teilnahmenummer, createdBy);
			fail("keine IllegalArgumentException");
		} catch (IllegalArgumentException e) {

			assertEquals("wettbewerbsjahr darf nicht null sein.", e.getMessage());
		}

	}

}
