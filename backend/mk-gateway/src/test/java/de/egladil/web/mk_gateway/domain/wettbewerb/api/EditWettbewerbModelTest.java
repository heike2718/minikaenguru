// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb.api;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons_validation.ValidationDelegate;
import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.EditWettbewerbModel;

/**
 * EditWettbewerbModelTest
 */
public class EditWettbewerbModelTest {

	@Test
	void should_Serialize() throws JsonProcessingException {

		// Arrange
		EditWettbewerbModel model = EditWettbewerbModel.createForTest(2021, "11.11.2020",
			"01.08.2021", "06.03.2021", "01.06.2021");

		// Act
		String serialized = new ObjectMapper().writeValueAsString(model);

		// Assert
		System.out.println(serialized);

		assertEquals(
			"{\"jahr\":2021,\"status\":null,\"wettbewerbsbeginn\":\"11.11.2020\",\"wettbewerbsende\":\"01.08.2021\",\"datumFreischaltungLehrer\":\"06.03.2021\",\"datumFreischaltungPrivat\":\"01.06.2021\",\"loesungsbuchstabenIkids\":null,\"loesungsbuchstabenKlasse1\":null,\"loesungsbuchstabenKlasse2\":null}",
			serialized);

	}

	@Test
	void should_checkThrowException_whenCompletelyInvalid() {

		// Arrange
		EditWettbewerbModel completelyInvalidModel = EditWettbewerbModel.createForTest(0, "11112020",
			"1820", "2802.2021", "01.062021");

		// Act
		try {

			new ValidationDelegate().check(completelyInvalidModel, EditWettbewerbModel.class);
			fail("keine InvalidInputException");
		} catch (InvalidInputException e) {

			ResponsePayload responsePayload = e.getResponsePayload();
			assertEquals("Die Eingaben sind nicht korrekt.", responsePayload.getMessage().getMessage());
		}
	}
}
