// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_kataloge.application.impl.KatalogsucheFacadeImpl;
import de.egladil.web.mk_kataloge.domain.KatalogItem;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * KatalogsucheFacadeImplTest
 */
@QuarkusTest
public class KatalogsucheFacadeImplTest {

	@Inject
	KatalogsucheFacadeImpl facade;

	@Test
	void should_sucheOrteInLandWork() {

		// Act
		List<KatalogItem> treffer = facade.sucheOrteInLandMitNameBeginnendMit("DE-HE", "Wie");

		// Assert
		assertEquals(1, treffer.size());
	}

}
