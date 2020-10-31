// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.event.EventType;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * KindCreatedTest
 */
public class KindCreatedTest {

	@Test
	void should_allAttributesBeInitialized() throws JsonProcessingException {

		// Arrange
		String kindID = "abcde-1234567890";
		String teilnahmenummer = "ZGFR54DE";
		Klassenstufe klassenstufe = Klassenstufe.ZWEI;
		Sprache sprache = Sprache.en;
		String triggeringUser = "uuid-uuid";
		String klasseID = "hdgt-653-djez";

		// Act
		KindCreated eventPayload = (KindCreated) new KindCreated(triggeringUser)
			.withKindID(kindID).withKlasseID(klasseID).withKlassenstufe(klassenstufe)
			.withSprache(sprache)
			.withTeilnahmenummer(teilnahmenummer);

		// Assert
		assertNotNull(eventPayload.occuredOn());
		assertEquals(EventType.KIND_CREATED.getLabel(), eventPayload.typeName());

		String serialized = new ObjectMapper().writeValueAsString(eventPayload);

		// {"kindID":"abcde-1234567890","teilnahmenummer":"ZGFR54DE","klassenstufe":"ZWEI","sprache":"en","triggeringUser":"uuid-uuid","klasseID":"hdgt-653-djez"}
		assertEquals(
			"{\"kindID\":\"abcde-1234567890\",\"teilnahmenummer\":\"ZGFR54DE\",\"klassenstufe\":\"ZWEI\",\"sprache\":\"en\",\"triggeringUser\":\"uuid-uuid\",\"klasseID\":\"hdgt-653-djez\"}",
			serialized);
	}

}
