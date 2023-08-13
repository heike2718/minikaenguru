// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers.SortedTable;
import de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers.SortnumberRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;

/**
 * SortNumberGeneratorImplTest
 */
@QuarkusTest
public class SortNumberGeneratorImplTest {

	@InjectMock
	SortnumberRepository repositoy;

	@Inject
	SortNumberGeneratorImpl generator;

	@Test
	void should_getNextSortnumberLoesungszettelCallRepository_onlyOnce() {

		// Arrange
		when(repositoy.getMaxSortnumber(SortedTable.LOESUNGSZETTEL)).thenReturn(Long.valueOf(13));

		// Act
		long result1 = generator.getNextSortnumberLoesungszettel();
		long result2 = generator.getNextSortnumberLoesungszettel();
		long result3 = generator.getNextSortnumberLoesungszettel();

		// Asssert
		assertEquals(Long.valueOf(14), result1);
		assertEquals(Long.valueOf(15), result2);
		assertEquals(Long.valueOf(16), result3);

		verify(repositoy).getMaxSortnumber(SortedTable.LOESUNGSZETTEL);
	}

	@Test
	void should_getNextSortnumberUploadsCallRepository_onlyOnce() {

		// Arrange
		when(repositoy.getMaxSortnumber(SortedTable.UPLOADS)).thenReturn(Long.valueOf(13));

		// Act
		long result1 = generator.getNextSortnumberUploads();
		long result2 = generator.getNextSortnumberUploads();
		long result3 = generator.getNextSortnumberUploads();

		// Asssert
		assertEquals(Long.valueOf(14), result1);
		assertEquals(Long.valueOf(15), result2);
		assertEquals(Long.valueOf(16), result3);

		verify(repositoy).getMaxSortnumber(SortedTable.UPLOADS);
	}

}
