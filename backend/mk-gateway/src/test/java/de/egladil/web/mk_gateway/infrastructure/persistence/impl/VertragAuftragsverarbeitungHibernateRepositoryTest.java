// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.adv.AdvService;
import de.egladil.web.mk_gateway.domain.adv.PostleitzahlLand;
import de.egladil.web.mk_gateway.domain.adv.VertragAuftragsdatenverarbeitung;
import de.egladil.web.mk_gateway.domain.adv.Vertragstext;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VertragAdvAPIModel;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterVertragAdv;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterVertragAdvText;

/**
 * VertragAuftragsverarbeitungHibernateRepositoryTest
 */
public class VertragAuftragsverarbeitungHibernateRepositoryTest {

	@Test
	void should_mapFromAggregate_initializeAllAttributes() {

		// Arrange
		String schulname = "Hijahsdho";
		String plz = "76545";
		String ort = "Ggagdu";
		String strasse = "Ggu-Htfzu-Straße";
		String hausnummer = "13-15";

		String schulkuerzel = "ASDERS";

		VertragAdvAPIModel apiModel = new VertragAdvAPIModel()
			.withHausnummer(hausnummer)
			.withOrt(ort)
			.withPlz(plz)
			.withSchulkuerzel(schulkuerzel)
			.withSchulname(schulname)
			.withStrasse(strasse);

		Map<String, Object> schuleKatalogeMap = new HashMap<>();

		schuleKatalogeMap.put("kuerzel", schulkuerzel);
		schuleKatalogeMap.put("name", "Schule 98765");
		schuleKatalogeMap.put("ort", ort);
		schuleKatalogeMap.put("land", "Hessen");
		schuleKatalogeMap.put("kuerzelLand", "DE-HE");

		SchuleAPIModel schuleAPIModel = SchuleAPIModel.withAttributes(schuleKatalogeMap);

		Vertragstext vertragstext = new Vertragstext().withIdentifier(new Identifier("gasdgqoug"));

		String lehrerUuid = "GUIguigigzfzfi";
		String unterzeichnetAm = "11.09.2020 17:29:13";

		VertragAuftragsdatenverarbeitung vertrag = new AdvService().initVertrag(apiModel, lehrerUuid,
			new PostleitzahlLand(plz, Optional.of(schuleAPIModel)), vertragstext,
			unterzeichnetAm);

		PersistenterVertragAdvText persistenterVertragstext = new PersistenterVertragAdvText();
		persistenterVertragstext.setUuid("AGDUQGUOG");

		// Act
		PersistenterVertragAdv result = new VertragAuftragsverarbeitungHibernateRepository().mapFromAggregate(vertrag,
			persistenterVertragstext);

		// Assert
		assertEquals(schulkuerzel, result.getSchulkuerzel());
		assertNull(result.getUuid());
		assertEquals(unterzeichnetAm, result.getAbgeschlossenAm());
		assertEquals(lehrerUuid, result.getAbgeschlossenDurch());
		assertEquals(persistenterVertragstext, result.getAdvText());
		assertEquals(hausnummer, result.getHausnummer());
		assertEquals(strasse, result.getStrasse());
		assertEquals(ort, result.getOrt());
		assertEquals(plz, result.getPlz());
		assertEquals(schulname, result.getSchulname());
		assertEquals("DE", result.getLaendercode());

	}

}
