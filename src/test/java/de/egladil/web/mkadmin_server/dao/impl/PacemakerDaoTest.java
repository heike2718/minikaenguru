//=====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mkadmin_server.dao.impl;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * PacemakerDaoTest
 */
public class PacemakerDaoTest {

	@Test
	void nameFachlicherSchluesselStimmt() {

		// Act + Assert
		assertEquals("monitorId", new PacemakerDaoImpl().getNameFachlicherSchluessel());
	}

	@Test
	void tableNameStimmt() {

		// Act + Assert
		assertEquals("PACEMAKER", new PacemakerDaoImpl().getTableName());
	}

}
