//=====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mkadmin_server.domain;

import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import de.egladil.web.mkadmin_server.dao.impl.PacemakerDaoImpl;

/**
 * PacemakerTest
 */
public class PacemakerTest {

	@Test
	void hasFachlicherSchluesselAttribute() {

		// Arrange
		PacemakerDaoImpl dao = new PacemakerDaoImpl();
		String attributName = dao.getNameFachlicherSchluessel();
		String firstLetter = attributName.substring(0, 1);
		String suffix = attributName.substring(1);
		String nameGetter = "get" + firstLetter.toUpperCase() + suffix;

		// Act
		try {
			Pacemaker.class.getMethod(nameGetter);
		} catch (NoSuchMethodException | SecurityException e) {
			fail("Pacemaker muss Attrribut " + attributName + " haben. " + e.getClass().getName() + ": " + e.getMessage());
		}
	}

}
