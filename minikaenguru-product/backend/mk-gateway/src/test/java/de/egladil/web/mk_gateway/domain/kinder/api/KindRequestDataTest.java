// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * KindRequestDataTest
 */
public class KindRequestDataTest {

	@Test
	void should_serializeAsJson() throws JsonProcessingException {

		// Arrange
		String nachname = "Wiener";
		String klasseUuid = "6d57b4f6-61ed-450a-ae17-7ccb3d9d776a";

		KindEditorModel initialesKindEditorModel = new KindEditorModel(Klassenstufe.ZWEI, Sprache.de)
			.withNachname(nachname)
			.withVorname("Norbert").withZusatz("blond")
			.withKlasseUuid(klasseUuid);

		KindRequestData kindRequestData = new KindRequestData().withKind(initialesKindEditorModel)
			.withUuid("neu").withKuerzelLand("DE-ST");

		// Act
		String serialized = new ObjectMapper().writeValueAsString(kindRequestData);

		// System.out.println(serialized);

		// {"uuid":"neu","kuerzelLand":"DE-ST","kind":{"vorname":"Norbert","nachname":"Wiener","zusatz":"blond","klassenstufe":{"klassenstufe":"ZWEI","label":"Klasse
		// 2"},"sprache":{"sprache":"de","label":"deutsch"},"klasseUuid":"6d57b4f6-61ed-450a-ae17-7ccb3d9d776a"}}
		assertEquals(
			"{\"uuid\":\"neu\",\"kuerzelLand\":\"DE-ST\",\"kind\":{\"vorname\":\"Norbert\",\"nachname\":\"Wiener\",\"zusatz\":\"blond\",\"klassenstufe\":{\"klassenstufe\":\"ZWEI\",\"label\":\"Klasse 2\"},\"sprache\":{\"sprache\":\"de\",\"label\":\"deutsch\"},\"klasseUuid\":\"6d57b4f6-61ed-450a-ae17-7ccb3d9d776a\"}}",
			serialized);
	}
}
