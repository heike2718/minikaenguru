// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.rest;

import static org.junit.Assert.fail;

import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_wettbewerb.infrastructure.rest.WettbewerbResource;

/**
 * WettbewerbServiceTest
 */
public class WettbewerbServiceTest {

	@Test
	void should_getAktuellenWettbewerbThrowNotFound_when_noWettbewerb() {

		// Arrange
		WettbewerbService wettbewerbService = Mockito.mock(WettbewerbService.class);
		Mockito.when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.empty());

		WettbewerbResource resource = WettbewerbResource.createForTest(wettbewerbService);

		// Act
		try {

			resource.getAktuellenWettbewerb();
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			// nüscht
		}

	}

}
