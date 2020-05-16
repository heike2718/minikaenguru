// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * SchulteilnahmeChangedTest
 */
public class SchulteilnahmeChangedTest {

	@Test
	void should_ConstructorSetAllAttributes_and_SerializationBeOk() throws JsonProcessingException {

		// Arrange
		Integer wettbewerbsjahr = Integer.valueOf(2020);
		String teilnahmenummer = "sahgfqggqu";
		String changedBy = "xjkcgsjdgfw";
		String schulname = "Baumschule";

		// Act
		SchulteilnahmeChanged event = new SchulteilnahmeChanged(wettbewerbsjahr, teilnahmenummer, schulname, changedBy);

		String serialization = new ObjectMapper().writeValueAsString(event);

		// System.out.println(serialization);

		// Assert
		assertEquals(
			"{\"wettbewerbsjahr\":2020,\"teilnahmenummer\":\"sahgfqggqu\",\"triggeringUser\":\"xjkcgsjdgfw\",\"schulname\":\"Baumschule\"}",
			serialization);
		assertNotNull(event.occuredOn());
		assertEquals(changedBy, event.triggeringUser());
		assertEquals(wettbewerbsjahr, event.wettbewerbsjahr());
		assertEquals(teilnahmenummer, event.teilnahmenummer());
		assertEquals(schulname, event.schulname());
		assertEquals("SchulteilnahmeChanged", event.typeName());
	}

}
