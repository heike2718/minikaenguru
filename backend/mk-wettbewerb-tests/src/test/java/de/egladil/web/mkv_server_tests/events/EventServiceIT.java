// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.event.EventService;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.StoredEvent;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.EventRepositoryHibernate;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * EventServiceIT
 */
public class EventServiceIT extends AbstractIntegrationTest {

	private EventRepositoryHibernate repo;

	private EventService eventService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		repo = EventRepositoryHibernate.createForIntegrationTest(entityManager);
		eventService = EventService.createForTests(repo);
	}

	@Test
	void should_findAllEventsAfter_work() {

		// Arrange
		LocalDateTime ldt = CommonTimeUtils.parseToLocalDate("01.10.2020").atStartOfDay();

		// Act
		List<StoredEvent> events = repo.findEventsAfter(ldt);

		// Assert
		assertTrue(events.size() > 10);

	}

	@Test
	void should_exportEventsStartingFromDate_work() {

		// Act
		DownloadData downloadData = eventService.exportEventsStartingFromDate("2020-10-01");

		// Assert
		assertEquals("2020-10-01-mk-gateway-events.log", downloadData.filename());

		String data = new String(downloadData.data());
		System.out.println(data);

	}

}
