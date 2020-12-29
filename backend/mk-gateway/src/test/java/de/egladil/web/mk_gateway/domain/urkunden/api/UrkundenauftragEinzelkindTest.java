// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.api;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.urkunden.Farbschema;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenart;

/**
 * UrkundenauftragEinzelkindTest
 */
public class UrkundenauftragEinzelkindTest {

	@Test
	void serialize() throws JsonProcessingException {

		// Arrange
		UrkundenauftragEinzelkind auftrag = new UrkundenauftragEinzelkind()
			.withDateString("14.12.2020")
			.withFarbschema(Farbschema.GREEN)
			.withKindUuid("e0f2467a-6b19-4b37-9d38-0dfc3d327faf")
			.withUrkundenart(Urkundenart.TEILNAHME);

		// Act
		String serialization = new ObjectMapper().writeValueAsString(auftrag);

		// System.out.println(serialization);

		// {"urkundenart":"TEILNAHME","kindUuid":"e0f2467a-6b19-4b37-9d38-0dfc3d327faf","dateString":"14.12.2020","farbschema":"GREEN"}
		assertEquals(
			"{\"urkundenart\":\"TEILNAHME\",\"kindUuid\":\"e0f2467a-6b19-4b37-9d38-0dfc3d327faf\",\"dateString\":\"14.12.2020\",\"farbschema\":\"GREEN\"}",
			serialization);
	}

}
