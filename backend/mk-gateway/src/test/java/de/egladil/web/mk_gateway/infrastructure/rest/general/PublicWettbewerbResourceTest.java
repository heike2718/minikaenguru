// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import static org.junit.Assert.fail;

import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * PublicWettbewerbResourceTest
 */
public class PublicWettbewerbResourceTest {

	@Test
	void should_getAktuellenWettbewerbThrowNotFound_when_noWettbewerb() {

		// Arrange
		WettbewerbService wettbewerbService = Mockito.mock(WettbewerbService.class);
		Mockito.when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.empty());

		PublicWettbewerbResource resource = PublicWettbewerbResource.createForTest(wettbewerbService);

		// Act
		try {

			resource.getAktuellenWettbewerb();
			fail("keine NotFoundException");
		} catch (NotFoundException e) {

			// nüscht
		}

	}

}
