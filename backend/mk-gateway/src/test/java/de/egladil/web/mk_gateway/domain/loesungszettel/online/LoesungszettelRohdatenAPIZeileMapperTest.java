// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.online;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.loesungszettel.online.LoesungszettelRohdatenAPIZeileMapper;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * LoesungszettelRohdatenAPIZeileMapperTest
 */
public class LoesungszettelRohdatenAPIZeileMapperTest {

	@Test
	void should_NameZeileWork_forIKIDS() {

		// Arrange
		LoesungszettelRohdatenAPIZeileMapper mapper = new LoesungszettelRohdatenAPIZeileMapper(Klassenstufe.IKID);

		// Act + Assert
		assertEquals("A-1", mapper.getNameZeile(0));
		assertEquals("A-2", mapper.getNameZeile(1));

		assertEquals("B-1", mapper.getNameZeile(2));
		assertEquals("B-2", mapper.getNameZeile(3));

		assertEquals("C-1", mapper.getNameZeile(4));
		assertEquals("C-2", mapper.getNameZeile(5));

		for (int i = 6; i < 15; i++) {

			assertNull(mapper.getNameZeile(i));
		}
	}

	@Test
	void should_NameZeileWork_forEINS() {

		// Arrange
		LoesungszettelRohdatenAPIZeileMapper mapper = new LoesungszettelRohdatenAPIZeileMapper(Klassenstufe.EINS);

		// Act + Assert
		assertEquals("A-1", mapper.getNameZeile(0));
		assertEquals("A-2", mapper.getNameZeile(1));
		assertEquals("A-3", mapper.getNameZeile(2));
		assertEquals("A-4", mapper.getNameZeile(3));

		assertEquals("B-1", mapper.getNameZeile(4));
		assertEquals("B-2", mapper.getNameZeile(5));
		assertEquals("B-3", mapper.getNameZeile(6));
		assertEquals("B-4", mapper.getNameZeile(7));

		assertEquals("C-1", mapper.getNameZeile(8));
		assertEquals("C-2", mapper.getNameZeile(9));
		assertEquals("C-3", mapper.getNameZeile(10));
		assertEquals("C-4", mapper.getNameZeile(11));

		for (int i = 12; i < 15; i++) {

			assertNull(mapper.getNameZeile(i));
		}

	}

	@Test
	void should_NameZeileWork_forZWEI() {

		// Arrange
		LoesungszettelRohdatenAPIZeileMapper mapper = new LoesungszettelRohdatenAPIZeileMapper(Klassenstufe.ZWEI);

		// Act + Assert
		assertEquals("A-1", mapper.getNameZeile(0));
		assertEquals("A-2", mapper.getNameZeile(1));
		assertEquals("A-3", mapper.getNameZeile(2));
		assertEquals("A-4", mapper.getNameZeile(3));
		assertEquals("A-5", mapper.getNameZeile(4));

		assertEquals("B-1", mapper.getNameZeile(5));
		assertEquals("B-2", mapper.getNameZeile(6));
		assertEquals("B-3", mapper.getNameZeile(7));
		assertEquals("B-4", mapper.getNameZeile(8));
		assertEquals("B-5", mapper.getNameZeile(9));

		assertEquals("C-1", mapper.getNameZeile(10));
		assertEquals("C-2", mapper.getNameZeile(11));
		assertEquals("C-3", mapper.getNameZeile(12));
		assertEquals("C-4", mapper.getNameZeile(13));
		assertEquals("C-5", mapper.getNameZeile(14));

		assertNull(mapper.getNameZeile(15));

	}

}
