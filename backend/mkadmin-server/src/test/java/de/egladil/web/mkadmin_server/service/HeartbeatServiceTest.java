// =====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkadmin_server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mkadmin_server.dao.PacemakerDao;
import de.egladil.web.mkadmin_server.domain.Pacemaker;

/**
 * HeartbeatServiceTest
 */
public class HeartbeatServiceTest {

	private PacemakerDao dao;

	private HeartbeatService service;

	private Pacemaker pacemaker;

	@BeforeEach
	public void setUp() {

		dao = Mockito.mock(PacemakerDao.class);
		service = new HeartbeatService(dao);
		pacemaker = new Pacemaker();
		pacemaker.setId(1l);
		pacemaker.setMonitorId("mkverwaltung-database");
		pacemaker.setWert("wert-1558961580334");
	}

	@Test
	void updateSuccess() {

		// Arrange
		Mockito.when(dao.findByUniqueIdentifier("mkverwaltung-database")).thenReturn(Optional.of(pacemaker));
		Mockito.when(dao.save(pacemaker)).thenReturn(pacemaker);

		// Act
		ResponsePayload responsePayload = service.update();

		// Assert
		assertEquals("INFO", responsePayload.getMessage().getLevel());
		assertEquals("mkverwaltung-database lebt", responsePayload.getMessage().getMessage());
		assertNull(responsePayload.getData());

	}

	@Test
	void updateExceptionOnFind() {

		// Arrange
		Mockito.when(dao.findByUniqueIdentifier("mkverwaltung-database")).thenThrow(new RuntimeException("testmessage"));

		// Act
		ResponsePayload responsePayload = service.update();

		// Assert
		assertEquals("ERROR", responsePayload.getMessage().getLevel());
		assertEquals("Fehler beim Speichern des pacemakers mkverwaltung-database: testmessage",
			responsePayload.getMessage().getMessage());
		assertNull(responsePayload.getData());

	}

	@Test
	void updateExceptionOnSave() {

		// Arrange
		Mockito.when(dao.findByUniqueIdentifier("mkverwaltung-database")).thenReturn(Optional.of(pacemaker));
		Mockito.when(dao.save(pacemaker)).thenThrow(new RuntimeException("testmessage"));

		// Act
		ResponsePayload responsePayload = service.update();

		// Assert
		assertEquals("ERROR", responsePayload.getMessage().getLevel());
		assertEquals("Fehler beim Speichern des pacemakers mkverwaltung-database: testmessage",
			responsePayload.getMessage().getMessage());
		assertNull(responsePayload.getData());

	}

}
