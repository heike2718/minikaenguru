// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.StoredEvent;

/**
 * EventService
 */
@ApplicationScoped
public class EventService {

	private final static String LOG_DATE_TIME_FORMAT = "yyyy-MM-dd kk:mm:ss";

	private final static String FILENAME_DATE_FORMAT = "yyyy-MM-dd";

	@Inject
	EventRepository eventRepository;

	public static EventService createForTests(final EventRepository eventRepository) {

		EventService result = new EventService();
		result.eventRepository = eventRepository;
		return result;
	}

	/**
	 * Exportiert den Inhalt der Tabelle EVENTS, die ab datum aufgetreten sind.
	 *
	 * @return DownloadData
	 */
	public DownloadData exportEventsStartingFromDate(final String dateString) {

		LocalDate datum = parseToLocalDate(dateString);

		List<StoredEvent> events = eventRepository.findEventsAfter(datum.atStartOfDay());

		final StringBuffer sb = new StringBuffer();
		events.forEach(event -> append(sb, event));

		String filename = datum.format(DateTimeFormatter.ofPattern(FILENAME_DATE_FORMAT)) + "-mk-gateway-events.log";

		DownloadData result = new DownloadData(filename, sb.toString().getBytes());
		return result;
	}

	/**
	 * Wandelt einen String mit dem Format 'dd.MM.yyyy' in ein LocalDate um.
	 *
	 * @param  dateString
	 *                    darf nicht null sein. Muss gültiges Datumsformat haben.
	 * @return            LocalDate
	 */
	private LocalDate parseToLocalDate(final String dateString) throws IllegalArgumentException {

		if (StringUtils.isBlank(dateString)) {

			return LocalDate.now(ZoneId.systemDefault());
		}

		try {

			TemporalAccessor ta = DateTimeFormatter.ofPattern(FILENAME_DATE_FORMAT).parse(dateString);
			return LocalDate.from(ta);
		} catch (DateTimeParseException e) {

			throw new IllegalArgumentException(dateString + " ist kein gültiges Datum");
		}

	}

	private void append(final StringBuffer sb, final StoredEvent event) {

		sb.append(StringUtils.rightPad(event.getId().toString(), 7));
		sb.append(event.getOccuredOn().format(DateTimeFormatter.ofPattern(LOG_DATE_TIME_FORMAT)));
		sb.append("  ");
		sb.append(StringUtils.rightPad(event.getName(), 40));
		sb.append(event.getBody());
		sb.append("\n");
	}

}
